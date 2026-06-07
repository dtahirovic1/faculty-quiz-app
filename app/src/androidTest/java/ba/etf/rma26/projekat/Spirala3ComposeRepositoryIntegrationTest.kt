package ba.etf.rma26

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import ba.etf.rma26.projekat.MainActivity
import ba.etf.rma26.projekat.data.repositories.AccountRepository
import ba.etf.rma26.projekat.data.repositories.ApiConfig
import ba.etf.rma26.projekat.data.repositories.KvizRepository
import ba.etf.rma26.projekat.data.repositories.PredmetIGrupaRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class Spirala3ComposeRepositoryIntegrationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var baseUrl: String
    private lateinit var hash: String
    private var apiKey: String? = null

    @Before
    fun setup() = runBlocking {
        val arguments = InstrumentationRegistry.getArguments()
        baseUrl = arguments.getString("rmaBaseUrl") ?: "http://10.0.2.2:3000"
        val apiKey = arguments.getString("rmaApiKey")

        assertBackendIsRunning()
        ApiConfig.postaviBaseURL(baseUrl)
        ApiConfig.postaviApiKey(apiKey)

        hash = "compose-${UUID.randomUUID().toString().take(8)}"
        AccountRepository.postaviHash(hash)

        assertTrue(
            "Novi hash '$hash' ne treba imati upisane grupe prije UI upisa.",
            PredmetIGrupaRepository.getUpisaneGrupe().isEmpty()
        )

        assertTrue(
            "Novi hash '$hash' ne treba imati upisane kvizove prije UI upisa.",
            KvizRepository.getUpisani().isEmpty()
        )
    }

    @Test
    fun uiEnrollmentMutatesRepositoryStateAndCounter() = runBlocking {
        waitForTag("odabirGodina")
        waitForTag("odabirPredmet")
        waitForTag("odabirGrupa")
        waitForTag("dodajPredmetDugme")
        waitForTag("brojKvizova")

        val upisaniKvizoviPrije = KvizRepository.getUpisani()

        val counterBeforeText = textForTag("brojKvizova")
        val counterBefore = firstNumberInText(counterBeforeText)

        assertTrue(
            "brojKvizova mora biti 0 prije upisa jer na početku student nije upisan niti na jedan predmet i nema kvizova, a kod vas je '$counterBefore'.",
            counterBefore == 0
        )

        val selection = selectFirstAvailableEnrollmentCombination()

        composeRule.waitUntil(timeoutMillis = 10_000) {
            runCatching {
                composeRule.onNodeWithTag("dodajPredmetDugme").assertIsEnabled()
                true
            }.getOrDefault(false)
        }

        composeRule.onNodeWithTag("dodajPredmetDugme").performClick()

        composeRule.waitUntil(timeoutMillis = 10_000) {
            runBlocking {
                PredmetIGrupaRepository.getUpisaneGrupe()
                    .any { stringProperty(it, "naziv") == selection.nazivGrupe }
            }
        }

        val upisaneGrupeNakon = PredmetIGrupaRepository.getUpisaneGrupe()
        assertTrue(
            "Nakon klika na dugme 'Upiši me', repozitorij mora vratiti upisanu grupu '${selection.nazivGrupe}'. " +
                    "Ovo provjerava da UI poziva PredmetIGrupaRepository.upisiUGrupu.",
            upisaneGrupeNakon.any { stringProperty(it, "naziv") == selection.nazivGrupe }
        )

        val upisaniKvizoviNakon = KvizRepository.getUpisani()
        assertTrue(
            "Nakon UI upisa, KvizRepository.getUpisani() mora vratiti kvizove za novu grupu.",
            upisaniKvizoviNakon.isNotEmpty()
        )
        assertTrue(
            "Nakon upisa repozitorij mora vratiti za getUpisani veći broj nego na početku test",
            upisaniKvizoviPrije.count()<upisaniKvizoviNakon.count()
        )

        composeRule.waitUntil(timeoutMillis = 10_000,conditionDescription = "Broj kvizova se ni nakon 10 sekudni nije ažurirao na korisničkom interfejsu! ") {
            val current = firstNumberInText(textForTag("brojKvizova"))
            current != null && current != counterBefore
        }

        val counterAfterText = textForTag("brojKvizova")
        val counterAfter = firstNumberInText(counterAfterText)
        assertTrue(
            "brojKvizova mora sadržavati broj kvizova. Prije: '$counterBeforeText', poslije: '$counterAfterText'.",
            counterAfter != null
        )

        assertTrue(
            "Broj prikazan u brojKvizova mora se promijeniti nakon upisa. " +
                    "Prije: '$counterBeforeText', poslije: '$counterAfterText'. " +
                    "Ako repozitorij jeste promijenjen, ali broj nije, UI ne osvježava state nakon upisa.",
            counterBefore != counterAfter
        )
    }

    private data class EnrollmentSelection(
        val godina: String,
        val nazivPredmeta: String,
        val nazivGrupe: String
    )

    private fun selectFirstAvailableEnrollmentCombination(): EnrollmentSelection {
        val predmeti = runBlocking { PredmetIGrupaRepository.getPredmeti() }
        assertTrue("Backend/repozitorij mora vratiti barem jedan predmet.", predmeti.isNotEmpty())

        val predmetiPoGodinama = predmeti
            .map {
                Triple(
                    intProperty(it, "godina").toString(),
                    intProperty(it, "id"),
                    stringProperty(it, "naziv")
                )
            }
            .groupBy { it.first }

        for (godina in listOf("2", "3", "4", "5")) {
            if (!selectDropdownOptionIfVisible("odabirGodina", godina)) continue

            val kandidati = predmetiPoGodinama[godina].orEmpty()
            for ((_, idPredmeta, nazivPredmeta) in kandidati) {
                if (!selectDropdownOptionIfVisible("odabirPredmet", nazivPredmeta)) continue

                val grupe = runBlocking { PredmetIGrupaRepository.getGrupeZaPredmet(idPredmeta) }
                for (grupa in grupe) {
                    val nazivGrupe = stringProperty(grupa, "naziv")
                    if (selectDropdownOptionIfVisible("odabirGrupa", nazivGrupe)) {
                        return EnrollmentSelection(godina, nazivPredmeta, nazivGrupe)
                    }
                }
            }
        }

        throw AssertionError(
            "Nije pronađena kombinacija godina/predmet/grupa koju UI nudi za upis. " +
                    "Provjerite da odabirGodina, odabirPredmet i odabirGrupa prikazuju podatke iz repozitorija."
        )
    }

    private fun selectDropdownOptionIfVisible(tag: String, optionText: String): Boolean {
        composeRule.onNodeWithTag(tag).performClick()

        val found = runCatching {
            composeRule.waitUntil(timeoutMillis = 1500) {
                composeRule
                    .onAllNodesWithText(optionText, substring = false)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
            true
        }.getOrDefault(false)

        if (!found) {
            composeRule.onNodeWithTag(tag).performClick()
            return false
        }

        composeRule
            .onAllNodesWithText(optionText, substring = false)
            .onFirst()
            .performClick()

        return true
    }

    private fun assertBackendIsRunning() {
        val connection = URL("${baseUrl.trimEnd('/')}/predmet").openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 3000
        connection.readTimeout = 3000

        val status = try {
            connection.responseCode
        } finally {
            connection.disconnect()
        }

        assertTrue(
            "Backend nije dostupan na $baseUrl. Pokrenite rma26-backend prije testiranja.",
            status in 200..299
        )
    }

    private fun waitForTag(tag: String) {
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun textForTag(tag: String): String {
        val node = composeRule.onNodeWithTag(tag).fetchSemanticsNode()
        return node.config
            .getOrNull(SemanticsProperties.Text)
            ?.joinToString("") { it.text }
            .orEmpty()
    }

    @Suppress("UNCHECKED_CAST")
    private fun propertyValue(obj: Any?, propertyName: String): Any? {
        assertTrue("Model je null, pa nije moguće pročitati polje '$propertyName'.", obj != null)

        val property = obj!!::class.memberProperties
            .find { it.name == propertyName } as? KProperty1<Any, *>
            ?: throw AssertionError(
                "Model ${obj::class.simpleName} mora imati polje '$propertyName'."
            )

        return property.get(obj)
    }

    private fun intProperty(obj: Any?, propertyName: String): Int {
        val value = propertyValue(obj, propertyName)
        assertTrue("Polje '$propertyName' mora biti broj.", value is Number)
        return (value as Number).toInt()
    }

    private fun stringProperty(obj: Any?, propertyName: String): String {
        val value = propertyValue(obj, propertyName)
        assertTrue("Polje '$propertyName' mora biti String.", value is String)
        return value as String
    }
    private fun firstNumberInText(text: String): Int? {
        return Regex("""\d+""").find(text)?.value?.toIntOrNull()
    }
}