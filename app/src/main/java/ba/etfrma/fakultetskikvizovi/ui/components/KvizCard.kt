package ba.etfrma.fakultetskikvizovi.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ba.etfrma.fakultetskikvizovi.data.referentniDatum
import ba.etfrma.fakultetskikvizovi.model.Kviz
import ba.etfrma.fakultetskikvizovi.model.KvizStatus
import ba.etfrma.fakultetskikvizovi.model.getStatus
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
            .testTag("kviz_item_${kviz.naziv}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = kviz.nazivPredmeta,
                    fontSize = 18.sp
                )
                Text(text = kviz.naziv)
                Text(text = datumZaPrikaz)
                Text(text = "${kviz.trajanje} min")
            }
            Column(horizontalAlignment = Alignment.End) {
                kviz.osvojeniBodovi?.let {
                    Text(text = it.toString())
                }
                StatusIndikator(status = status)
            }
        }
    }
}