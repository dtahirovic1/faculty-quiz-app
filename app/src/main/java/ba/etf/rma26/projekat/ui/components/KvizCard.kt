package ba.etf.rma26.projekat.ui.components

import androidx.compose.foundation.clickable
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
import ba.etf.rma26.projekat.data.models.Kviz
import ba.etf.rma26.projekat.data.models.KvizStatus
import ba.etf.rma26.projekat.data.models.KvizTaken
import ba.etf.rma26.projekat.data.models.getStatus
import ba.etf.rma26.projekat.ui.theme.AMGSilver
import ba.etf.rma26.projekat.ui.theme.CardGraphite
import ba.etf.rma26.projekat.ui.theme.SilverWhite
import ba.etf.rma26.projekat.util.AppDateProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun KvizCard(
    kviz: Kviz,
    pokusaj: KvizTaken? = null,
    onClick: () -> Unit = {},
    onNeaktivanClick: (KvizStatus) -> Unit = {}
) {
    val status = kviz.getStatus(
        pokusaj = pokusaj,
        referentniDatum = AppDateProvider.danas()
    )

    val datumZaPrikaz = when (status) {
        KvizStatus.PLAVA -> formatirajDatum(pokusaj?.datumRada ?: kviz.datumKraj)
        KvizStatus.ZELENA -> formatirajDatum(kviz.datumKraj)
        KvizStatus.CRVENA -> formatirajDatum(kviz.datumKraj)
        KvizStatus.ZUTA -> formatirajDatum(kviz.datumPocetka)
    }

    val mozeRaditi = status == KvizStatus.ZELENA

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .testTag("kviz_item_${kviz.naziv}")
            .clickable {
                if (mozeRaditi) {
                    onClick()
                } else {
                    onNeaktivanClick(status)
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = CardGraphite,
            contentColor = SilverWhite
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = kviz.nazivPredmeta ?: "",
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
                modifier = Modifier.align(Alignment.TopEnd),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.testTag("kviz_status_icon")) {
                    StatusIndikator(status = status)
                }

                pokusaj?.let {
                    Text(
                        text = it.osvojenBodovi.toString(),
                        fontSize = 12.sp,
                        color = AMGSilver
                    )
                }
            }
        }
    }
}

private fun formatirajDatum(datum: String): String {
    return try {
        val localDate = LocalDate.parse(datum.take(10))
        localDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    } catch (_: Exception) {
        datum.take(10)
    }
}