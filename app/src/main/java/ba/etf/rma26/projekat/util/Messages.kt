package ba.etf.rma26.projekat.util

import ba.etf.rma26.projekat.data.models.KvizStatus

object Messages {
    const val USERNAME_OBAVEZAN = "Unesite username."
    const val KVIZ_NE_POSTOJI = "Kviz ne postoji."
    const val KVIZ_VEC_URADEN = "Ovaj kviz je već urađen."
    const val KVIZ_PROSAO = "Ovaj kviz je prošao."
    const val KVIZ_NIJE_POCEO = "Ovaj kviz još nije počeo."
    const val KVIZ_NE_MOZE_ZAPOCETI = "Kviz nije moguće započeti."
    const val POGRESNA_GRUPA = "Ne možete raditi ovaj kviz jer on nije namijenjen vasoj grupi."
    const val KVIZ_NIJE_ZAVRSEN = "Kviz nije moguće završiti."
    const val KVIZ_ZAVRSEN = "Kviz je završen."
    const val ODGOVOR_NIJE_POSLAN = "Odgovor nije ispravno poslan."
    const val ODGOVORITE_NA_SVA_PITANJA = "Odgovorite na sva pitanja prije završavanja kviza."
    const val NEMA_PITANJA = "Nema pitanja za ovaj kviz."

    fun porukaZaStatus(status: KvizStatus): String {
        return when (status) {
            KvizStatus.PLAVA -> KVIZ_VEC_URADEN
            KvizStatus.CRVENA -> KVIZ_PROSAO
            KvizStatus.ZUTA -> KVIZ_NIJE_POCEO
            KvizStatus.ZELENA -> ""
        }
    }
}
