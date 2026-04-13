package ba.etfrma.fakultetskikvizovi.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary          = PetronasTeal,
    onPrimary        = Color(0xFF0A0A0A),
    background       = DeepGraphite,
    surface          = SurfaceDark,
    surfaceVariant   = CardGraphite,
    onBackground     = SilverWhite,
    onSurface        = SilverWhite,
    onSurfaceVariant = AMGSilver,
    outline          = BorderSubtle
)

private val LightColorScheme = darkColorScheme(
    primary          = PetronasTeal,
    onPrimary        = Color(0xFF0A0A0A),
    background       = DeepGraphite,
    surface          = SurfaceDark,
    surfaceVariant   = CardGraphite,
    onBackground     = SilverWhite,
    onSurface        = SilverWhite,
    onSurfaceVariant = AMGSilver,
    outline          = BorderSubtle
)

@Composable
fun FakultetskiKvizoviTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}