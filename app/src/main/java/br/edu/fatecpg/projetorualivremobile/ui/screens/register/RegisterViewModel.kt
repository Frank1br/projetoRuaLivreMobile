package br.edu.fatecpg.projetorualivremobile.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.fatecpg.projetorualivremobile.data.repository.AuthRepository
import br.edu.fatecpg.projetorualivremobile.util.PasswordValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val usuario: String = "",
    val email: String = "",
    val senha: String = "",
    val confirmSenha: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegisterSuccess: Boolean = false,
    val passwordRequirements: PasswordValidator.PasswordRequirements = PasswordValidator.PasswordRequirements(),
    val confirmSenhaError: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onUsuarioChange(value: String) =
        _uiState.update { it.copy(usuario = value, error = null) }

    fun onEmailChange(value: String) =
        _uiState.update { it.copy(email = value, error = null) }

    fun onSenhaChange(value: String) {
        val requirements = PasswordValidator.checkRequirements(value)
        val confirmError = if (_uiState.value.confirmSenha.isNotEmpty() && value != _uiState.value.confirmSenha)
            "As senhas não coincidem" else null
        _uiState.update {
            it.copy(
                senha = value,
                error = null,
                passwordRequirements = requirements,
                confirmSenhaError = confirmError
            )
        }
    }

    fun onConfirmSenhaChange(value: String) {
        val confirmError = if (value.isNotEmpty() && value != _uiState.value.senha)
            "As senhas não coincidem" else null
        _uiState.update { it.copy(confirmSenha = value, confirmSenhaError = confirmError, error = null) }
    }

    fun register() {
        val state = _uiState.value

        if (state.usuario.isBlank() || state.email.isBlank() || state.senha.isBlank()) {
            _uiState.update { it.copy(error = "Preencha todos os campos") }
            return
        }

        if (state.confirmSenha.isBlank()) {
            _uiState.update { it.copy(error = "Confirme sua senha") }
            return
        }

        if (state.senha != state.confirmSenha) {
            _uiState.update { it.copy(confirmSenhaError = "As senhas não coincidem") }
            return
        }

        val validation = PasswordValidator.validate(state.senha, state.usuario, state.email)
        if (validation.isFailure) {
            _uiState.update { it.copy(error = validation.exceptionOrNull()?.message) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.register(state.usuario, state.email, state.senha, "")
            result.fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, isRegisterSuccess = true) } },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message ?: "Erro ao cadastrar") } }
            )
        }
    }
}