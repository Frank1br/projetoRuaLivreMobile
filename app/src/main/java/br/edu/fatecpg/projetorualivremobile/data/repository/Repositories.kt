package br.edu.fatecpg.projetorualivremobile.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import br.edu.fatecpg.projetorualivremobile.data.model.Alagamento
import br.edu.fatecpg.projetorualivremobile.data.model.Alerta
import br.edu.fatecpg.projetorualivremobile.data.model.TokenResponse
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
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// Token JWT persistido em EncryptedSharedPreferences. Sobrevive a restarts do app.
@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = run {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) {
            prefs.edit().apply {
                if (value == null) remove(KEY_TOKEN) else putString(KEY_TOKEN, value)
            }.apply()
        }

    private companion object {
        const val PREFS_FILE = "rualivre_secure_prefs"
        const val KEY_TOKEN = "jwt_token"
    }
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

    suspend fun login(email: String, senha: String): Result<TokenResponse> = runCatching {
        val normalizedEmail = email.trim().lowercase()
        val response = api.login(LoginRequest(normalizedEmail, senha))
        tokenStore.token = response.accessToken
        currentUser = runCatching { api.getMe() }.getOrNull()
        response
    }

    suspend fun register(nome: String, email: String, senha: String): Result<Usuario> = runCatching {
        val normalizedEmail = email.trim().lowercase()
        val usuario = api.register(RegisterRequest(nome.trim(), normalizedEmail, senha))
        currentUser = usuario
        val loginResponse = api.login(LoginRequest(normalizedEmail, senha))
        tokenStore.token = loginResponse.accessToken
        usuario
    }

    suspend fun getMe(): Result<Usuario> = runCatching { api.getMe() }

    /** Verifica se há token persistido e tenta restaurar a sessão chamando /auth/me. */
    suspend fun bootstrap(): Boolean {
        if (tokenStore.token.isNullOrBlank()) return false
        val result = runCatching { api.getMe() }
        return result.fold(
            onSuccess = { user ->
                currentUser = user
                true
            },
            onFailure = {
                logout()
                false
            }
        )
    }

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