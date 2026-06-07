package ba.etf.rma26

import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class Spirala3PublicIntegrationTest {

    companion object {
        private const val PACKAGE_REPOSITORIES = "ba.etf.rma26.projekat.data.repositories"
        private const val DEFAULT_BASE_URL = "http://10.0.2.2:3000"

        lateinit var baseUrl: String
        private var apiKey: String? = null

        @JvmStatic
        @BeforeClass
        fun initApi() {
            val arguments = InstrumentationRegistry.getArguments()
            baseUrl = arguments.getString("rmaBaseUrl") ?: DEFAULT_BASE_URL
            apiKey = arguments.getString("rmaApiKey")

            if (arguments.getString("rmaReset") == "true") {
                resetBackend()
            }

            invoke("ApiConfig", "postaviBaseURL", baseUrl)
            invoke("ApiConfig", "postaviApiKey", apiKey)
        }

        private fun repository(className: String): Any {
            val fullName = "$PACKAGE_REPOSITORIES.$className"
            val clazz = try {
                Class.forName(fullName).kotlin
            } catch (e: ClassNotFoundException) {
                throw AssertionError("Repozitorij $fullName ne postoji. Provjerite paket i naziv klase.", e)
            }

            return clazz.objectInstance
                ?: throw AssertionError("Repozitorij $className mora biti definisan kao Kotlin 'object', ne kao class.")
        }

        private fun method(className: String, methodName: String, argCount: Int): KFunction<*> {
            val instance = repository(className)
            return instance::class.declaredFunctions
                .filter { it.name == methodName }
                .firstOrNull { it.parameters.size == argCount + 1 }
                ?.also { it.isAccessible = true }
                ?: throw AssertionError(
                    "Metoda $className.$methodName sa $argCount parametara nije pronađena. " +
                            "Provjerite tačan naziv metode i broj parametara iz postavke."
                )
        }

        private fun invoke(className: String, methodName: String, vararg args: Any?): Any? = runBlocking {
            val instance = repository(className)
            val function = method(className, methodName, args.size)

            try {
                if (function.isSuspend) {
                    function.callSuspend(instance, *args)
                } else {
                    function.call(instance, *args)
                }
            } catch (e: Throwable) {
                val root = e.cause ?: e
                throw AssertionError(
                    "Poziv $className.$methodName(${args.joinToString()}) nije uspio. " +
                            "Poruka greške: ${root.message ?: root::class.simpleName}",
                    root
                )
            }
        }

        private fun assertSuspend(className: String, methodName: String, argCount: Int) {
            val function = method(className, methodName, argCount)
            assertTrue(
                "Metoda $className.$methodName mora biti suspend funkcija prema postavci Spirale 3.",
                function.isSuspend
            )
        }

        private fun assertRegularFunction(className: String, methodName: String, argCount: Int) {
            val function = method(className, methodName, argCount)
            assertFalse(
                "Metoda $className.$methodName ne treba biti suspend funkcija prema postavci.",
                function.isSuspend
            )
        }

        private fun listResult(value: Any?, callName: String): List<*> {
            assertNotNull("$callName ne smije vratiti null.", value)
            assertTrue("$callName mora vratiti List, ali je vraćeno: ${value!!::class.qualifiedName}", value is List<*>)
            return value as List<*>
        }

        @Suppress("UNCHECKED_CAST")
        private fun propertyValue(obj: Any?, propertyName: String): Any? {
            assertNotNull("Model je null, pa nije moguće pročitati polje '$propertyName'.", obj)

            val properties = obj!!::class.memberProperties
            val property = properties.find { it.name == propertyName } as? KProperty1<Any, *>
                ?: throw AssertionError(
                    "Model ${obj::class.simpleName} mora imati polje '$propertyName'. " +
                            "Pronađena polja: ${properties.map { it.name }.sorted()}"
                )

            property.isAccessible = true
            return property.get(obj)
        }

        private fun intProperty(obj: Any?, propertyName: String): Int {
            val value = propertyValue(obj, propertyName)
            assertTrue(
                "Polje '$propertyName' mora biti broj, ali je vrijednost: $value (${value?.let { it::class.simpleName }}).",
                value is Number
            )
            return (value as Number).toInt()
        }

        private fun stringProperty(obj: Any?, propertyName: String): String {
            val value = propertyValue(obj, propertyName)
            assertTrue(
                "Polje '$propertyName' mora biti String, ali je vrijednost: $value (${value?.let { it::class.simpleName }}).",
                value is String
            )
            return value as String
        }

        private fun setHash(hash: String) {
            val result = invoke("AccountRepository", "postaviHash", hash)
            assertEquals("AccountRepository.postaviHash mora vratiti true za validan hash.", true, result)
        }

        private fun uniqueHash(label: String): String {
            return "test-$label-${UUID.randomUUID().toString().take(8)}"
        }

        private fun firstGroupId(): Int {
            val groups = listResult(invoke("PredmetIGrupaRepository", "getGrupe"), "getGrupe()")
            assertTrue("Backend mora vratiti barem jednu grupu za ove testove.", groups.isNotEmpty())
            return intProperty(groups.first(), "id")
        }

        private fun quizWithQuestion(): Pair<Int, Int> {
            val quizzes = listResult(invoke("KvizRepository", "getAll"), "KvizRepository.getAll()")
            assertTrue("Backend mora vratiti barem jedan kviz.", quizzes.isNotEmpty())

            for (quiz in quizzes) {
                val quizId = intProperty(quiz, "id")
                val questions = listResult(
                    invoke("PitanjeKvizRepository", "getPitanja", quizId),
                    "PitanjeKvizRepository.getPitanja($quizId)"
                )
                if (questions.isNotEmpty()) {
                    return quizId to intProperty(questions.first(), "id")
                }
            }

            throw AssertionError("Backend nema nijedan kviz sa pitanjima, pa nije moguće testirati pokušaj i odgovore.")
        }

        private fun resetBackend() {
            val connection = URL("${baseUrl.trimEnd('/')}/reset").openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.connectTimeout = 3000
            connection.readTimeout = 3000
            apiKey?.let { connection.setRequestProperty("X-API-Key", it) }

            val status = connection.responseCode
            connection.disconnect()

            assertTrue(
                "rmaReset=true je postavljen, ali backend nije dozvolio reset. " +
                        "Pokrenite backend sa ALLOW_RESET=true. HTTP status: $status",
                status in 200..299
            )
        }
        private fun backendBody(path: String): String {
            val connection = URL("${baseUrl.trimEnd('/')}$path").openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 3000
            connection.readTimeout = 3000

            val status = connection.responseCode
            val body = if (status in 200..299) {
                connection.inputStream.bufferedReader().readText()
            } else {
                connection.errorStream?.bufferedReader()?.readText().orEmpty()
            }
            connection.disconnect()

            assertTrue(
                "Backend GET $path nije uspio. HTTP status: $status, odgovor: $body",
                status in 200..299
            )

            return body
        }

        private fun firstBackendQuizWithQuestion(): Pair<Int, Int> {
            val quizBody = backendBody("/kviz")

            val quizIds = Regex("\"id\"\\s*:\\s*(\\d+)")
                .findAll(quizBody)
                .map { it.groupValues[1].toInt() }
                .toList()

            assertTrue("Backend /kviz mora vratiti barem jedan kviz.", quizIds.isNotEmpty())

            for (quizId in quizIds) {
                val questionBody = backendBody("/kviz/$quizId/pitanja")

                val questionId = Regex("\"id\"\\s*:\\s*(\\d+)")
                    .find(questionBody)
                    ?.groupValues
                    ?.get(1)
                    ?.toInt()

                if (questionId != null) {
                    return quizId to questionId
                }
            }

            throw AssertionError("Backend nema nijedan kviz sa pitanjima.")
        }
    }

    @Test
    fun test01_VerifyRepositorySignatures() {
        assertRegularFunction("ApiConfig", "postaviBaseURL", 1)
        assertRegularFunction("ApiConfig", "postaviApiKey", 1)

        assertSuspend("AccountRepository", "postaviHash", 1)
        assertSuspend("AccountRepository", "getHash", 0)

        assertSuspend("KvizRepository", "getAll", 0)
        assertSuspend("KvizRepository", "getById", 1)
        assertSuspend("KvizRepository", "getUpisani", 0)

        assertSuspend("PredmetIGrupaRepository", "getPredmeti", 0)
        assertSuspend("PredmetIGrupaRepository", "getGrupe", 0)
        assertSuspend("PredmetIGrupaRepository", "getGrupeZaPredmet", 1)
        assertSuspend("PredmetIGrupaRepository", "upisiUGrupu", 1)
        assertSuspend("PredmetIGrupaRepository", "getUpisaneGrupe", 0)

        assertSuspend("PitanjeKvizRepository", "getPitanja", 1)

        assertSuspend("TakeKvizRepository", "zapocniKviz", 1)
        assertSuspend("TakeKvizRepository", "getPocetiKvizovi", 0)

        assertSuspend("OdgovorRepository", "getOdgovoriKviz", 1)
        assertSuspend("OdgovorRepository", "postaviOdgovorKviz", 3)
    }

    @Test
    fun test02_AccountRepository_StatePersistence() {
        val hash = uniqueHash("account")

        assertEquals(true, invoke("AccountRepository", "postaviHash", hash))
        assertEquals("getHash mora vratiti isti hash koji je prethodno postavljen.", hash, invoke("AccountRepository", "getHash"))

        val blankResult = invoke("AccountRepository", "postaviHash", "   ")
        assertEquals("postaviHash mora vratiti false za prazan ili whitespace-only hash.", false, blankResult)
        assertEquals(
            "Nevalidan hash ne smije zamijeniti prethodno validno postavljeni hash.",
            hash,
            invoke("AccountRepository", "getHash")
        )
    }

    @Test
    fun test03_PredmetIGrupaRepository_FetchData() {
        val predmeti = listResult(invoke("PredmetIGrupaRepository", "getPredmeti"), "getPredmeti()")
        assertTrue("getPredmeti() mora vratiti barem jedan predmet sa backend servisa.", predmeti.isNotEmpty())
        intProperty(predmeti.first(), "id")
        stringProperty(predmeti.first(), "naziv")
        intProperty(predmeti.first(), "godina")

        val grupe = listResult(invoke("PredmetIGrupaRepository", "getGrupe"), "getGrupe()")
        assertTrue("getGrupe() mora vratiti barem jednu grupu sa backend servisa.", grupe.isNotEmpty())
        intProperty(grupe.first(), "id")
        stringProperty(grupe.first(), "naziv")
        intProperty(grupe.first(), "idPredmeta")
    }

    @Test
    fun test04_PredmetIGrupaRepository_GetGrupeZaPredmet() {
        val sveGrupe = listResult(invoke("PredmetIGrupaRepository", "getGrupe"), "getGrupe()")
        assertTrue("Za ovaj test backend mora imati barem jednu grupu.", sveGrupe.isNotEmpty())

        val idPredmeta = intProperty(sveGrupe.first(), "idPredmeta")
        val grupeZaPredmet = listResult(
            invoke("PredmetIGrupaRepository", "getGrupeZaPredmet", idPredmeta),
            "getGrupeZaPredmet($idPredmeta)"
        )

        assertTrue("getGrupeZaPredmet($idPredmeta) mora vratiti barem jednu grupu.", grupeZaPredmet.isNotEmpty())
        assertTrue(
            "Sve grupe vraćene iz getGrupeZaPredmet($idPredmeta) moraju imati idPredmeta=$idPredmeta.",
            grupeZaPredmet.all { intProperty(it, "idPredmeta") == idPredmeta }
        )

        val nepostojeceGrupe = listResult(
            invoke("PredmetIGrupaRepository", "getGrupeZaPredmet", -999),
            "getGrupeZaPredmet(-999)"
        )
        assertTrue("Za nepostojeći predmet metoda treba vratiti praznu listu.", nepostojeceGrupe.isEmpty())
    }

    @Test
    fun test05_KvizRepository_GetMethods() {
        setHash("demo")

        val sviKvizovi = listResult(invoke("KvizRepository", "getAll"), "KvizRepository.getAll()")
        assertTrue("KvizRepository.getAll() mora vratiti barem jedan kviz.", sviKvizovi.isNotEmpty())

        val prviKvizId = intProperty(sviKvizovi.first(), "id")
        stringProperty(sviKvizovi.first(), "naziv")

        val pronadjeniKviz = invoke("KvizRepository", "getById", prviKvizId)
        assertNotNull("getById($prviKvizId) mora vratiti kviz koji postoji.", pronadjeniKviz)
        assertEquals("getById mora vratiti kviz sa traženim id-em.", prviKvizId, intProperty(pronadjeniKviz, "id"))

        val upisaniKvizovi = listResult(invoke("KvizRepository", "getUpisani"), "KvizRepository.getUpisani()")
        assertTrue("Za demo hash backend ima upisane grupe, pa getUpisani() treba vratiti kvizove.", upisaniKvizovi.isNotEmpty())
    }

    @Test
    fun test06_KvizRepository_GetById_NotFound() {
        val nepostojeciKviz = invoke("KvizRepository", "getById", -999)
        assertNull("getById mora vratiti null za nepostojeći ID kviza.", nepostojeciKviz)
    }

    @Test
    fun test07_EnrollmentWorkflow_Verification() {
        val hash = uniqueHash("enroll")
        setHash(hash)

        val upisanePrije = listResult(
            invoke("PredmetIGrupaRepository", "getUpisaneGrupe"),
            "getUpisaneGrupe() prije upisa"
        )
        assertTrue(
            "Novi student sa hashom '$hash' ne treba imati upisane grupe prije poziva upisiUGrupu.",
            upisanePrije.isEmpty()
        )

        val idGrupe = firstGroupId()
        val upisUspio = invoke("PredmetIGrupaRepository", "upisiUGrupu", idGrupe)
        assertEquals("upisiUGrupu($idGrupe) mora vratiti true za postojeću grupu.", true, upisUspio)

        val upisNepostojece = invoke("PredmetIGrupaRepository", "upisiUGrupu", -999)
        assertEquals("upisiUGrupu mora vratiti false za nepostojeću grupu.", false, upisNepostojece)

        val upisanePoslije = listResult(
            invoke("PredmetIGrupaRepository", "getUpisaneGrupe"),
            "getUpisaneGrupe() nakon upisa"
        )

        assertTrue(
            "Nakon upisa, getUpisaneGrupe() mora sadržavati grupu id=$idGrupe. " +
                    "Provjerite da upisiUGrupu koristi trenutno postavljeni hash iz AccountRepository.",
            upisanePoslije.any { intProperty(it, "id") == idGrupe }
        )
    }

    @Test
    fun test08_PitanjeKvizRepository_Fetch() {
        val (idKviza, _) = quizWithQuestion()
        val pitanja = listResult(
            invoke("PitanjeKvizRepository", "getPitanja", idKviza),
            "PitanjeKvizRepository.getPitanja($idKviza)"
        )

        assertTrue("Kviz id=$idKviza mora imati pitanja u testnom backendu.", pitanja.isNotEmpty())
        assertTrue(
            "Svako pitanje vraćeno za kviz id=$idKviza mora imati idKviza=$idKviza.",
            pitanja.all { intProperty(it, "idKviza") == idKviza }
        )
        intProperty(pitanja.first(), "id")
        stringProperty(pitanja.first(), "tekstPitanja")
    }

    @Test
    fun test09_TakeKvizRepository_MutatesStartedQuizState() {
        val hash = uniqueHash("take")
        setHash(hash)

        val prije = invoke("TakeKvizRepository", "getPocetiKvizovi")
        assertTrue(
            "Novi student ne treba imati započete kvizove prije poziva zapocniKviz. " +
                    "Metoda može vratiti null ili praznu listu.",
            prije == null || (prije is List<*> && prije.isEmpty())
        )

        val (idKviza, _) = firstBackendQuizWithQuestion()

        val pokusaj = invoke("TakeKvizRepository", "zapocniKviz", idKviza)
        assertNotNull("zapocniKviz($idKviza) ne smije vratiti null za postojeći kviz.", pokusaj)

        val idPokusaja = intProperty(pokusaj, "id")
        assertEquals(
            "KvizTaken.idKviza mora odgovarati kvizu koji je započet.",
            idKviza,
            intProperty(pokusaj, "idKviza")
        )

        val poslije = listResult(
            invoke("TakeKvizRepository", "getPocetiKvizovi"),
            "getPocetiKvizovi() nakon započinjanja kviza"
        )
        assertTrue(
            "Nakon zapocniKviz($idKviza), getPocetiKvizovi() mora sadržavati novi pokušaj id=$idPokusaja.",
            poslije.any { intProperty(it, "id") == idPokusaja }
        )

        val drugiPoziv = invoke("TakeKvizRepository", "zapocniKviz", idKviza)
        assertNotNull("Ponovni poziv zapocniKviz($idKviza) treba vratiti postojeći pokušaj.", drugiPoziv)
        assertEquals(
            "Ponovni poziv zapocniKviz za isti hash i kviz ne treba kreirati novi pokušaj.",
            idPokusaja,
            intProperty(drugiPoziv, "id")
        )

        val nepostojeci = invoke("TakeKvizRepository", "zapocniKviz", -999)
        assertNull(
            "zapocniKviz mora vratiti null za nepostojeći kviz.",
            nepostojeci
        )
    }

    @Test
    fun test10_OdgovorRepository_MutatesAnswersAndPreservesValidStateOnInvalidInput() {
        val hash = uniqueHash("answer")
        setHash(hash)

        val (idKviza, idPitanja) = firstBackendQuizWithQuestion()

        val pokusaj = invoke("TakeKvizRepository", "zapocniKviz", idKviza)
        assertNotNull("Prije slanja odgovora mora se uspješno započeti kviz.", pokusaj)

        val idPokusaja = intProperty(pokusaj, "id")

        val odgovoriPrije = listResult(
            invoke("OdgovorRepository", "getOdgovoriKviz", idKviza),
            "getOdgovoriKviz($idKviza) prije slanja odgovora"
        )
        assertTrue(
            "Novi pokušaj ne treba imati odgovore prije slanja prvog odgovora.",
            odgovoriPrije.isEmpty()
        )

        val bodoviNakonValidnog = invoke(
            "OdgovorRepository",
            "postaviOdgovorKviz",
            idPokusaja,
            idPitanja,
            0
        )
        assertTrue(
            "postaviOdgovorKviz mora vratiti broj bodova >= 0 za validan odgovor.",
            bodoviNakonValidnog is Number && bodoviNakonValidnog.toInt() >= 0
        )

        val odgovoriNakonValidnog = listResult(
            invoke("OdgovorRepository", "getOdgovoriKviz", idKviza),
            "getOdgovoriKviz($idKviza) nakon validnog odgovora"
        )
        assertTrue(
            "Nakon validnog odgovora, getOdgovoriKviz mora vratiti odgovor za pitanje id=$idPitanja.",
            odgovoriNakonValidnog.any {
                intProperty(it, "idKvizTaken") == idPokusaja &&
                        intProperty(it, "idPitanje") == idPitanja &&
                        intProperty(it, "odgovor") == 0
            }
        )

        val rezultatNevalidnog = invoke(
            "OdgovorRepository",
            "postaviOdgovorKviz",
            idPokusaja,
            idPitanja,
            999
        )
        assertEquals(
            "Nevalidan indeks odgovora mora vratiti -1.",
            -1,
            (rezultatNevalidnog as Number).toInt()
        )

        val odgovoriNakonNevalidnog = listResult(
            invoke("OdgovorRepository", "getOdgovoriKviz", idKviza),
            "getOdgovoriKviz($idKviza) nakon nevalidnog odgovora"
        )
        assertTrue(
            "Nevalidan odgovor ne smije obrisati ili zamijeniti prethodno validno spremljen odgovor.",
            odgovoriNakonNevalidnog.any {
                intProperty(it, "idKvizTaken") == idPokusaja &&
                        intProperty(it, "idPitanje") == idPitanja &&
                        intProperty(it, "odgovor") == 0
            }
        )
    }
}