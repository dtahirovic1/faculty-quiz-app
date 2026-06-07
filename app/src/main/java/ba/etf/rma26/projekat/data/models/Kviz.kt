package ba.etf.rma26.projekat.data.models

import java.time.LocalDate

data class Kviz(
    val id: Int = 0,
    val naziv: String = "",
    val idPredmeta: Int = 0,
    val idGrupe: Int = 0,
    val datumPocetka: String = "",
    val datumKraj: String = "",
    val trajanje: Int = 0,
    val nazivPredmeta: String? = null,
    val nazivGrupe: String? = null
)

enum class KvizStatus {
    ZELENA,
    PLAVA,
    ZUTA,
    CRVENA
}

fun String.toLocalDate(): LocalDate? {
    return try {
        LocalDate.parse(this.take(10))
    } catch (_: Exception) {
        null
    }
}

fun Kviz.getStatus(
    pokusaj: KvizTaken?,
    referentniDatum: LocalDate
): KvizStatus {
    if (pokusaj != null) {
        return KvizStatus.PLAVA
    }

    val pocetak = datumPocetka.toLocalDate()
    val kraj = datumKraj.toLocalDate()

    if (pocetak == null || kraj == null) {
        return KvizStatus.ZELENA
    }

    return when {
        referentniDatum.isBefore(pocetak) -> KvizStatus.ZUTA
        referentniDatum.isAfter(kraj) -> KvizStatus.CRVENA
        else -> KvizStatus.ZELENA
    }
}
