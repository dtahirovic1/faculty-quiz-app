package ba.etf.rma26.projekat.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import ba.etf.rma26.projekat.data.repositories.AccountRepository
import ba.etf.rma26.projekat.ui.theme.AMGSilver
import ba.etf.rma26.projekat.util.Messages
import kotlinx.coroutines.launch

private val PetronasTeal = Color(0xFF00A19C)

@Composable
fun AccountScreen(
    onNastavi: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var poruka by remember { mutableStateOf<String?>(null) }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
    ) {
        Text(
            text = "Prijava korisnika",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Unesite username za rad sa kvizovima.",
            fontSize = 14.sp,
            color = AMGSilver
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                poruka = null
            },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("usernameInput")
        )

        poruka?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.testTag("usernamePoruka")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    val uspjeh = AccountRepository.postaviHash(username)

                    if (uspjeh) {
                        onNastavi()
                    } else {
                        poruka = Messages.USERNAME_OBAVEZAN
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = PetronasTeal,
                contentColor = Color(0xFF0A0A0A)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("nastaviDugme")
        ) {
            Text("Nastavi")
        }
    }
}
