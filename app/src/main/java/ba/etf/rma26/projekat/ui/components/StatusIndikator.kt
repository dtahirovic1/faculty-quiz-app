package ba.etf.rma26.projekat.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import ba.etf.rma26.projekat.R
import ba.etf.rma26.projekat.data.models.KvizStatus

@Composable
fun StatusIndikator(status: KvizStatus) {

    val slikaId = when (status) {
        KvizStatus.PLAVA   -> R.drawable.plava
        KvizStatus.ZELENA  -> R.drawable.zelena
        KvizStatus.ZUTA    -> R.drawable.zuta
        KvizStatus.CRVENA  -> R.drawable.crvena
    }

    val opis = when (status) {
        KvizStatus.PLAVA   -> "Plava"
        KvizStatus.ZELENA  -> "Zelena"
        KvizStatus.ZUTA    -> "Žuta"
        KvizStatus.CRVENA  -> "Crvena"
    }

    Image(
        painter = painterResource(id = slikaId),
        contentDescription = opis,
        modifier = Modifier.testTag("kviz_status_icon")
    )
}