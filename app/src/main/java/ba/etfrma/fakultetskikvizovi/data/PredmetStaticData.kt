package ba.etfrma.fakultetskikvizovi.data

import ba.etfrma.fakultetskikvizovi.model.Predmet

object PredmetStaticData {
    private val sviPredmeti = listOf(
        Predmet("RMA", 2),
        Predmet("DM", 2),
        Predmet("RPR", 2),
        Predmet("TP", 1),
        Predmet("UUP", 1),
        Predmet("WT", 3)
    )
    private val upisaniPredmeti = mutableListOf("RMA", "UUP")

    fun getAll(): List<Predmet> = sviPredmeti
    fun getUpisani(): List<Predmet> = sviPredmeti.filter { it.naziv in upisaniPredmeti }
    fun getNeupisani(): List<Predmet> = sviPredmeti.filter { it.naziv !in upisaniPredmeti }
    fun upisise(predmet: Predmet) {
        if (predmet.naziv !in upisaniPredmeti) {
            upisaniPredmeti.add(predmet.naziv)
        }
    }
}