package ba.etfrma.fakultetskikvizovi.model

import java.time.LocalDateTime

enum class KvizStatus {
    PLAVA,
    ZELENA,
    ZUTA,
    CRVENA
}
data class Kviz(
    var naziv: String,
    var nazivPredmeta: String,
    var datumPocetak: LocalDateTime,
    var datumKraj: LocalDateTime,
    var datumRada: LocalDateTime?,
    var trajanje: Int,
    var nazivGrupe: String,
    var osvojeniBodovi: Float?
)

fun Kviz.getStatus(referentniDatum: LocalDateTime): KvizStatus {
    if (datumRada != null) return KvizStatus.PLAVA
    return when {
        referentniDatum.isBefore(datumPocetak) -> KvizStatus.ZUTA
        referentniDatum.isAfter(datumKraj)     -> KvizStatus.CRVENA
        else                                    -> KvizStatus.ZELENA
    }
}