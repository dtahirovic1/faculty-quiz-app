package ba.etfrma.fakultetskikvizovi.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ba.etfrma.fakultetskikvizovi.ui.screens.FilterScreen
import ba.etfrma.fakultetskikvizovi.ui.screens.KvizoviScreen
import ba.etfrma.fakultetskikvizovi.viewmodel.KvizViewModel
import ba.etfrma.fakultetskikvizovi.viewmodel.UpisViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    val kvizViewModel: KvizViewModel = viewModel()
    val upisViewModel: UpisViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "filter"
    ) {
        composable("filter") {
            FilterScreen(
                kvizViewModel = kvizViewModel,
                upisViewModel = upisViewModel,
                onPrikaziKvizove = { navController.navigate("kvizovi") }
            )
        }
        composable("kvizovi") {
            KvizoviScreen(
                kvizViewModel = kvizViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}