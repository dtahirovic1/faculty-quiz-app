package ba.etfrma.fakultetskikvizovi.viewmodel

import androidx.lifecycle.ViewModel
import ba.etfrma.fakultetskikvizovi.data.KvizStaticData
import ba.etfrma.fakultetskikvizovi.model.Kviz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class FilterOpcija(val naziv: String) {
    SVI_MOJI("Svi moji kvizovi"),
    SVI("Svi kvizovi"),
    URADENI("Urađeni kvizovi"),
    BUDUCI("Budući kvizovi"),
    PROSLI("Prošli kvizovi")
}

class KvizViewModel : ViewModel() {
    private val _selectedFilter = MutableStateFlow(FilterOpcija.SVI_MOJI)
    val selectedFilter: StateFlow<FilterOpcija> = _selectedFilter.asStateFlow()
    private val _kvizovi = MutableStateFlow<List<Kviz>>(emptyList())
    val kvizovi: StateFlow<List<Kviz>> = _kvizovi.asStateFlow()

    init {
        ucitajKvizove()
    }

    fun promijeniFilter(filter: FilterOpcija) {
        _selectedFilter.value = filter
        ucitajKvizove()
    }

    private fun ucitajKvizove() {
        _kvizovi.value = when (_selectedFilter.value) {
            FilterOpcija.SVI_MOJI -> KvizStaticData.getUpisani()
            FilterOpcija.SVI      -> KvizStaticData.getAll()
            FilterOpcija.URADENI  -> KvizStaticData.getDone()
            FilterOpcija.BUDUCI   -> KvizStaticData.getFuture()
            FilterOpcija.PROSLI   -> KvizStaticData.getNotTaken()
        }
    }
}