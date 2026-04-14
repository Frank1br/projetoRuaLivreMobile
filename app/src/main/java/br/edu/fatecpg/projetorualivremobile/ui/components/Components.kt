package br.edu.fatecpg.projetorualivremobile.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import br.edu.fatecpg.projetorualivremobile.ui.theme.IndigoPrimario

private val BorderUnfocused = Color(0xFFDDDDE8)
private val LabelUnfocused = Color(0xFF9999AA)
private val PlaceholderColor = Color(0xFFBBBBCC)

@Composable
fun BottomBar(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = onNavigateToHome,
            icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
            label = { Text("Início") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = IndigoPrimario,
                selectedTextColor = IndigoPrimario,
                unselectedIconColor = Color(0xFF9999AA),
                unselectedTextColor = Color(0xFF9999AA),
                indicatorColor = IndigoPrimario.copy(alpha = 0.12f)
            )
        )
        NavigationBarItem(
            selected = currentRoute == "map",
            onClick = onNavigateToMap,
            icon = { Icon(Icons.Default.Map, contentDescription = "Mapa") },
            label = { Text("Mapa") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = IndigoPrimario,
                selectedTextColor = IndigoPrimario,
                unselectedIconColor = Color(0xFF9999AA),
                unselectedTextColor = Color(0xFF9999AA),
                indicatorColor = IndigoPrimario.copy(alpha = 0.12f)
            )
        )
        NavigationBarItem(
            selected = currentRoute == "dashboard",
            onClick = onNavigateToDashboard,
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dados") },
            label = { Text("Dados") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = IndigoPrimario,
                selectedTextColor = IndigoPrimario,
                unselectedIconColor = Color(0xFF9999AA),
                unselectedTextColor = Color(0xFF9999AA),
                indicatorColor = IndigoPrimario.copy(alpha = 0.12f)
            )
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = onNavigateToProfile,
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = IndigoPrimario,
                selectedTextColor = IndigoPrimario,
                unselectedIconColor = Color(0xFF9999AA),
                unselectedTextColor = Color(0xFF9999AA),
                indicatorColor = IndigoPrimario.copy(alpha = 0.12f)
            )
        )
    }
}

@Composable
fun RuaLivreTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = if (placeholder.isNotEmpty()) {
            { Text(placeholder, color = PlaceholderColor) }
        } else null,
        modifier = modifier.fillMaxWidth(),
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha",
                        tint = LabelUnfocused
                    )
                }
            }
        } else null,
        keyboardOptions = keyboardOptions,
        isError = isError,
        supportingText = if (isError && errorMessage != null) {
            { Text(errorMessage) }
        } else null,
        singleLine = true,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            // Focused
            focusedBorderColor = IndigoPrimario,
            focusedLabelColor = IndigoPrimario,
            focusedTextColor = Color(0xFF1A1A2E),
            focusedContainerColor = Color.White,
            // Unfocused
            unfocusedBorderColor = BorderUnfocused,
            unfocusedLabelColor = LabelUnfocused,
            unfocusedTextColor = Color(0xFF1A1A2E),
            unfocusedContainerColor = Color.White,
            // Cursor
            cursorColor = IndigoPrimario,
            // Error
            errorBorderColor = Color(0xFFD32F2F),
            errorLabelColor = Color(0xFFD32F2F),
            errorContainerColor = Color.White,
        )
    )
}

@Composable
fun RuaLivreButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled && !isLoading,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = IndigoPrimario,
            contentColor = Color.White,
            disabledContainerColor = IndigoPrimario.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
        } else {
            Text(text)
        }
    }
}