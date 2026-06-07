package ba.etf.rma26.projekat.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ba.etf.rma26.projekat.ui.screens.FilterScreen
import ba.etf.rma26.projekat.ui.screens.KvizDetaljiScreen
import ba.etf.rma26.projekat.ui.screens.KvizoviScreen
import ba.etf.rma26.projekat.viewmodel.KvizDetaljiViewModel
import ba.etf.rma26.projekat.viewmodel.KvizViewModel
import ba.etf.rma26.projekat.viewmodel.UpisViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    val kvizViewModel: KvizViewModel = viewModel()
    val upisViewModel: UpisViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "filter"
    ) {
        composable("filter") {
            FilterScreen(
                upisViewModel = upisViewModel,
                kvizViewModel = kvizViewModel,
                onPrikaziKvizove = {
                    navController.navigate("kvizovi")
                }
            )
        }

        composable("kvizovi") {
            KvizoviScreen(
                kvizViewModel = kvizViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onKvizClick = { idKviza ->
                    navController.navigate("kvizDetalji/$idKviza")
                }
            )
        }

        composable(
            route = "kvizDetalji/{idKviza}",
            arguments = listOf(
                navArgument("idKviza") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val idKviza = backStackEntry.arguments?.getInt("idKviza") ?: return@composable

            val detaljiViewModel: KvizDetaljiViewModel = viewModel()

            KvizDetaljiScreen(
                idKviza = idKviza,
                viewModel = detaljiViewModel,
                onBack = {
                    kvizViewModel.osvjezi()
                    navController.popBackStack()
                }
            )
        }
    }
}