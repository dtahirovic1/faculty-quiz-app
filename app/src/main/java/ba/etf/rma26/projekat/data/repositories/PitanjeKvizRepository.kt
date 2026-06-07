package ba.etf.rma26.projekat.data.repositories

import ba.etf.rma26.projekat.data.models.Pitanje

object PitanjeKvizRepository {

    suspend fun getPitanja(idKviza: Int): List<Pitanje> {
        return try {
            val response = RetrofitClient.service.getPitanja(idKviza)

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