package br.edu.fatecpg.projetorualivremobile.ui.screens.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.fatecpg.projetorualivremobile.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ForgotStep { EMAIL, CODE_NEW_PASSWORD, DONE }

data class ForgotUiState(
    val step: ForgotStep = ForgotStep.EMAIL,
    val email: String = "",
    val codigo: String = "",
    val novaSenha: String = "",
    val confirmSenha: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotUiState())
    val uiState: StateFlow<ForgotUiState> = _uiState.asStateFlow()

    fun onEmailChange(v: String) = _uiState.update { it.copy(email = v, error = null) }
    fun onCodigoChange(v: String) = _uiState.update { it.copy(codigo = v.filter(Char::isDigit).take(6), error = null) }
    fun onNovaSenhaChange(v: String) = _uiState.update { it.copy(novaSenha = v, error = null) }
    fun onConfirmSenhaChange(v: String) = _uiState.update { it.copy(confirmSenha = v, error = null) }

    fun enviarCodigo() {
        val state = _uiState.value
        if (state.email.isBlank()) {
            _uiState.update { it.copy(error = "Informe o email cadastrado") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.esqueciSenha(state.email).fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, step = ForgotStep.CODE_NEW_PASSWORD) } },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message ?: "Falha ao enviar código") } }
            )
        }
    }

    fun confirmarReset() {
        val state = _uiState.value
        if (state.codigo.length != 6) {
            _uiState.update { it.copy(error = "Código deve ter 6 dígitos") }
            return
        }
        if (state.novaSenha.length < 6) {
            _uiState.update { it.copy(error = "Senha deve ter ao menos 6 caracteres") }
            return
        }
        if (state.novaSenha != state.confirmSenha) {
            _uiState.update { it.copy(error = "Senhas não coincidem") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.resetarSenha(state.email, state.codigo, state.novaSenha).fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, step = ForgotStep.DONE) } },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message ?: "Falha ao redefinir senha") } }
            )
        }
    }
}
