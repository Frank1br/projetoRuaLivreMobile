package br.edu.fatecpg.projetorualivremobile.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.fatecpg.projetorualivremobile.data.model.HistoricoEntry
import br.edu.fatecpg.projetorualivremobile.data.model.NivelAlagamento
import br.edu.fatecpg.projetorualivremobile.data.model.RegiaoCount
import br.edu.fatecpg.projetorualivremobile.data.repository.AlagamentoRepository
import br.edu.fatecpg.projetorualivremobile.data.repository.DashboardRepository
import br.edu.fatecpg.projetorualivremobile.util.ErrorMessages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val alagamentosAtivos: Int = 0,
    val camerasAtivas: Int = 0,
    val alertasHoje: Int = 0,
    val porNivel: Map<NivelAlagamento, Int> = emptyMap(),
    val porRegiao: List<RegiaoCount> = emptyList(),
    val historico: List<HistoricoEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val alagamentoRepository: AlagamentoRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        carregarDados()
    }

    fun carregarDados() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val statsResult = dashboardRepository.getStats()
            val historicoResult = dashboardRepository.getHistorico(180)
            val alagamentosResult = alagamentoRepository.getAlagamentos()

            val stats = statsResult.getOrNull()
            val alagamentos = alagamentosResult.getOrElse { emptyList() }
            val porNivel = alagamentos
                .groupBy { it.nivel }
                .mapValues { it.value.size }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    alagamentosAtivos = stats?.totalAlagamentosAtivos ?: alagamentos.size,
                    camerasAtivas = stats?.totalCamerasAtivas ?: 0,
                    alertasHoje = stats?.totalAlertasHoje ?: 0,
                    porNivel = porNivel,
                    porRegiao = stats?.alagamentosPorRegiao ?: emptyList(),
                    historico = historicoResult.getOrElse { emptyList() },
                    error = (statsResult.exceptionOrNull() ?: alagamentosResult.exceptionOrNull())
                        ?.let { e -> ErrorMessages.from(e) }
                )
            }
        }
    }
}
