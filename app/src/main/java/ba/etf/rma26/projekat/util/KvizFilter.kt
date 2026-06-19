package ba.etf.rma26.projekat.util

import ba.etf.rma26.projekat.data.models.Kviz
import ba.etf.rma26.projekat.data.models.KvizTaken
import ba.etf.rma26.projekat.viewmodel.FilterOpcija
import java.time.LocalDate

object KvizFilter {
    fun filtriraj(
        filter: FilterOpcija,
        svi: List<Kviz>,
        upisani: List<Kviz>,
        pokusajiPoKvizu: Map<Int, KvizTaken>,
        referentniDatum: LocalDate
    ): List<Kviz> {
        val filtrirani = when (filter) {
            FilterOpcija.SVI_MOJI -> upisani
            FilterOpcija.SVI -> svi
            FilterOpcija.URADENI -> upisani.filter { kviz ->
                pokusajiPoKvizu[kviz.id]?.zavrsen == true
            }
            FilterOpcija.BUDUCI -> upisani.filter { kviz ->
                DateUtils.parseApiDate(kviz.datumPocetka)?.isAfter(referentniDatum) == true
            }
            FilterOpcija.PROSLI -> upisani.filter { kviz ->
                DateUtils.parseApiDate(kviz.datumKraj)?.isBefore(referentniDatum) == true
            }
        }

        return filtrirani.sortedBy { it.datumPocetka }
    }
}
