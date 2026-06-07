package ba.etf.rma26.projekat.data.repositories

import ba.etf.rma26.projekat.data.models.KvizTaken

object TakeKvizRepository {

    suspend fun zapocniKviz(idKviza: Int): KvizTaken? {
        val hash = AccountRepository.getHash()

        if (hash.isBlank()) {
            return null
        }

        return try {
            val response = RetrofitClient.service.zapocniKviz(
                hash = hash,
                idKviza = idKviza
            )

            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getPocetiKvizovi(): List<KvizTaken>? {
        val hash = AccountRepository.getHash()

        if (hash.isBlank()) {
            return emptyList()
        }

        return try {
            val response = RetrofitClient.service.getPocetiKvizovi(hash)

            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}