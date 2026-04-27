package ba.etfrma.fakultetskikvizovi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ba.etfrma.fakultetskikvizovi.ui.theme.DisabledBg
import ba.etfrma.fakultetskikvizovi.ui.theme.DisabledText
import ba.etfrma.fakultetskikvizovi.ui.theme.PetronasTeal
import ba.etfrma.fakultetskikvizovi.viewmodel.FilterOpcija
import ba.etfrma.fakultetskikvizovi.viewmodel.KvizViewModel
import ba.etfrma.fakultetskikvizovi.viewmodel.UpisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    kvizViewModel: KvizViewModel,
    upisViewModel: UpisViewModel,
    onPrikaziKvizove: () -> Unit
) {
    val odabranaGodina    by upisViewModel.odabranaGodina.collectAsState()
    val odabranPredmet    by upisViewModel.odabranPredmet.collectAsState()
    val odabranaGrupa     by upisViewModel.odabranaGrupa.collectAsState()
    val dostupniPredmeti  by upisViewModel.dostupniPredmeti.collectAsState()
    val dostupneGrupe     by upisViewModel.dostupneGrupe.collectAsState()
    val mozeDaSeUpise     by upisViewModel.mozeDaSeUpise.collectAsState()

    val selectedFilter by kvizViewModel.selectedFilter.collectAsState()
    val kvizovi        by kvizViewModel.kvizovi.collectAsState()

    var godinaOtvorena by remember { mutableStateOf(false) }
    var predmetOtvoren by remember { mutableStateOf(false) }
    var grupaOtvorena  by remember { mutableStateOf(false) }
    var filterOtvoren  by remember { mutableStateOf(false) }

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
                (1..5).forEach { godina ->
                    DropdownMenuItem(
                        text = { Text("$godina. godina") },
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
                if (odabranaGodina != null) predmetOtvoren = !predmetOtvoren
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
                if (odabranPredmet != null) grupaOtvorena = !grupaOtvorena
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
                upisViewModel.upisise()
                kvizViewModel.promijeniFilter(kvizViewModel.selectedFilter.value)
            },
            enabled = mozeDaSeUpise,
            colors = ButtonDefaults.buttonColors(
                containerColor         = PetronasTeal,
                contentColor           = Color(0xFF0A0A0A),
                disabledContainerColor = DisabledBg,
                disabledContentColor   = DisabledText
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
                contentColor   = Color(0xFF0A0A0A)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("prikaziKvizoveDugme")
        ) {
            Text("Prikaži kvizove")
        }
    }
}