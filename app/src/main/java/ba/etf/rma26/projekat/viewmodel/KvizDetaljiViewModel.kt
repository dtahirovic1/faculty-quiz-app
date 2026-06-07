package ba.etf.rma26.projekat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ba.etf.rma26.projekat.data.models.KvizStatus
import ba.etf.rma26.projekat.data.models.KvizTaken
import ba.etf.rma26.projekat.data.models.Pitanje
import ba.etf.rma26.projekat.data.models.getStatus
import ba.etf.rma26.projekat.data.repositories.KvizRepository
import ba.etf.rma26.projekat.data.repositories.OdgovorRepository
import ba.etf.rma26.projekat.data.repositories.PitanjeKvizRepository
import ba.etf.rma26.projekat.data.repositories.TakeKvizRepository
import ba.etf.rma26.projekat.util.AppDateProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class KvizDetaljiViewModel : ViewModel() {

    private val _pitanja = MutableStateFlow<List<Pitanje>>(emptyList())
    val pitanja: StateFlow<List<Pitanje>> = _pitanja.asStateFlow()

    private val _pokusaj = MutableStateFlow<KvizTaken?>(null)
    val pokusaj: StateFlow<KvizTaken?> = _pokusaj.asStateFlow()

    private val _rezultat = MutableStateFlow<Int?>(null)
    val rezultat: StateFlow<Int?> = _rezultat.asStateFlow()

    private val _poruka = MutableStateFlow<String?>(null)
    val poruka: StateFlow<String?> = _poruka.asStateFlow()

    fun ucitaj(idKviza: Int) {
        viewModelScope.launch {
            _poruka.value = null
            _pitanja.value = emptyList()
            _rezultat.value = null
            _pokusaj.value = null

            val kviz = KvizRepository.getById(idKviza)

            if (kviz == null) {
                _poruka.value = "Kviz ne postoji."
                return@launch
            }

            val pokusaji = TakeKvizRepository.getPocetiKvizovi() ?: emptyList()
            val pokusajZaKviz = pokusaji.firstOrNull { it.idKviza == idKviza }

            val status = kviz.getStatus(
                pokusaj = pokusajZaKviz,
                referentniDatum = AppDateProvider.danas()
            )

            if (status != KvizStatus.ZELENA) {
                _poruka.value = when (status) {
                    KvizStatus.PLAVA -> "Ovaj kviz je već urađen."
                    KvizStatus.CRVENA -> "Ovaj kviz je prošao."
                    KvizStatus.ZUTA -> "Ovaj kviz još nije počeo."
                    KvizStatus.ZELENA -> null
                }
                return@launch
            }

            val noviPokusaj = TakeKvizRepository.zapocniKviz(idKviza)

            if (noviPokusaj == null) {
                _poruka.value = "Kviz nije moguće započeti."
                return@launch
            }

            _pokusaj.value = noviPokusaj
            _pitanja.value = PitanjeKvizRepository.getPitanja(idKviza)
        }
    }

    fun odgovori(idPitanja: Int, odgovor: Int) {
        val idPokusaja = _pokusaj.value?.id ?: return

        viewModelScope.launch {
            _rezultat.value = OdgovorRepository.postaviOdgovorKviz(
                idKvizTaken = idPokusaja,
                idPitanje = idPitanja,
                odgovor = odgovor
            )
        }
    }
}