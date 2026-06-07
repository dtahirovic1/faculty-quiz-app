package ba.etf.rma26.projekat.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ba.etf.rma26.projekat.ui.theme.AMGSilver
import ba.etf.rma26.projekat.ui.theme.CardGraphite
import ba.etf.rma26.projekat.ui.theme.SilverWhite
import ba.etf.rma26.projekat.viewmodel.KvizDetaljiViewModel

private val PetronasTeal = Color(0xFF00A19C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KvizDetaljiScreen(
    idKviza: Int,
    viewModel: KvizDetaljiViewModel,
    onBack: () -> Unit
) {
    val pitanja by viewModel.pitanja.collectAsState()
    val rezultat by viewModel.rezultat.collectAsState()
    val poruka by viewModel.poruka.collectAsState()

    LaunchedEffect(idKviza) {
        viewModel.ucitaj(idKviza)
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
            rezultat?.let { bodovi ->
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
                        text = "Rezultat: $bodovi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SilverWhite,
                        modifier = Modifier.padding(12.dp)
                    )
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

                return@Column
            }

            if (pitanja.isEmpty()) {
                Text(
                    text = "Nema pitanja za ovaj kviz.",
                    fontSize = 14.sp,
                    color = AMGSilver
                )
            } else {
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
                                    Button(
                                        onClick = {
                                            viewModel.odgovori(
                                                idPitanja = pitanje.id,
                                                odgovor = index
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PetronasTeal,
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
                }
            }
        }
    }
}