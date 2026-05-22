package br.edu.fatecpg.projetorualivremobile.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.draw.scale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import br.edu.fatecpg.projetorualivremobile.ui.theme.IndigoPrimario
import br.edu.fatecpg.projetorualivremobile.util.PasswordValidator

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
    // Micro-interação: o botão encolhe levemente enquanto pressionado.
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(120),
        label = "buttonScale"
    )
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .scale(scale),
        enabled = enabled && !isLoading,
        interactionSource = interactionSource,
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
// Checklist de requisitos de senha. Compartilhado entre cadastro e
// recuperação de senha para manter as mesmas regras em todo o sistema.
@Composable
fun PasswordRequirementsChecklist(
    requirements: PasswordValidator.PasswordRequirements,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(start = 4.dp)) {
        Text(
            text = "Requisitos da senha:",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF666680),
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        RequirementItem("Mínimo 8 caracteres", requirements.hasMinLength)
        RequirementItem("Pelo menos uma letra maiúscula (A-Z)", requirements.hasUppercase)
        RequirementItem("Pelo menos uma letra minúscula (a-z)", requirements.hasLowercase)
        RequirementItem("Pelo menos um número (0-9)", requirements.hasDigit)
        RequirementItem("Pelo menos um caractere especial (!@#\$...)", requirements.hasSpecialChar)
        RequirementItem("Sem espaços no início ou no fim", requirements.noLeadingTrailingSpaces)
    }
}

@Composable
private fun RequirementItem(label: String, isMet: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isMet) Color(0xFF4CAF50) else Color(0xFFAAAAAA),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isMet) Color(0xFF4CAF50) else Color(0xFF888888)
        )
    }
}
