package ba.etf.rma26.projekat.data.models

data class Pitanje(
    val id: Int = 0,
    val idKviza: Int = 0,
    val naziv: String = "",
    val tekstPitanja: String = "",
    val opcije: List<String> = emptyList(),
    val tacan: Int = -1
)