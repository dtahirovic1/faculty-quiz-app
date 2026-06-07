package ba.etf.rma26.projekat.data.repositories

import ba.etf.rma26.projekat.data.models.Kviz

object KvizRepository {

    suspend fun getAll(): List<Kviz> {
        return try {
            val response = RetrofitClient.service.getKvizovi()

            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getById(id: Int): Kviz? {
        return try {
            val response = RetrofitClient.service.getKvizById(id)

            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getUpisani(): List<Kviz> {
        val hash = AccountRepository.getHash()

        if (hash.isBlank()) {
            return emptyList()
        }

        return try {
            val response = RetrofitClient.service.getUpisaniKvizovi(hash)

            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}