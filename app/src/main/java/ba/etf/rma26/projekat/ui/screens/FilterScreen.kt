package ba.etf.rma26.projekat.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ba.etf.rma26.projekat.viewmodel.FilterOpcija
import ba.etf.rma26.projekat.viewmodel.KvizViewModel
import ba.etf.rma26.projekat.viewmodel.UpisViewModel
import kotlinx.coroutines.launch

private val PetronasTeal = Color(0xFF00A19C)
private val DisabledBg = Color(0xFF2E2E2E)
private val DisabledText = Color(0xFF8A8A8A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    upisViewModel: UpisViewModel,
    kvizViewModel: KvizViewModel,
    onPrikaziKvizove: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val odabranaGodina by upisViewModel.odabranaGodina.collectAsState()
    val odabranPredmet by upisViewModel.odabranPredmet.collectAsState()
    val odabranaGrupa by upisViewModel.odabranaGrupa.collectAsState()
    val dostupniPredmeti by upisViewModel.dostupniPredmeti.collectAsState()
    val dostupneGrupe by upisViewModel.dostupneGrupe.collectAsState()
    val mozeDaSeUpise by upisViewModel.mozeDaSeUpise.collectAsState()

    val selectedFilter by kvizViewModel.selectedFilter.collectAsState()
    val kvizovi by kvizViewModel.kvizovi.collectAsState()

    var godinaOtvorena by remember { mutableStateOf(false) }
    var predmetOtvoren by remember { mutableStateOf(false) }
    var grupaOtvorena by remember { mutableStateOf(false) }
    var filterOtvoren by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Upis na predmet",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = godinaOtvorena,
            onExpandedChange = { godinaOtvorena = !godinaOtvorena },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("odabirGodina")
        ) {
            OutlinedTextField(
                value = odabranaGodina?.toString() ?: "Odaberi godinu",
                onValueChange = {},
                readOnly = true,
                label = { Text("Godina studija") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = godinaOtvorena)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = godinaOtvorena,
                onDismissRequest = { godinaOtvorena = false }
            ) {
                listOf(2, 3, 4, 5).forEach { godina ->
                    DropdownMenuItem(
                        text = { Text(godina.toString()) },
                        onClick = {
                            upisViewModel.odaberiGodinu(godina)
                            godinaOtvorena = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = predmetOtvoren,
            onExpandedChange = {
                if (odabranaGodina != null) {
                    predmetOtvoren = !predmetOtvoren
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("odabirPredmet")
        ) {
            OutlinedTextField(
                value = odabranPredmet?.naziv ?: "Odaberi predmet",
                onValueChange = {},
                readOnly = true,
                enabled = odabranaGodina != null,
                label = { Text("Predmet") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = predmetOtvoren)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = predmetOtvoren,
                onDismissRequest = { predmetOtvoren = false }
            ) {
                if (dostupniPredmeti.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Nema dostupnih predmeta") },
                        onClick = { predmetOtvoren = false }
                    )
                } else {
                    dostupniPredmeti.forEach { predmet ->
                        DropdownMenuItem(
                            text = { Text(predmet.naziv) },
                            onClick = {
                                upisViewModel.odaberiPredmet(predmet)
                                predmetOtvoren = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = grupaOtvorena,
            onExpandedChange = {
                if (odabranPredmet != null) {
                    grupaOtvorena = !grupaOtvorena
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("odabirGrupa")
        ) {
            OutlinedTextField(
                value = odabranaGrupa?.naziv ?: "Odaberi grupu",
                onValueChange = {},
                readOnly = true,
                enabled = odabranPredmet != null,
                label = { Text("Grupa") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = grupaOtvorena)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = grupaOtvorena,
                onDismissRequest = { grupaOtvorena = false }
            ) {
                dostupneGrupe.forEach { grupa ->
                    DropdownMenuItem(
                        text = { Text(grupa.naziv) },
                        onClick = {
                            upisViewModel.odaberiGrupu(grupa)
                            grupaOtvorena = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                scope.launch {
                    val uspjeh = upisViewModel.upisise()

                    if (uspjeh) {
                        kvizViewModel.osvjezi()
                    }
                }
            },
            enabled = mozeDaSeUpise,
            colors = ButtonDefaults.buttonColors(
                containerColor = PetronasTeal,
                contentColor = Color(0xFF0A0A0A),
                disabledContainerColor = DisabledBg,
                disabledContentColor = DisabledText
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dodajPredmetDugme")
        ) {
            Text("Upiši me")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Filteri",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = filterOtvoren,
            onExpandedChange = { filterOtvoren = !filterOtvoren },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("filterKvizova")
        ) {
            OutlinedTextField(
                value = selectedFilter.naziv,
                onValueChange = {},
                readOnly = true,
                label = { Text("Filter") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterOtvoren)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = filterOtvoren,
                onDismissRequest = { filterOtvoren = false }
            ) {
                FilterOpcija.entries.forEach { opcija ->
                    DropdownMenuItem(
                        text = { Text(opcija.naziv) },
                        onClick = {
                            kvizViewModel.promijeniFilter(opcija)
                            filterOtvoren = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Pronađeno je ${kvizovi.size} kvizova",
            fontSize = 14.sp,
            modifier = Modifier.testTag("brojKvizova")
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onPrikaziKvizove,
            colors = ButtonDefaults.buttonColors(
                containerColor = PetronasTeal,
                contentColor = Color(0xFF0A0A0A)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("prikaziKvizoveDugme")
        ) {
            Text("Prikaži kvizove")
        }
    }
}