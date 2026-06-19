package ba.etf.rma26.projekat.data.models

import ba.etf.rma26.projekat.util.DateUtils
import ba.etf.rma26.projekat.util.KvizStatusCalculator
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
    return DateUtils.parseApiDate(this)
}

fun Kviz.getStatus(
    pokusaj: KvizTaken?,
    referentniDatum: LocalDate
): KvizStatus {
    return KvizStatusCalculator.izracunaj(
        kviz = this,
        pokusaj = pokusaj,
        referentniDatum = referentniDatum
    )
}
