package ba.etf.rma26.projekat.data.models

data class Odgovor(
    val id: Int,
    val idKvizTaken: Int,
    val idPitanje: Int,
    val odgovor: Int
)