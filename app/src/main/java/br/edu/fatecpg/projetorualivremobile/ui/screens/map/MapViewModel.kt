package br.edu.fatecpg.projetorualivremobile.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.fatecpg.projetorualivremobile.data.model.Alagamento
import br.edu.fatecpg.projetorualivremobile.data.model.Camera
import br.edu.fatecpg.projetorualivremobile.data.repository.AlagamentoRepository
import br.edu.fatecpg.projetorualivremobile.data.repository.CameraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val cameras: List<Camera> = emptyList(),
    val alagamentos: List<Alagamento> = emptyList(),
    val selectedCamera: Camera? = null,
    val selectedAlagamento: Alagamento? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val alagamentoRepository: AlagamentoRepository,
    private val cameraRepository: CameraRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        carregarDados()
    }

    fun carregarDados() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val alagamentosDeferred = async { alagamentoRepository.getAlagamentos() }
            val camerasDeferred = async { cameraRepository.getCameras() }

            val alagamentosResult = alagamentosDeferred.await()
            val camerasResult = camerasDeferred.await()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    alagamentos = alagamentosResult.getOrElse { emptyList() },
                    cameras = camerasResult.getOrElse { emptyList() },
                    error = alagamentosResult.exceptionOrNull()?.message
                        ?: camerasResult.exceptionOrNull()?.message
                )
            }
        }
    }

    fun selectCamera(camera: Camera) {
        _uiState.update { it.copy(selectedCamera = camera, selectedAlagamento = null) }
    }

    fun selectAlagamento(alagamento: Alagamento) {
        _uiState.update { it.copy(selectedAlagamento = alagamento, selectedCamera = null) }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedCamera = null, selectedAlagamento = null) }
    }
}