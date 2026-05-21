package br.edu.fatecpg.projetorualivremobile.ui.screens.forgot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.edu.fatecpg.projetorualivremobile.ui.components.RuaLivreButton
import br.edu.fatecpg.projetorualivremobile.ui.components.RuaLivreTextField
import br.edu.fatecpg.projetorualivremobile.ui.theme.IndigoPrimario

@Composable
fun ForgotPasswordScreen(
    onDone: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.step) {
        if (state.step == ForgotStep.DONE) onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IndigoPrimario)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
        ) {
            IconButton(onClick = onNavigateBack, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Recuperar senha",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 12.dp)
            )
            Text(
                text = when (state.step) {
                    ForgotStep.EMAIL -> "Informe o email cadastrado para receber um código."
                    ForgotStep.CODE_NEW_PASSWORD -> "Enviamos um código de 6 dígitos. Digite-o e defina uma nova senha."
                    ForgotStep.DONE -> ""
                },
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.78f)
                .align(Alignment.BottomCenter)
                .background(Color.White, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp, vertical = 32.dp)
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = IndigoPrimario.copy(alpha = 0.10f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.LockReset,
                            contentDescription = null,
                            tint = IndigoPrimario,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                when (state.step) {
                    ForgotStep.EMAIL -> {
                        RuaLivreTextField(
                            value = state.email,
                            onValueChange = viewModel::onEmailChange,
                            label = "Email",
                            placeholder = "seu@email.com",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                        state.error?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        RuaLivreButton(
                            text = "Enviar código",
                            onClick = viewModel::enviarCodigo,
                            isLoading = state.isLoading
                        )
                    }

                    ForgotStep.CODE_NEW_PASSWORD -> {
                        RuaLivreTextField(
                            value = state.codigo,
                            onValueChange = viewModel::onCodigoChange,
                            label = "Código de 6 dígitos",
                            placeholder = "000000",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        RuaLivreTextField(
                            value = state.novaSenha,
                            onValueChange = viewModel::onNovaSenhaChange,
                            label = "Nova senha",
                            isPassword = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        RuaLivreTextField(
                            value = state.confirmSenha,
                            onValueChange = viewModel::onConfirmSenhaChange,
                            label = "Confirmar nova senha",
                            isPassword = true
                        )
                        state.error?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        RuaLivreButton(
                            text = "Redefinir senha",
                            onClick = viewModel::confirmarReset,
                            isLoading = state.isLoading
                        )
                    }

                    ForgotStep.DONE -> Unit
                }
            }
        }
    }
}
