package ba.etf.rma26.projekat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ba.etf.rma26.projekat.data.models.Grupa
import ba.etf.rma26.projekat.data.models.Predmet
import ba.etf.rma26.projekat.data.repositories.PredmetIGrupaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UpisViewModel : ViewModel() {

    private val _odabranaGodina = MutableStateFlow<Int?>(null)
    val odabranaGodina: StateFlow<Int?> = _odabranaGodina.asStateFlow()

    private val _odabranPredmet = MutableStateFlow<Predmet?>(null)
    val odabranPredmet: StateFlow<Predmet?> = _odabranPredmet.asStateFlow()

    private val _odabranaGrupa = MutableStateFlow<Grupa?>(null)
    val odabranaGrupa: StateFlow<Grupa?> = _odabranaGrupa.asStateFlow()

    private val _dostupniPredmeti = MutableStateFlow<List<Predmet>>(emptyList())
    val dostupniPredmeti: StateFlow<List<Predmet>> = _dostupniPredmeti.asStateFlow()

    private val _dostupneGrupe = MutableStateFlow<List<Grupa>>(emptyList())
    val dostupneGrupe: StateFlow<List<Grupa>> = _dostupneGrupe.asStateFlow()

    private val _mozeDaSeUpise = MutableStateFlow(false)
    val mozeDaSeUpise: StateFlow<Boolean> = _mozeDaSeUpise.asStateFlow()

    private fun azurirajMozeDaSeUpise() {
        _mozeDaSeUpise.value = _odabranaGodina.value != null &&
                _odabranPredmet.value != null &&
                _odabranaGrupa.value != null
    }

    fun odaberiGodinu(godina: Int) {
        _odabranaGodina.value = godina

        _odabranPredmet.value = null
        _odabranaGrupa.value = null
        _dostupneGrupe.value = emptyList()
        _dostupniPredmeti.value = emptyList()

        viewModelScope.launch {
            val sviPredmeti = PredmetIGrupaRepository.getPredmeti()
            val upisaneGrupe = PredmetIGrupaRepository.getUpisaneGrupe()
            val idUpisanihPredmeta = upisaneGrupe.map { it.idPredmeta }.toSet()

            _dostupniPredmeti.value = sviPredmeti
                .filter { it.godina == godina }
                .filter { it.id !in idUpisanihPredmeta }
        }

        azurirajMozeDaSeUpise()
    }

    fun odaberiPredmet(predmet: Predmet) {
        _odabranPredmet.value = predmet
        _odabranaGrupa.value = null
        _dostupneGrupe.value = emptyList()

        viewModelScope.launch {
            _dostupneGrupe.value =
                PredmetIGrupaRepository.getGrupeZaPredmet(predmet.id)
        }

        azurirajMozeDaSeUpise()
    }

    fun odaberiGrupu(grupa: Grupa) {
        _odabranaGrupa.value = grupa
        azurirajMozeDaSeUpise()
    }

    suspend fun upisise(): Boolean {
        val grupa = _odabranaGrupa.value ?: return false

        val uspjeh = PredmetIGrupaRepository.upisiUGrupu(grupa.id)

        if (uspjeh) {
            _odabranaGodina.value = null
            _odabranPredmet.value = null
            _odabranaGrupa.value = null
            _dostupniPredmeti.value = emptyList()
            _dostupneGrupe.value = emptyList()
            azurirajMozeDaSeUpise()
        }

        return uspjeh
    }
}