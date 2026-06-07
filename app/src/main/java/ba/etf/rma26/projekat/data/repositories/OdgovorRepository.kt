package ba.etf.rma26.projekat.data.repositories

import ba.etf.rma26.projekat.data.models.Odgovor
import ba.etf.rma26.projekat.data.models.OdgovorRequest

object OdgovorRepository {

    suspend fun getOdgovoriKviz(idKviza: Int): List<Odgovor> {
        val hash = AccountRepository.getHash()

        if (hash.isBlank()) {
            return emptyList()
        }

        return try {
            val response = RetrofitClient.service.getOdgovoriKviz(
                hash = hash,
                idKviza = idKviza
            )

            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun postaviOdgovorKviz(
        idKvizTaken: Int,
        idPitanje: Int,
        odgovor: Int
    ): Int {
        val hash = AccountRepository.getHash()

        if (hash.isBlank()) return -1
        if (odgovor < 0) return -1

        val request = OdgovorRequest(
            idPitanje = idPitanje,
            odgovor = odgovor
        )

        return try {
            val postResponse = RetrofitClient.service.postaviOdgovor(
                hash = hash,
                idKvizTaken = idKvizTaken,
                request = request
            )

            if (postResponse.isSuccessful) {
                return postResponse.body() ?: -1
            }

            val putResponse = RetrofitClient.service.promijeniOdgovor(
                hash = hash,
                idKvizTaken = idKvizTaken,
                request = request
            )

            if (putResponse.isSuccessful) {
                putResponse.body() ?: -1
            } else {
                -1
            }
        } catch (_: Exception) {
            -1
        }
    }
}