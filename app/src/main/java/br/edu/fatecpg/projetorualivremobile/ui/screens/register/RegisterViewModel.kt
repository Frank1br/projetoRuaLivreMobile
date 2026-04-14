package br.edu.fatecpg.projetorualivremobile.ui.screens.register

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

data class RegisterUiState(
    val usuario: String = "",
    val email: String = "",
    val senha: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegisterSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onUsuarioChange(value: String) = _uiState.update { it.copy(usuario = value, error = null) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, error = null) }
    fun onSenhaChange(value: String) = _uiState.update { it.copy(senha = value, error = null) }

    fun register() {
        val state = _uiState.value
        if (state.usuario.isBlank() || state.email.isBlank() || state.senha.isBlank()) {
            _uiState.update { it.copy(error = "Preencha todos os campos") }
            return
        }
        if (state.senha.length < 8) {
            _uiState.update { it.copy(error = "A senha deve ter no mínimo 8 caracteres") }
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