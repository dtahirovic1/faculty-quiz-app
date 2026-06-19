package ba.etf.rma26.projekat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ba.etf.rma26.projekat.data.models.Kviz
import ba.etf.rma26.projekat.data.models.KvizStatus
import ba.etf.rma26.projekat.data.models.KvizTaken
import ba.etf.rma26.projekat.data.models.Pitanje
import ba.etf.rma26.projekat.data.repositories.KvizRepository
import ba.etf.rma26.projekat.data.repositories.OdgovorRepository
import ba.etf.rma26.projekat.data.repositories.PitanjeKvizRepository
import ba.etf.rma26.projekat.data.repositories.PredmetIGrupaRepository
import ba.etf.rma26.projekat.data.repositories.TakeKvizRepository
import ba.etf.rma26.projekat.util.AppDateProvider
import ba.etf.rma26.projekat.util.KvizStatusCalculator
import ba.etf.rma26.projekat.util.Messages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KvizDetaljiViewModel : ViewModel() {

    private val _kviz = MutableStateFlow<Kviz?>(null)
    val kviz: StateFlow<Kviz?> = _kviz.asStateFlow()

    private val _status = MutableStateFlow<KvizStatus?>(null)
    val status: StateFlow<KvizStatus?> = _status.asStateFlow()

    private val _pitanja = MutableStateFlow<List<Pitanje>>(emptyList())
    val pitanja: StateFlow<List<Pitanje>> = _pitanja.asStateFlow()

    private val _pokusaj = MutableStateFlow<KvizTaken?>(null)
    val pokusaj: StateFlow<KvizTaken?> = _pokusaj.asStateFlow()

    private val _imaZapocetPokusaj = MutableStateFlow(false)
    val imaZapocetPokusaj: StateFlow<Boolean> = _imaZapocetPokusaj.asStateFlow()

    private val _odabraniOdgovori = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val odabraniOdgovori: StateFlow<Map<Int, Int>> = _odabraniOdgovori.asStateFlow()

    private val _kvizZavrsen = MutableStateFlow(false)
    val kvizZavrsen: StateFlow<Boolean> = _kvizZavrsen.asStateFlow()

    private val _korisnikUpisan = MutableStateFlow(false)
    val korisnikUpisan: StateFlow<Boolean> = _korisnikUpisan.asStateFlow()

    private val _provjeraUpisaZavrsena = MutableStateFlow(false)
    val provjeraUpisaZavrsena: StateFlow<Boolean> = _provjeraUpisaZavrsena.asStateFlow()

    private val _rezultat = MutableStateFlow<Int?>(null)
    val rezultat: StateFlow<Int?> = _rezultat.asStateFlow()

    private val _poruka = MutableStateFlow<String?>(null)
    val poruka: StateFlow<String?> = _poruka.asStateFlow()

    private var idTrenutnogKviza: Int? = null
    private var pokusajZaNastavak: KvizTaken? = null

    fun ucitaj(idKviza: Int) {
        viewModelScope.launch {
            idTrenutnogKviza = idKviza
            pokusajZaNastavak = null
            _poruka.value = null
            _kviz.value = null
            _status.value = null
            _pitanja.value = emptyList()
            _odabraniOdgovori.value = emptyMap()
            _kvizZavrsen.value = false
            _korisnikUpisan.value = false
            _provjeraUpisaZavrsena.value = false
            _rezultat.value = null
            _pokusaj.value = null
            _imaZapocetPokusaj.value = false

            val ucitaniKviz = KvizRepository.getById(idKviza)

            if (ucitaniKviz == null) {
                _provjeraUpisaZavrsena.value = true
                _poruka.value = Messages.KVIZ_NE_POSTOJI
                return@launch
            }

            _kviz.value = ucitaniKviz

            val upisaneGrupe = PredmetIGrupaRepository.getUpisaneGrupe()
            val upisanNaGrupuKviza = upisaneGrupe.any { grupa ->
                grupa.id == ucitaniKviz.idGrupe &&
                        grupa.idPredmeta == ucitaniKviz.idPredmeta
            }

            _korisnikUpisan.value = upisanNaGrupuKviza
            _provjeraUpisaZavrsena.value = true

            if (!upisanNaGrupuKviza) {
                _poruka.value = Messages.POGRESNA_GRUPA
                return@launch
            }

            val pokusaji = TakeKvizRepository.getPocetiKvizovi() ?: emptyList()
            val pokusajZaKviz = pokusaji.firstOrNull { it.idKviza == idKviza }
            val izracunatiStatus = KvizStatusCalculator.izracunaj(
                kviz = ucitaniKviz,
                pokusaj = pokusajZaKviz,
                referentniDatum = AppDateProvider.danas()
            )

            _status.value = izracunatiStatus

            if (izracunatiStatus != KvizStatus.ZELENA) {
                _kvizZavrsen.value = izracunatiStatus == KvizStatus.PLAVA
                _poruka.value = Messages.porukaZaStatus(izracunatiStatus)
                return@launch
            }

            if (pokusajZaKviz?.zavrsen == false) {
                pokusajZaNastavak = pokusajZaKviz
                _imaZapocetPokusaj.value = true
            }
        }
    }

    fun zapocniKviz() {
        val idKviza = idTrenutnogKviza ?: return
        val trenutniStatus = _status.value ?: return

        if (!_korisnikUpisan.value) {
            _poruka.value = Messages.POGRESNA_GRUPA
            return
        }

        if (trenutniStatus != KvizStatus.ZELENA) {
            _poruka.value = Messages.porukaZaStatus(trenutniStatus)
            return
        }

        viewModelScope.launch {
            _poruka.value = null

            val aktivniPokusaj = pokusajZaNastavak ?: TakeKvizRepository.zapocniKviz(idKviza)

            if (aktivniPokusaj == null || aktivniPokusaj.zavrsen) {
                _kvizZavrsen.value = aktivniPokusaj?.zavrsen == true
                _status.value = if (aktivniPokusaj?.zavrsen == true) KvizStatus.PLAVA else _status.value
                _poruka.value = if (aktivniPokusaj?.zavrsen == true) {
                    Messages.KVIZ_VEC_URADEN
                } else {
                    Messages.KVIZ_NE_MOZE_ZAPOCETI
                }
                return@launch
            }

            _pokusaj.value = aktivniPokusaj
            _pitanja.value = PitanjeKvizRepository.getPitanja(idKviza)

            val spremljeniOdgovori = OdgovorRepository.getOdgovoriKviz(idKviza)
            _odabraniOdgovori.value = spremljeniOdgovori.associate {
                it.idPitanje to it.odgovor
            }
        }
    }

    fun odgovori(idPitanja: Int, odgovor: Int) {
        val trenutniPokusaj = _pokusaj.value ?: return

        if (trenutniPokusaj.zavrsen || _kvizZavrsen.value) {
            _poruka.value = Messages.KVIZ_VEC_URADEN
            return
        }

        val pitanje = _pitanja.value.firstOrNull { it.id == idPitanja } ?: return

        if (odgovor !in pitanje.opcije.indices) {
            _poruka.value = Messages.ODGOVOR_NIJE_POSLAN
            return
        }

        _poruka.value = null
        _odabraniOdgovori.value = _odabraniOdgovori.value + (idPitanja to odgovor)
    }

    fun zavrsiKviz() {
        val trenutniPokusaj = _pokusaj.value ?: return
        val pitanjaZaKviz = _pitanja.value

        if (trenutniPokusaj.zavrsen || _kvizZavrsen.value) {
            _poruka.value = Messages.KVIZ_VEC_URADEN
            return
        }

        if (pitanjaZaKviz.isEmpty()) {
            _poruka.value = Messages.NEMA_PITANJA
            return
        }

        if (_odabraniOdgovori.value.size < pitanjaZaKviz.size) {
            _poruka.value = Messages.ODGOVORITE_NA_SVA_PITANJA
            return
        }

        viewModelScope.launch {
            val bodovi = OdgovorRepository.postaviOdgovoreKviz(
                idKvizTaken = trenutniPokusaj.id,
                odgovori = _odabraniOdgovori.value
            )

            if (bodovi == -1) {
                _poruka.value = Messages.ODGOVOR_NIJE_POSLAN
                return@launch
            }

            val zavrseniPokusaj = TakeKvizRepository.zavrsiKviz(trenutniPokusaj)

            if (zavrseniPokusaj == null) {
                _poruka.value = Messages.KVIZ_NIJE_ZAVRSEN
                return@launch
            }

            val finalniPokusaj = zavrseniPokusaj.copy(
                zavrsen = true,
                osvojenBodovi = bodovi
            )
            _rezultat.value = bodovi

            pokusajZaNastavak = null
            _pokusaj.value = finalniPokusaj
            _pitanja.value = emptyList()
            _odabraniOdgovori.value = emptyMap()
            _imaZapocetPokusaj.value = false
            _kvizZavrsen.value = true
            _status.value = KvizStatus.PLAVA
            _poruka.value = Messages.KVIZ_ZAVRSEN
        }
    }
}
