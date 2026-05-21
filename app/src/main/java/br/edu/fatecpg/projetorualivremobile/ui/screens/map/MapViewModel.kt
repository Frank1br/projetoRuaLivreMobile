package br.edu.fatecpg.projetorualivremobile.ui.screens.map

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.fatecpg.projetorualivremobile.data.model.Alagamento
import br.edu.fatecpg.projetorualivremobile.data.model.AlagamentoReportado
import br.edu.fatecpg.projetorualivremobile.data.model.Camera
import br.edu.fatecpg.projetorualivremobile.data.repository.AlagamentoRepository
import br.edu.fatecpg.projetorualivremobile.data.repository.CameraRepository
import br.edu.fatecpg.projetorualivremobile.data.repository.ReportRepository
import br.edu.fatecpg.projetorualivremobile.util.AuthEventBus
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class MapUiState(
    val cameras: List<Camera> = emptyList(),
    val alagamentos: List<Alagamento> = emptyList(),
    val reports: List<AlagamentoReportado> = emptyList(),
    val selectedCamera: Camera? = null,
    val selectedAlagamento: Alagamento? = null,
    val selectedReport: AlagamentoReportado? = null,
    val isLoading: Boolean = false,
    val isSubmittingReport: Boolean = false,
    val reportError: String? = null,
    val reportSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val alagamentoRepository: AlagamentoRepository,
    private val cameraRepository: CameraRepository,
    private val reportRepository: ReportRepository,
    private val authEventBus: AuthEventBus,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    /** Empurra uma mensagem para o snackbar global (via AuthEventBus). */
    fun notifyError(message: String) = authEventBus.notifyError(message)

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val locationClient = LocationServices.getFusedLocationProviderClient(appContext)

    init {
        carregarDados()
    }

    fun carregarDados() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val alagamentosDeferred = async { alagamentoRepository.getAlagamentos() }
            val camerasDeferred = async { cameraRepository.getCameras() }
            val reportsDeferred = async { reportRepository.listar() }

            val alagamentosResult = alagamentosDeferred.await()
            val camerasResult = camerasDeferred.await()
            val reportsResult = reportsDeferred.await()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    alagamentos = alagamentosResult.getOrElse { emptyList() },
                    cameras = camerasResult.getOrElse { emptyList() },
                    reports = reportsResult.getOrElse { emptyList() },
                    error = alagamentosResult.exceptionOrNull()?.message
                        ?: camerasResult.exceptionOrNull()?.message
                )
            }
        }
    }

    fun selectCamera(camera: Camera) {
        _uiState.update { it.copy(selectedCamera = camera, selectedAlagamento = null, selectedReport = null) }
    }

    fun selectAlagamento(alagamento: Alagamento) {
        _uiState.update { it.copy(selectedAlagamento = alagamento, selectedCamera = null, selectedReport = null) }
    }

    fun selectReport(report: AlagamentoReportado) {
        _uiState.update { it.copy(selectedReport = report, selectedCamera = null, selectedAlagamento = null) }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedCamera = null, selectedAlagamento = null, selectedReport = null) }
    }

    /** Obtém a localização atual via FusedLocationProvider. Requer permissão já concedida. */
    @SuppressLint("MissingPermission")
    suspend fun obterLocalizacaoAtual(): Pair<Double, Double>? =
        suspendCancellableCoroutine { cont ->
            locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { loc ->
                    if (loc != null) cont.resume(loc.latitude to loc.longitude)
                    else cont.resume(null)
                }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }

    fun submitReport(
        latitude: Double,
        longitude: Double,
        descricao: String?,
        fotoJpeg: ByteArray
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingReport = true, reportError = null, reportSuccess = false) }
            val result = reportRepository.criar(latitude, longitude, descricao, fotoJpeg)
            result.fold(
                onSuccess = {
                    _uiState.update { s -> s.copy(isSubmittingReport = false, reportSuccess = true) }
                    carregarDados()
                },
                onFailure = { e ->
                    _uiState.update { s ->
                        s.copy(
                            isSubmittingReport = false,
                            reportError = e.message ?: "Falha ao enviar o reporte"
                        )
                    }
                }
            )
        }
    }

    fun removerReport(id: Int) {
        viewModelScope.launch {
            reportRepository.remover(id).onSuccess { carregarDados() }
            clearSelection()
        }
    }

    fun clearReportFeedback() {
        _uiState.update { it.copy(reportError = null, reportSuccess = false) }
    }
}
