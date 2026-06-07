package ba.etf.rma26.projekat.data.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AccountRepository {
    private val _hash = MutableStateFlow("")
    val hash: StateFlow<String> = _hash.asStateFlow()

    suspend fun postaviHash(acHash: String): Boolean {
        val noviHash = acHash.trim()

        if (noviHash.isBlank()) {
            return false
        }

        _hash.value = noviHash
        return true
    }

    suspend fun getHash(): String {
        return _hash.value
    }
}