package ba.etfrma.fakultetskikvizovi.data

import ba.etfrma.fakultetskikvizovi.model.Kviz
import java.time.LocalDateTime

val referentniDatum = LocalDateTime.of(2026, 4, 9, 12, 0)

object KvizStaticData {
    private val sviKvizovi = listOf(

        //plava-urađen
        Kviz(
            naziv = "Kviz 1",
            nazivPredmeta = "RMA",
            datumPocetak = LocalDateTime.of(2026, 4, 2, 10, 0),
            datumKraj = LocalDateTime.of(2026, 4, 2, 10, 30),
            datumRada = LocalDateTime.of(2026, 4, 2, 10, 15),
            trajanje = 30,
            nazivGrupe = "1a",
            osvojeniBodovi = 8f
        ),

        //crvena-prošao, nije urađen
        Kviz(
            naziv = "Kviz 2",
            nazivPredmeta = "RMA",
            datumPocetak = LocalDateTime.of(2026, 4, 3, 11, 0),
            datumKraj = LocalDateTime.of(2026, 4, 3, 11, 30),
            datumRada = null,
            trajanje = 30,
            nazivGrupe = "1b",
            osvojeniBodovi = null
        ),

        //zelena-aktivan, nije urađen
        Kviz(
            naziv = "Kviz 3",
            nazivPredmeta = "RMA",
            datumPocetak = LocalDateTime.of(2026, 4, 9, 11, 45),
            datumKraj = LocalDateTime.of(2026, 4, 9, 12, 30),
            datumRada = null,
            trajanje = 45,
            nazivGrupe = "1a",
            osvojeniBodovi = null
        ),

        //zuta-tek treba biti aktivan
        Kviz(
            naziv = "Kviz 4",
            nazivPredmeta = "RMA",
            datumPocetak = LocalDateTime.of(2026, 4, 12, 10, 0),
            datumKraj = LocalDateTime.of(2026, 4, 12, 10, 30),
            datumRada = null,
            trajanje = 30,
            nazivGrupe = "1b",
            osvojeniBodovi = null
        ),

        Kviz(
            naziv = "Kviz 1",
            nazivPredmeta = "UUP",
            datumPocetak = LocalDateTime.of(2026, 4, 5, 9, 0),
            datumKraj = LocalDateTime.of(2026, 4, 5, 9, 30),
            datumRada = LocalDateTime.of(2026, 4, 5, 9, 20),
            trajanje = 30,
            nazivGrupe = "1a",
            osvojeniBodovi = 9f
        ),

        Kviz(
            naziv = "Kviz 2",
            nazivPredmeta = "UUP",
            datumPocetak = LocalDateTime.of(2026, 4, 14, 13, 0),
            datumKraj = LocalDateTime.of(2026, 4, 14, 13, 30),
            datumRada = null,
            trajanje = 30,
            nazivGrupe = "1b",
            osvojeniBodovi = null
        ),

        Kviz(
            naziv = "Kviz 3",
            nazivPredmeta = "UUP",
            datumPocetak = LocalDateTime.of(2026, 4, 16, 14, 0),
            datumKraj = LocalDateTime.of(2026, 4, 16, 14, 30),
            datumRada = null,
            trajanje = 30,
            nazivGrupe = "1a",
            osvojeniBodovi = null
        ),

        Kviz(
            naziv = "Kviz 4",
            nazivPredmeta = "UUP",
            datumPocetak = LocalDateTime.of(2026, 4, 18, 15, 0),
            datumKraj = LocalDateTime.of(2026, 4, 18, 15, 30),
            datumRada = null,
            trajanje = 30,
            nazivGrupe = "1b",
            osvojeniBodovi = null
        ),

        Kviz(
            naziv = "Kviz 1",
            nazivPredmeta = "DM",
            datumPocetak = LocalDateTime.of(2026, 4, 15, 8, 0),
            datumKraj = LocalDateTime.of(2026, 4, 15, 8, 30),
            datumRada = null,
            trajanje = 30,
            nazivGrupe = "1a",
            osvojeniBodovi = null
        ),

        Kviz(
            naziv = "Kviz 2",
            nazivPredmeta = "DM",
            datumPocetak = LocalDateTime.of(2026, 4, 16, 8, 0),
            datumKraj = LocalDateTime.of(2026, 4, 16, 8, 30),
            datumRada = null,
            trajanje = 30,
            nazivGrupe = "1b",
            osvojeniBodovi = null
        ),

        Kviz(
            naziv = "Kviz 1",
            nazivPredmeta = "RPR",
            datumPocetak = LocalDateTime.of(2026, 4, 17, 9, 0),
            datumKraj = LocalDateTime.of(2026, 4, 17, 9, 30),
            datumRada = null,
            trajanje = 30,
            nazivGrupe = "1a",
            osvojeniBodovi = null
        ),

        Kviz(
            naziv = "Kviz 2",
            nazivPredmeta = "RPR",
            datumPocetak = LocalDateTime.of(2026, 4, 18, 9, 0),
            datumKraj = LocalDateTime.of(2026, 4, 18, 9, 30),
            datumRada = null,
            trajanje = 30,
            nazivGrupe = "1b",
            osvojeniBodovi = null
        ),

        Kviz(
            naziv = "Kviz 1",
            nazivPredmeta = "WT",
            datumPocetak = LocalDateTime.of(2026, 4, 19, 10, 0),
            datumKraj = LocalDateTime.of(2026, 4, 19, 10, 30),
            datumRada = null,
            trajanje = 30,
            nazivGrupe = "1a",
            osvojeniBodovi = null
        ),

        Kviz(
            naziv = "Kviz 2",
            nazivPredmeta = "WT",
            datumPocetak = LocalDateTime.of(2026, 4, 20, 10, 0),
            datumKraj = LocalDateTime.of(2026, 4, 20, 10, 30),
            datumRada = null,
            trajanje = 30,
            nazivGrupe = "1b",
            osvojeniBodovi = null
        ),

        Kviz(
            naziv = "Kviz 1",
            nazivPredmeta = "TP",
            datumPocetak = LocalDateTime.of(2026, 4, 21, 11, 0),
            datumKraj = LocalDateTime.of(2026, 4, 21, 11, 30),
            datumRada = null,
            trajanje = 30,
            nazivGrupe = "1a",
            osvojeniBodovi = null
        ),

        Kviz(
            naziv = "Kviz 2",
            nazivPredmeta = "TP",
            datumPocetak = LocalDateTime.of(2026, 4, 22, 11, 0),
            datumKraj = LocalDateTime.of(2026, 4, 22, 11, 30),
            datumRada = null,
            trajanje = 30,
            nazivGrupe = "1b",
            osvojeniBodovi = null
        )
    )

    fun getAll(): List<Kviz> = sviKvizovi.sortedBy { it.datumPocetak }
    fun getDone(): List<Kviz> = getUpisani().filter { it.datumRada != null }
    fun getFuture(): List<Kviz> = getUpisani().filter { referentniDatum.isBefore(it.datumPocetak) }
    fun getNotTaken(): List<Kviz> = getUpisani().filter { it.datumRada == null && referentniDatum.isAfter(it.datumKraj) }
    fun getUpisani(): List<Kviz> = sviKvizovi
        .filter { it.nazivPredmeta in PredmetStaticData.getUpisani().map { p -> p.naziv } }
        .sortedBy { it.datumPocetak }

    fun getReferentDate(): LocalDateTime = referentniDatum

}
