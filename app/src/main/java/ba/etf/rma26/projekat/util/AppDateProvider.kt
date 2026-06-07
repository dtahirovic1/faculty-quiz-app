package ba.etf.rma26.projekat.util

import java.time.LocalDate

object AppDateProvider {

    private var referentniDatum: LocalDate? = null

    fun danas(): LocalDate {
        return referentniDatum ?: LocalDate.now()
    }

    fun postaviReferentniDatum(datum: LocalDate?) {
        referentniDatum = datum
    }
}