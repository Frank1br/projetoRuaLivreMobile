package br.edu.fatecpg.projetorualivremobile.data.repository

import br.edu.fatecpg.projetorualivremobile.data.model.Alagamento
import br.edu.fatecpg.projetorualivremobile.data.model.Alerta
import br.edu.fatecpg.projetorualivremobile.data.model.AuthResponse
import br.edu.fatecpg.projetorualivremobile.data.model.Camera
import br.edu.fatecpg.projetorualivremobile.data.model.DashboardStats
import br.edu.fatecpg.projetorualivremobile.data.model.HistoricoEntry
import br.edu.fatecpg.projetorualivremobile.data.model.IppData
import br.edu.fatecpg.projetorualivremobile.data.model.LocalizacaoCameraRequest
import br.edu.fatecpg.projetorualivremobile.data.model.LoginRequest
import br.edu.fatecpg.projetorualivremobile.data.model.RegisterRequest
import br.edu.fatecpg.projetorualivremobile.data.model.StatusCameraRequest
import br.edu.fatecpg.projetorualivremobile.data.model.Usuario
import br.edu.fatecpg.projetorualivremobile.data.remote.RuaLivreApi
import javax.inject.Inject
import javax.inject.Singleton

// Armazena o token JWT em memória, acessível pelo interceptor sem criar dependência circular
@Singleton
class TokenStore @Inject constructor() {
    var token: String? = null
}

// ─── Auth ─────────────────────────────────────────────────────────────────────

@Singleton
class AuthRepository @Inject constructor(
    private val api: RuaLivreApi,
    private val tokenStore: TokenStore
) {
    private var currentUser: Usuario? = null

    val isLoggedIn: Boolean get() = currentUser != null
    val currentUsuario: Usuario? get() = currentUser

    suspend fun login(email: String, senha: String): Result<AuthResponse> = runCatching {
        val response = api.login(LoginRequest(email, senha))
        currentUser = response.usuario
        tokenStore.token = response.token
        response
    }

    suspend fun register(nome: String, email: String, senha: String, telefone: String): Result<AuthResponse> = runCatching {
        val response = api.register(RegisterRequest(nome, email, senha, telefone))
        currentUser = response.usuario
        tokenStore.token = response.token
        response
    }

    suspend fun getMe(): Result<Usuario> = runCatching { api.getMe() }

    fun logout() {
        currentUser = null
        tokenStore.token = null
    }
}

// ─── Alagamentos / Flood ──────────────────────────────────────────────────────

@Singleton
class AlagamentoRepository @Inject constructor(private val api: RuaLivreApi) {

    suspend fun getAlagamentos(): Result<List<Alagamento>> = runCatching {
        api.getAlagamentos()
    }

    suspend fun getAlagamento(id: String): Result<Alagamento> = runCatching {
        api.getAlagamento(id)
    }

    suspend fun resolverAlagamento(id: String): Result<Alagamento> = runCatching {
        api.resolverAlagamento(id)
    }

    suspend fun getAlertas(limit: Int = 20): Result<List<Alerta>> = runCatching {
        api.getAlertas(limit)
    }

    suspend fun getIppRanking(): Result<List<IppData>> = runCatching {
        api.getIppRanking()
    }

    suspend fun getIpp(cameraId: String): Result<IppData> = runCatching {
        api.getIpp(cameraId)
    }
}

// ─── Câmeras ──────────────────────────────────────────────────────────────────

@Singleton
class CameraRepository @Inject constructor(private val api: RuaLivreApi) {

    suspend fun getCameras(): Result<List<Camera>> = runCatching {
        api.getCameras()
    }

    suspend fun getCamera(id: String): Result<Camera> = runCatching {
        api.getCamera(id)
    }

    suspend fun updateStatus(id: String, status: String): Result<Camera> = runCatching {
        api.updateCameraStatus(id, StatusCameraRequest(status))
    }

    suspend fun updateLocalizacao(id: String, lat: Double, lon: Double): Result<Camera> = runCatching {
        api.updateCameraLocalizacao(id, LocalizacaoCameraRequest(lat, lon))
    }
}

// ─── Dashboard ────────────────────────────────────────────────────────────────

@Singleton
class DashboardRepository @Inject constructor(private val api: RuaLivreApi) {

    suspend fun getStats(): Result<DashboardStats> = runCatching {
        api.getDashboardStats()
    }

    suspend fun getHistorico(dias: Int = 30): Result<List<HistoricoEntry>> = runCatching {
        api.getHistorico(dias)
    }

    suspend fun getAlertas(limit: Int = 20): Result<List<Alerta>> = runCatching {
        api.getAlertas(limit)
    }
}