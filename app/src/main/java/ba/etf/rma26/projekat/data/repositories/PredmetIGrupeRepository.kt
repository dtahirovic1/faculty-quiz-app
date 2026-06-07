package ba.etf.rma26.projekat.data.repositories

import ba.etf.rma26.projekat.data.models.Grupa
import ba.etf.rma26.projekat.data.models.Predmet

object PredmetIGrupaRepository {

    suspend fun getPredmeti(): List<Predmet> {
        return try {
            val response = RetrofitClient.service.getPredmeti()

            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getGrupe(): List<Grupa> {
        return try {
            val response = RetrofitClient.service.getGrupe()

            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getGrupeZaPredmet(idPredmeta: Int): List<Grupa> {
        return try {
            val response = RetrofitClient.service.getGrupeZaPredmet(idPredmeta)

            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun upisiUGrupu(idGrupa: Int): Boolean {
        val hash = AccountRepository.getHash()

        if (hash.isBlank()) {
            return false
        }

        return try {
            val response = RetrofitClient.service.upisiUGrupu(
                hash = hash,
                idGrupa = idGrupa,
                apiKey = ApiConfig.getApiKey()
            )

            if (response.isSuccessful) {
                response.body() == true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUpisaneGrupe(): List<Grupa> {
        val hash = AccountRepository.getHash()

        if (hash.isBlank()) {
            return emptyList()
        }

        return try {
            val response = RetrofitClient.service.getUpisaneGrupe(hash)

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