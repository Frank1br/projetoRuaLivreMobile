package br.edu.fatecpg.projetorualivremobile.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.fatecpg.projetorualivremobile.data.model.Alerta
import br.edu.fatecpg.projetorualivremobile.data.model.NivelAlagamento
import br.edu.fatecpg.projetorualivremobile.data.repository.AlagamentoRepository
import br.edu.fatecpg.projetorualivremobile.data.repository.AuthRepository
import br.edu.fatecpg.projetorualivremobile.data.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val nomeUsuario: String = "",
    val bairrosAtingidos: Int = 0,
    val totalBairrosMonitorados: Int = 0,
    val pctAtingidos: Int = 0,
    val totalAlagamentos: Int = 0,
    val nCriticoAlto: Int = 0,
    val nMedio: Int = 0,
    val nBaixo: Int = 0,
    val alertas: List<Alerta> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val alagamentoRepository: AlagamentoRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        carregarDados()
    }

    fun carregarDados() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val nomeUsuario = authRepository.currentUsuario?.nome ?: "Usuário"

            val alagamentosDeferred = async { alagamentoRepository.getAlagamentos() }
            val alertasDeferred = async { alagamentoRepository.getAlertas() }
            val statsDeferred = async { dashboardRepository.getStats() }

            val alagamentosResult = alagamentosDeferred.await()
            val alertasResult = alertasDeferred.await()
            val statsResult = statsDeferred.await()

            val alagamentos = alagamentosResult.getOrElse { emptyList() }
            val stats = statsResult.getOrNull()

            // Bairros distintos com alagamento ativo (ignora os sem bairro identificado).
            val bairrosAtingidos = alagamentos
                .mapNotNull { it.bairro?.takeIf { b -> b.isNotBlank() } }
                .toSet()
                .size

            val total = stats?.totalBairrosMonitorados ?: 0
            val pct = if (total > 0) (bairrosAtingidos * 100f / total).toInt() else 0

            val nCriticoAlto = alagamentos.count {
                it.nivel == NivelAlagamento.CRITICO || it.nivel == NivelAlagamento.ALTO
            }
            val nMedio = alagamentos.count { it.nivel == NivelAlagamento.MEDIO }
            val nBaixo = alagamentos.count { it.nivel == NivelAlagamento.BAIXO }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    nomeUsuario = nomeUsuario,
                    bairrosAtingidos = bairrosAtingidos,
                    totalBairrosMonitorados = total,
                    pctAtingidos = pct,
                    totalAlagamentos = alagamentos.size,
                    nCriticoAlto = nCriticoAlto,
                    nMedio = nMedio,
                    nBaixo = nBaixo,
                    alertas = alertasResult.getOrElse { emptyList() },
                    error = alagamentosResult.exceptionOrNull()?.message
                        ?: statsResult.exceptionOrNull()?.message
                )
            }
        }
    }
}
