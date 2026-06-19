package ba.etf.rma26.projekat.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ba.etf.rma26.projekat.data.models.Kviz
import ba.etf.rma26.projekat.data.models.KvizStatus
import ba.etf.rma26.projekat.ui.theme.AMGSilver
import ba.etf.rma26.projekat.ui.theme.CardGraphite
import ba.etf.rma26.projekat.ui.theme.SilverWhite
import ba.etf.rma26.projekat.util.DateUtils
import ba.etf.rma26.projekat.util.Messages
import ba.etf.rma26.projekat.viewmodel.KvizDetaljiViewModel

private val PetronasTeal = Color(0xFF00A19C)
private val SelectedAnswerColor = Color(0xFFD7D7D7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KvizDetaljiScreen(
    idKviza: Int,
    viewModel: KvizDetaljiViewModel,
    onBack: () -> Unit
) {
    val kviz by viewModel.kviz.collectAsState()
    val pitanja by viewModel.pitanja.collectAsState()
    val pokusaj by viewModel.pokusaj.collectAsState()
    val imaZapocetPokusaj by viewModel.imaZapocetPokusaj.collectAsState()
    val odabraniOdgovori by viewModel.odabraniOdgovori.collectAsState()
    val kvizZavrsen by viewModel.kvizZavrsen.collectAsState()
    val korisnikUpisan by viewModel.korisnikUpisan.collectAsState()
    val provjeraUpisaZavrsena by viewModel.provjeraUpisaZavrsena.collectAsState()
    val status by viewModel.status.collectAsState()
    val poruka by viewModel.poruka.collectAsState()

    LaunchedEffect(idKviza) {
        viewModel.ucitaj(idKviza)
    }

    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Detalji kviza")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Nazad"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (pokusaj == null) {
                kviz?.let {
                    KvizInfoCard(kviz = it)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            poruka?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardGraphite,
                        contentColor = SilverWhite
                    )
                ) {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SilverWhite,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                if (pokusaj == null || kvizZavrsen) {
                    return@Column
                }
            }

            if (kviz == null && poruka == null) {
                Text(
                    text = "Učitavanje detalja kviza...",
                    fontSize = 14.sp,
                    color = AMGSilver
                )
                return@Column
            }

            if (pokusaj == null) {
                if (!provjeraUpisaZavrsena) {
                    Text(
                        text = "Provjera upisa...",
                        fontSize = 14.sp,
                        color = AMGSilver
                    )
                    return@Column
                }

                if (korisnikUpisan && status == KvizStatus.ZELENA && !kvizZavrsen) {
                    Button(
                        onClick = { viewModel.zapocniKviz() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PetronasTeal,
                            contentColor = Color(0xFF0A0A0A)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("zapocniKvizDugme")
                    ) {
                        Text(if (imaZapocetPokusaj) "Nastavi kviz" else "Započni kviz")
                    }
                }
                return@Column
            }

            if (pitanja.isEmpty()) {
                Text(
                    text = Messages.NEMA_PITANJA,
                    fontSize = 14.sp,
                    color = AMGSilver
                )
            } else {
                Text(
                    text = "Odgovoreno: ${odabraniOdgovori.size}/${pitanja.size}",
                    fontSize = 14.sp,
                    color = AMGSilver,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(pitanja) { pitanje ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = CardGraphite,
                                contentColor = SilverWhite
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = pitanje.tekstPitanja,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SilverWhite
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                pitanje.opcije.forEachIndexed { index, opcija ->
                                    val odabran = odabraniOdgovori[pitanje.id] == index

                                    Button(
                                        onClick = {
                                            viewModel.odgovori(
                                                idPitanja = pitanje.id,
                                                odgovor = index
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (odabran) SelectedAnswerColor else PetronasTeal,
                                            contentColor = Color(0xFF0A0A0A)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                    ) {
                                        Text(text = opcija)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { viewModel.zavrsiKviz() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PetronasTeal,
                                contentColor = Color(0xFF0A0A0A)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 24.dp)
                                .testTag("zavrsiKvizDugme")
                        ) {
                            Text("Završi kviz")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KvizInfoCard(kviz: Kviz) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CardGraphite,
            contentColor = SilverWhite
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = kviz.naziv,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = SilverWhite
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Predmet: ${kviz.nazivPredmeta ?: "Nepoznat predmet"}",
                fontSize = 14.sp,
                color = AMGSilver
            )

            Text(
                text = "Grupa: ${kviz.nazivGrupe ?: "Nepoznata grupa"}",
                fontSize = 14.sp,
                color = AMGSilver
            )

            Text(
                text = "Trajanje: ${kviz.trajanje} min",
                fontSize = 14.sp,
                color = AMGSilver
            )

            Text(
                text = "Početak: ${DateUtils.formatApiDate(kviz.datumPocetka)}",
                fontSize = 14.sp,
                color = AMGSilver
            )

            Text(
                text = "Kraj: ${DateUtils.formatApiDate(kviz.datumKraj)}",
                fontSize = 14.sp,
                color = AMGSilver
            )
        }
    }
}
