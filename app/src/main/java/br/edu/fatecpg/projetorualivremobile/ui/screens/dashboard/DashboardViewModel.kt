package br.edu.fatecpg.projetorualivremobile.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.fatecpg.projetorualivremobile.data.repository.AlagamentoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val totalBairrosAlagados: Int = 0,
    val totalRuasAfetadas: Int = 0,
    val porNivel: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val alagamentoRepository: AlagamentoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        carregarDados()
    }

    private fun classificarNivel(nivelAgua: Double): String {
        return when {
            nivelAgua >= 80 -> "CRITICO"
            nivelAgua >= 60 -> "ALTO"
            nivelAgua >= 30 -> "MEDIO"
            else -> "BAIXO"
        }
    }

    fun carregarDados() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = alagamentoRepository.getAlagamentos()
            val alagamentos = result.getOrElse { emptyList() }

            val porNivel = alagamentos
                .groupBy { classificarNivel(it.nivel_agua) }
                .mapValues { it.value.size }

            val totalBairros = alagamentos.size
            val totalRuas = (totalBairros * 7) + 5

            _uiState.update {
                it.copy(
                    isLoading = false,
                    totalBairrosAlagados = totalBairros,
                    totalRuasAfetadas = totalRuas,
                    porNivel = porNivel,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
}