package ba.etfrma.fakultetskikvizovi.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ba.etfrma.fakultetskikvizovi.data.referentniDatum
import ba.etfrma.fakultetskikvizovi.model.Kviz
import ba.etfrma.fakultetskikvizovi.model.KvizStatus
import ba.etfrma.fakultetskikvizovi.model.getStatus
import ba.etfrma.fakultetskikvizovi.ui.theme.AMGSilver
import ba.etfrma.fakultetskikvizovi.ui.theme.CardGraphite
import ba.etfrma.fakultetskikvizovi.ui.theme.SilverWhite
import java.time.format.DateTimeFormatter

@Composable
fun KvizCard(kviz: Kviz) {

    val status = kviz.getStatus(referentniDatum)

    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    val datumZaPrikaz = when (status) {
        KvizStatus.PLAVA  -> kviz.datumRada!!.format(formatter)
        KvizStatus.ZELENA -> kviz.datumKraj.format(formatter)
        KvizStatus.CRVENA -> kviz.datumKraj.format(formatter)
        KvizStatus.ZUTA   -> kviz.datumPocetak.format(formatter)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .testTag("kviz_item_${kviz.naziv}"),
        colors = CardDefaults.cardColors(
            containerColor = CardGraphite,
            contentColor   = SilverWhite
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = kviz.nazivPredmeta,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = SilverWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = kviz.naziv,
                    fontSize = 13.sp,
                    color = AMGSilver
                )
                Text(
                    text = datumZaPrikaz,
                    fontSize = 13.sp,
                    color = AMGSilver
                )
                Text(
                    text = "${kviz.trajanje} min",
                    fontSize = 12.sp,
                    color = AMGSilver.copy(alpha = 0.6f)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StatusIndikator(status = status)

                kviz.osvojeniBodovi?.let {
                    Text(text = it.toString())
                }
            }
        }
    }
}