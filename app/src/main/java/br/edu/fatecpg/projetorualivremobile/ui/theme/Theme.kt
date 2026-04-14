package br.edu.fatecpg.projetorualivremobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val IndigoPrimario = Color(0xFF2B2B7C)
val IndigoSecundario = Color(0xFF3D3DB8)
val IndigoClaro = Color(0xFF5B5BD6)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimario,
    secondary = IndigoSecundario,
    tertiary = IndigoClaro,
    error = Color(0xFFD32F2F),
    background = Color(0xFFF3F4F8),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1A1A2E),
    onSurface = Color(0xFF1A1A2E),
)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoClaro,
    secondary = IndigoSecundario,
    tertiary = IndigoPrimario,
    error = Color(0xFFEF9A9A),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFFEEEEEE),
    onSurface = Color(0xFFEEEEEE),
)

val AlertaAltoColor = Color(0xFFE53935)
val AlertaMedioColor = Color(0xFFF57C00)
val AlertaBaixoColor = Color(0xFF2E7D32)
val AlertaColor = Color(0xFFD32F2F)
val AtencaoColor = Color(0xFFF57C00)
val SeguroColor = Color(0xFF2E7D32)

@Composable
fun RuaLivreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RuaLivreTypography,
        content = content
    )
}