package ba.etf.rma26.projekat.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import ba.etf.rma26.projekat.data.models.KvizStatus
import ba.etf.rma26.projekat.ui.components.KvizCard
import ba.etf.rma26.projekat.util.Messages
import ba.etf.rma26.projekat.viewmodel.KvizViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KvizoviScreen(
    kvizViewModel: KvizViewModel,
    onBack: () -> Unit,
    onKvizClick: (Int) -> Unit = {}
) {
    val kvizovi by kvizViewModel.kvizovi.collectAsState()
    val selectedFilter by kvizViewModel.selectedFilter.collectAsState()
    val pokusajiPoKvizu by kvizViewModel.pokusajiPoKvizu.collectAsState()


    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = selectedFilter.naziv)
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("listaKvizova")
        ) {
            items(kvizovi) { kviz ->
                KvizCard(
                    kviz = kviz,
                    pokusaj = pokusajiPoKvizu[kviz.id],
                    onClick = {
                        onKvizClick(kviz.id)
                    },
                    onNeaktivanClick = { status ->
                        scope.launch {
                            snackbarHostState.showSnackbar(porukaZaStatus(status))
                        }
                    }
                )
            }
        }
    }
}
private fun porukaZaStatus(status: KvizStatus): String {
    return Messages.porukaZaStatus(status)
}