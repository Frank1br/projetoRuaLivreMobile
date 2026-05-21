package br.edu.fatecpg.projetorualivremobile.ui.screens.changepwd

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.fatecpg.projetorualivremobile.data.repository.AuthRepository
import br.edu.fatecpg.projetorualivremobile.ui.components.RuaLivreButton
import br.edu.fatecpg.projetorualivremobile.ui.components.RuaLivreTextField
import br.edu.fatecpg.projetorualivremobile.ui.theme.IndigoPrimario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChangePwdUiState(
    val senhaAtual: String = "",
    val novaSenha: String = "",
    val confirm: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChangePwdUiState())
    val uiState: StateFlow<ChangePwdUiState> = _uiState.asStateFlow()

    fun onAtual(v: String) = _uiState.update { it.copy(senhaAtual = v, error = null) }
    fun onNova(v: String) = _uiState.update { it.copy(novaSenha = v, error = null) }
    fun onConfirm(v: String) = _uiState.update { it.copy(confirm = v, error = null) }

    fun submit() {
        val s = _uiState.value
        if (s.senhaAtual.isBlank() || s.novaSenha.length < 6) {
            _uiState.update { it.copy(error = "Preencha os campos (nova senha ≥ 6 caracteres)") }
            return
        }
        if (s.novaSenha != s.confirm) {
            _uiState.update { it.copy(error = "Confirmação não bate com a nova senha") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.trocarSenha(s.senhaAtual, s.novaSenha).fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, success = true) } },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message ?: "Falha ao trocar senha") } }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onDone: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.success) { if (state.success) onDone() }

    Scaffold(
        containerColor = Color(0xFFF3F4F8),
        topBar = {
            TopAppBar(
                title = { Text("Trocar senha", fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    navigationIconContentColor = IndigoPrimario,
                    titleContentColor = Color(0xFF1A1A2E)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RuaLivreTextField(state.senhaAtual, viewModel::onAtual, label = "Senha atual", isPassword = true)
            RuaLivreTextField(state.novaSenha, viewModel::onNova, label = "Nova senha (mín. 6)", isPassword = true)
            RuaLivreTextField(state.confirm, viewModel::onConfirm, label = "Confirmar nova senha", isPassword = true)

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            RuaLivreButton(
                text = "Salvar nova senha",
                onClick = viewModel::submit,
                isLoading = state.isLoading
            )
        }
    }
}
