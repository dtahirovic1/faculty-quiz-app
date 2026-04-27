package ba.etfrma.fakultetskikvizovi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import ba.etfrma.fakultetskikvizovi.ui.components.KvizCard
import ba.etfrma.fakultetskikvizovi.viewmodel.KvizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KvizoviScreen(
    kvizViewModel: KvizViewModel,
    onBack: () -> Unit
) {
    val kvizovi        by kvizViewModel.kvizovi.collectAsState()
    val selectedFilter by kvizViewModel.selectedFilter.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = selectedFilter.naziv) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("listaKvizova")
        ) {
            items(kvizovi) { kviz ->
                KvizCard(kviz = kviz)
            }
        }
    }
}