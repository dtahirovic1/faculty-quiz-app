package ba.etfrma.fakultetskikvizovi.viewmodel

import androidx.lifecycle.ViewModel
import ba.etfrma.fakultetskikvizovi.data.GrupaStaticData
import ba.etfrma.fakultetskikvizovi.data.PredmetStaticData
import ba.etfrma.fakultetskikvizovi.model.Grupa
import ba.etfrma.fakultetskikvizovi.model.Predmet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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

        _dostupniPredmeti.value = PredmetStaticData.getNeupisani()
            .filter { it.godina == godina }
        azurirajMozeDaSeUpise()
    }

    fun odaberiPredmet(predmet: Predmet) {
        _odabranPredmet.value = predmet
        _odabranaGrupa.value = null
        _dostupneGrupe.value = GrupaStaticData.getGrupaFromPredmet(predmet.naziv)
        azurirajMozeDaSeUpise()
    }

    fun odaberiGrupu(grupa: Grupa) {
        _odabranaGrupa.value = grupa
        azurirajMozeDaSeUpise()
    }

    fun upisise() {
        val predmet = _odabranPredmet.value ?: return
        PredmetStaticData.upisise(predmet)

        _odabranaGodina.value = null
        _odabranPredmet.value = null
        _odabranaGrupa.value = null
        _dostupniPredmeti.value = emptyList()
        _dostupneGrupe.value = emptyList()
        azurirajMozeDaSeUpise()
    }
}