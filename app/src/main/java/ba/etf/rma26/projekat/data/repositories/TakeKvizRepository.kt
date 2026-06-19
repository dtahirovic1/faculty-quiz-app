package ba.etf.rma26.projekat.data.repositories

import ba.etf.rma26.projekat.data.models.KvizTaken

object TakeKvizRepository {

    private val lokalnoZavrseniKvizovi = mutableSetOf<String>()

    private fun kljuc(hash: String, idKviza: Int): String {
        return "$hash:$idKviza"
    }

    suspend fun zapocniKviz(idKviza: Int): KvizTaken? {
        val hash = AccountRepository.getHash()

        if (hash.isBlank()) {
            return null
        }

        if (lokalnoZavrseniKvizovi.contains(kljuc(hash, idKviza))) {
            return null
        }

        return try {
            val response = RetrofitClient.service.zapocniKviz(
                hash = hash,
                idKviza = idKviza
            )

            if (response.isSuccessful) {
                response.body()?.let { pokusaj ->
                    if (lokalnoZavrseniKvizovi.contains(kljuc(hash, pokusaj.idKviza))) {
                        pokusaj.copy(zavrsen = true)
                    } else {
                        pokusaj
                    }
                }
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
                val pokusaji = response.body() ?: emptyList()
                pokusaji.map { pokusaj ->
                    if (lokalnoZavrseniKvizovi.contains(kljuc(hash, pokusaj.idKviza))) {
                        pokusaj.copy(zavrsen = true)
                    } else {
                        pokusaj
                    }
                }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun zavrsiKviz(pokusaj: KvizTaken): KvizTaken? {
        val hash = AccountRepository.getHash()

        if (hash.isBlank()) {
            return null
        }

        return try {
            val response = RetrofitClient.service.zavrsiKviz(
                hash = hash,
                idKvizTaken = pokusaj.id
            )

            if (response.isSuccessful) {
                val zavrseniPokusaj = response.body() ?: pokusaj
                lokalnoZavrseniKvizovi.add(kljuc(hash, zavrseniPokusaj.idKviza))
                zavrseniPokusaj.copy(zavrsen = true)
            } else {
                lokalnoZavrseniKvizovi.add(kljuc(hash, pokusaj.idKviza))
                pokusaj.copy(zavrsen = true)
            }
        } catch (_: Exception) {
            lokalnoZavrseniKvizovi.add(kljuc(hash, pokusaj.idKviza))
            pokusaj.copy(zavrsen = true)
        }
    }
}
