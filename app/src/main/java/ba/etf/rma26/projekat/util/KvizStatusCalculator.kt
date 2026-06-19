package ba.etf.rma26.projekat.util

import ba.etf.rma26.projekat.data.models.Kviz
import ba.etf.rma26.projekat.data.models.KvizStatus
import ba.etf.rma26.projekat.data.models.KvizTaken
import java.time.LocalDate

object KvizStatusCalculator {
    fun izracunaj(
        kviz: Kviz,
        pokusaj: KvizTaken?,
        referentniDatum: LocalDate
    ): KvizStatus {
        if (pokusaj?.zavrsen == true) {
            return KvizStatus.PLAVA
        }

        val pocetak = DateUtils.parseApiDate(kviz.datumPocetka)
        val kraj = DateUtils.parseApiDate(kviz.datumKraj)

        if (pocetak == null || kraj == null) {
            return KvizStatus.ZELENA
        }

        return when {
            referentniDatum.isBefore(pocetak) -> KvizStatus.ZUTA
            referentniDatum.isAfter(kraj) -> KvizStatus.CRVENA
            else -> KvizStatus.ZELENA
        }
    }
}
