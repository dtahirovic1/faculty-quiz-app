package ba.etf.rma26.projekat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ba.etf.rma26.projekat.data.models.Kviz
import ba.etf.rma26.projekat.data.models.KvizTaken
import ba.etf.rma26.projekat.data.models.toLocalDate
import ba.etf.rma26.projekat.data.repositories.AccountRepository
import ba.etf.rma26.projekat.data.repositories.KvizRepository
import ba.etf.rma26.projekat.data.repositories.TakeKvizRepository
import ba.etf.rma26.projekat.util.AppDateProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

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

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _pokusajiPoKvizu = MutableStateFlow<Map<Int, KvizTaken>>(emptyMap())
    val pokusajiPoKvizu: StateFlow<Map<Int, KvizTaken>> = _pokusajiPoKvizu.asStateFlow()

    init {
        viewModelScope.launch {
            AccountRepository.hash.collect {
                ucitajKvizove()
            }
        }
    }

    fun promijeniFilter(filter: FilterOpcija) {
        _selectedFilter.value = filter
        viewModelScope.launch {
            ucitajKvizove()
        }
    }

    fun osvjezi() {
        viewModelScope.launch {
            ucitajKvizove()
        }
    }

    private suspend fun ucitajKvizove() {
        _loading.value = true

        val danas = AppDateProvider.danas()

        val svi = KvizRepository.getAll()
        val upisani = KvizRepository.getUpisani()
        val pokusaji = TakeKvizRepository.getPocetiKvizovi() ?: emptyList()

        val pokusajiMap = pokusaji.associateBy { it.idKviza }
        _pokusajiPoKvizu.value = pokusajiMap

        val filtrirani = when (_selectedFilter.value) {
            FilterOpcija.SVI_MOJI -> upisani

            FilterOpcija.SVI -> svi

            FilterOpcija.URADENI -> upisani.filter { kviz ->
                pokusajiMap.containsKey(kviz.id)
            }

            FilterOpcija.BUDUCI -> upisani.filter { kviz ->
                kviz.datumPocetka.toLocalDate()?.isAfter(danas) == true
            }

            FilterOpcija.PROSLI -> upisani.filter { kviz ->
                kviz.datumKraj.toLocalDate()?.isBefore(danas) == true
            }
        }

        _kvizovi.value = filtrirani.sortedBy { it.datumPocetka }
        _loading.value = false
    }
}