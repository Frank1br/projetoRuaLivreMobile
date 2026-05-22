package br.edu.fatecpg.projetorualivremobile.ui.screens.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.fatecpg.projetorualivremobile.data.repository.AuthRepository
import br.edu.fatecpg.projetorualivremobile.util.ErrorMessages
import br.edu.fatecpg.projetorualivremobile.util.PasswordValidator
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
    val passwordRequirements: PasswordValidator.PasswordRequirements = PasswordValidator.PasswordRequirements(),
    val confirmSenhaError: String? = null,
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

    fun onCodigoChange(v: String) =
        _uiState.update { it.copy(codigo = v.filter(Char::isDigit).take(6), error = null) }

    fun onNovaSenhaChange(v: String) {
        val requisitos = PasswordValidator.checkRequirements(v)
        val confirmErro = if (_uiState.value.confirmSenha.isNotEmpty() && v != _uiState.value.confirmSenha)
            "As senhas não coincidem" else null
        _uiState.update {
            it.copy(
                novaSenha = v,
                error = null,
                passwordRequirements = requisitos,
                confirmSenhaError = confirmErro
            )
        }
    }

    fun onConfirmSenhaChange(v: String) {
        val confirmErro = if (v.isNotEmpty() && v != _uiState.value.novaSenha)
            "As senhas não coincidem" else null
        _uiState.update { it.copy(confirmSenha = v, confirmSenhaError = confirmErro, error = null) }
    }

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
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = ErrorMessages.from(e)) } }
            )
        }
    }

    fun confirmarReset() {
        val state = _uiState.value
        if (state.codigo.length != 6) {
            _uiState.update { it.copy(error = "O código deve ter 6 dígitos") }
            return
        }
        if (state.confirmSenha.isBlank()) {
            _uiState.update { it.copy(error = "Confirme a nova senha") }
            return
        }
        if (state.novaSenha != state.confirmSenha) {
            _uiState.update { it.copy(confirmSenhaError = "As senhas não coincidem") }
            return
        }
        // Mesmas regras da criação de conta.
        val validacao = PasswordValidator.validate(state.novaSenha, email = state.email)
        if (validacao.isFailure) {
            _uiState.update { it.copy(error = validacao.exceptionOrNull()?.message) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            // O backend rejeita se a nova senha for igual à anterior.
            authRepository.resetarSenha(state.email, state.codigo, state.novaSenha).fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, step = ForgotStep.DONE) } },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = ErrorMessages.from(e)) } }
            )
        }
    }
}
