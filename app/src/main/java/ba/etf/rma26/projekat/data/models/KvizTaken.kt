package ba.etf.rma26.projekat.data.models

data class KvizTaken(
    val id: Int = 0,
    val hash: String = "",
    val idKviza: Int = 0,
    val datumRada: String = "",
    val zavrsen: Boolean = false,
    val osvojenBodovi: Int = 0
)