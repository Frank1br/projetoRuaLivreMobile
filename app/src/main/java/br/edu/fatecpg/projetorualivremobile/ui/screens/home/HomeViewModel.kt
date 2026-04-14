package br.edu.fatecpg.projetorualivremobile.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.fatecpg.projetorualivremobile.data.model.Alerta
import br.edu.fatecpg.projetorualivremobile.data.model.NivelAlagamento
import br.edu.fatecpg.projetorualivremobile.data.repository.AlagamentoRepository
import br.edu.fatecpg.projetorualivremobile.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val nomeUsuario: String = "",
    val totalBairros: Int = 0,
    val pctAlagados: Int = 0,
    val pctAfetados: Int = 0,
    val pctLivres: Int = 0,
    val alertas: List<Alerta> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val alagamentoRepository: AlagamentoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        carregarDados()
    }

    fun carregarDados() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val nomeUsuario = authRepository.currentUsuario?.nome ?: "usuário"
            val alagamentosResult = alagamentoRepository.getAlagamentos()
            val alertasResult = alagamentoRepository.getAlertas()
            val alagamentos = alagamentosResult.getOrElse { emptyList() }

            val total = alagamentos.size.coerceAtLeast(1)
            val criticos = alagamentos.count { it.nivel == NivelAlagamento.CRITICO || it.nivel == NivelAlagamento.ALTO }
            val medios = alagamentos.count { it.nivel == NivelAlagamento.MEDIO }
            val livres = total - criticos - medios

            _uiState.update {
                it.copy(
                    isLoading = false,
                    nomeUsuario = nomeUsuario,
                    totalBairros = alagamentos.size,
                    pctAlagados = ((criticos.toFloat() / total) * 100).toInt(),
                    pctAfetados = ((medios.toFloat() / total) * 100).toInt(),
                    pctLivres = ((livres.coerceAtLeast(0).toFloat() / total) * 100).toInt(),
                    alertas = alertasResult.getOrElse { emptyList() },
                    error = alagamentosResult.exceptionOrNull()?.message
                )
            }
        }
    }
}