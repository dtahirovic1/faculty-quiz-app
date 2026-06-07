package ba.etf.rma26.projekat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ba.etf.rma26.projekat.data.repositories.AccountRepository
import ba.etf.rma26.projekat.navigation.NavGraph
import ba.etf.rma26.projekat.ui.theme.FakultetskiKvizoviTheme
import ba.etf.rma26.projekat.util.AppDateProvider
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FakultetskiKvizoviTheme {
                /*LaunchedEffect(Unit) { AccountRepository.postaviHash("m155")}*/
                AppDateProvider.postaviReferentniDatum(LocalDate.of(2021, 6, 9))
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AppPreview() {
    FakultetskiKvizoviTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavGraph()
        }
    }
}
