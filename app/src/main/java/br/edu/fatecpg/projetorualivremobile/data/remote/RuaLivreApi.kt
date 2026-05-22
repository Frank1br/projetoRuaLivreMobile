package br.edu.fatecpg.projetorualivremobile.data.remote

import br.edu.fatecpg.projetorualivremobile.data.model.Alagamento
import br.edu.fatecpg.projetorualivremobile.data.model.AlagamentoReportado
import br.edu.fatecpg.projetorualivremobile.data.model.Alerta
import br.edu.fatecpg.projetorualivremobile.data.model.AvatarPadrao
import br.edu.fatecpg.projetorualivremobile.data.model.ChangePasswordRequest
import br.edu.fatecpg.projetorualivremobile.data.model.ForgotPasswordRequest
import br.edu.fatecpg.projetorualivremobile.data.model.ResetPasswordRequest
import br.edu.fatecpg.projetorualivremobile.data.model.UpdatePerfilRequest
import br.edu.fatecpg.projetorualivremobile.data.model.TokenResponse
import br.edu.fatecpg.projetorualivremobile.data.model.Camera
import br.edu.fatecpg.projetorualivremobile.data.model.CreateCameraRequest
import br.edu.fatecpg.projetorualivremobile.data.model.DashboardStats
import br.edu.fatecpg.projetorualivremobile.data.model.FloodAnalysis
import br.edu.fatecpg.projetorualivremobile.data.model.HistoricoEntry
import br.edu.fatecpg.projetorualivremobile.data.model.IppData
import br.edu.fatecpg.projetorualivremobile.data.model.LocalizacaoCameraRequest
import br.edu.fatecpg.projetorualivremobile.data.model.LoginRequest
import br.edu.fatecpg.projetorualivremobile.data.model.NivelAlagamento
import br.edu.fatecpg.projetorualivremobile.data.model.ParaconsistentAnalysis
import br.edu.fatecpg.projetorualivremobile.data.model.RegiaoCount
import br.edu.fatecpg.projetorualivremobile.data.model.RegisterRequest
import br.edu.fatecpg.projetorualivremobile.data.model.StatusCamera
import br.edu.fatecpg.projetorualivremobile.data.model.StatusCameraRequest
import br.edu.fatecpg.projetorualivremobile.data.model.Usuario
import br.edu.fatecpg.projetorualivremobile.util.PasswordValidator
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

// Retrofit-annotated interface — cobertura completa de todos os endpoints
interface RuaLivreApi {

    // ── Auth ──────────────────────────────────────────────────────────────────

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Usuario

    @GET("auth/me")
    suspend fun getMe(): Usuario

    @PATCH("auth/me")
    suspend fun atualizarPerfil(@Body request: UpdatePerfilRequest): Usuario

    // 204 No Content — corpo vazio; Response<Unit> evita o erro
    // "response body was null but declared non-null".
    @POST("auth/me/change-password")
    suspend fun trocarSenha(@Body request: ChangePasswordRequest): Response<Unit>

    @POST("auth/forgot-password")
    suspend fun esqueciSenha(@Body request: ForgotPasswordRequest)

    @POST("auth/reset-password")
    suspend fun resetarSenha(@Body request: ResetPasswordRequest): Response<Unit>

    @GET("avatars/padroes")
    suspend fun getAvataresPadrao(): List<AvatarPadrao>

    @Multipart
    @POST("auth/me/avatar")
    suspend fun uploadAvatar(@Part foto: MultipartBody.Part): Usuario

    // ── Câmeras ───────────────────────────────────────────────────────────────

    @GET("cameras/")
    suspend fun getCameras(): List<Camera>

    @GET("cameras/{camera_id}")
    suspend fun getCamera(@Path("camera_id") cameraId: String): Camera

    @POST("cameras/")
    suspend fun createCamera(@Body request: CreateCameraRequest): Camera

    @PATCH("cameras/{camera_id}/status")
    suspend fun updateCameraStatus(
        @Path("camera_id") cameraId: String,
        @Body request: StatusCameraRequest
    ): Camera

    @PATCH("cameras/{camera_id}/localizacao")
    suspend fun updateCameraLocalizacao(
        @Path("camera_id") cameraId: String,
        @Body request: LocalizacaoCameraRequest
    ): Camera

    // ── Flood ─────────────────────────────────────────────────────────────────

    @POST("flood/analyze/{camera_id}")
    suspend fun analyzeCamera(@Path("camera_id") cameraId: String): FloodAnalysis

    @GET("flood/alagamentos")
    suspend fun getAlagamentos(): List<Alagamento>

    @GET("flood/alagamentos/{alagamento_id}")
    suspend fun getAlagamento(@Path("alagamento_id") alagamentoId: String): Alagamento

    // PATCH sem body — usa @HTTP com hasBody = false
    @HTTP(method = "PATCH", path = "flood/alagamentos/{alagamento_id}/resolver", hasBody = false)
    suspend fun resolverAlagamento(@Path("alagamento_id") alagamentoId: String): Alagamento

    @GET("flood/ipp/{camera_id}")
    suspend fun getIpp(@Path("camera_id") cameraId: String): IppData

    @GET("flood/ipp")
    suspend fun getIppRanking(): List<IppData>

    @GET("flood/paraconsistent/{camera_id}")
    suspend fun getParaconsistentAnalysis(@Path("camera_id") cameraId: String): ParaconsistentAnalysis

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @GET("dashboard/stats")
    suspend fun getDashboardStats(): DashboardStats

    @GET("dashboard/historico")
    suspend fun getHistorico(@Query("dias") dias: Int): List<HistoricoEntry>

    @GET("dashboard/alertas")
    suspend fun getAlertas(@Query("limit") limit: Int): List<Alerta>

    // ── Reports de usuário ────────────────────────────────────────────────────

    @Multipart
    @POST("reports/alagamentos")
    suspend fun criarReport(
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("descricao") descricao: RequestBody?,
        @Part foto: MultipartBody.Part
    ): AlagamentoReportado

    @GET("reports/alagamentos")
    suspend fun getReports(): List<AlagamentoReportado>

    @GET("reports/alagamentos/meus")
    suspend fun getMeusReports(): List<AlagamentoReportado>

    @DELETE("reports/alagamentos/{id}")
    suspend fun removerReport(@Path("id") id: Int): Response<Unit>
}

// ─── FakeApiService ───────────────────────────────────────────────────────────
// Implementação offline para desenvolvimento sem backend.
// Para ativar: alterar AppModule.provideRuaLivreApi() para retornar FakeApiService().

class FakeApiService : RuaLivreApi {

    private val fakeUsuario = Usuario(
        id = 1,
        nome = "Usuário Demo",
        email = "usuario@email.com"
    )

    // ── Auth ──────────────────────────────────────────────────────────────────

    override suspend fun login(request: LoginRequest): TokenResponse {
        delay(1000)
        if (request.email.isBlank() || request.senha.isBlank())
            throw Exception("Usuário ou senha inválidos")
        return TokenResponse(accessToken = "fake_token_123")
    }

    override suspend fun register(request: RegisterRequest): Usuario {
        delay(1000)
        if (request.nome.isBlank() || request.email.isBlank())
            throw Exception("Nome e e-mail são obrigatórios")
        val validation = PasswordValidator.validate(request.senha, request.nome, request.email)
        if (validation.isFailure)
            throw Exception(validation.exceptionOrNull()?.message ?: "Senha inválida")
        return fakeUsuario.copy(nome = request.nome, email = request.email)
    }

    override suspend fun getMe(): Usuario {
        delay(300)
        return fakeUsuario
    }

    // ── Câmeras ───────────────────────────────────────────────────────────────

    override suspend fun getCameras(): List<Camera> {
        delay(800)
        return listOf(
            Camera(id = "cam1", localizacao = "Câmera Centro", statusRaw = "ativo", latitude = -23.5505, longitude = -46.6333, bairro = "Centro"),
            Camera(id = "cam2", localizacao = "Câmera Liberdade", statusRaw = "ativo", latitude = -23.5489, longitude = -46.6388, bairro = "Liberdade"),
            Camera(id = "cam3", localizacao = "Câmera Pinheiros", statusRaw = "manutencao", latitude = -23.5521, longitude = -46.6412, bairro = "Pinheiros"),
            Camera(id = "cam4", localizacao = "Câmera Consolação", statusRaw = "ativo", latitude = -23.5560, longitude = -46.6450, bairro = "Consolação"),
            Camera(id = "cam5", localizacao = "Câmera Jardins", statusRaw = "inativo", latitude = -23.5480, longitude = -46.6300, bairro = "Jardins")
        )
    }

    override suspend fun getCamera(cameraId: String): Camera {
        delay(300)
        return getCameras().first { it.id == cameraId }
    }

    override suspend fun createCamera(request: CreateCameraRequest): Camera {
        delay(500)
        return Camera(id = "cam_new", localizacao = request.nome, latitude = request.latitude, longitude = request.longitude, bairro = request.bairro)
    }

    override suspend fun updateCameraStatus(cameraId: String, request: StatusCameraRequest): Camera {
        delay(300)
        return getCamera(cameraId)
    }

    override suspend fun updateCameraLocalizacao(cameraId: String, request: LocalizacaoCameraRequest): Camera {
        delay(300)
        return getCamera(cameraId).copy(latitude = request.latitude, longitude = request.longitude)
    }

    // ── Flood ─────────────────────────────────────────────────────────────────

    override suspend fun analyzeCamera(cameraId: String): FloodAnalysis {
        delay(1500)
        return FloodAnalysis(cameraId, true, 0.87, "2024-01-15T14:30:00")
    }

    override suspend fun getAlagamentos(): List<Alagamento> {
        delay(800)
        return listOf(
            Alagamento(id = "1", nivelAgua = 55.0, latitude = -23.5505, longitude = -46.6333, descricao = "Av. Principal alagada", bairro = "Centro", dataRegistro = "2024-01-15 14:30"),
            Alagamento(id = "2", nivelAgua = 30.0, latitude = -23.5489, longitude = -46.6388, descricao = "Rua parcialmente bloqueada", bairro = "Liberdade", dataRegistro = "2024-01-15 13:00"),
            Alagamento(id = "3", nivelAgua = 10.0, latitude = -23.5432, longitude = -46.6291, descricao = "Poça na calçada", bairro = "Vila Nova", dataRegistro = "2024-01-15 12:15"),
            Alagamento(id = "4", nivelAgua = 85.0, latitude = -23.5521, longitude = -46.6412, descricao = "Rua completamente alagada", bairro = "Pinheiros", dataRegistro = "2024-01-15 15:00"),
            Alagamento(id = "5", nivelAgua = 50.0, latitude = -23.5560, longitude = -46.6450, descricao = "Trânsito bloqueado", bairro = "Consolação", dataRegistro = "2024-01-15 14:00"),
            Alagamento(id = "6", nivelAgua = 30.0, latitude = -23.5480, longitude = -46.6300, descricao = "Água na rua principal", bairro = "Jardins", dataRegistro = "2024-01-15 11:30")
        )
    }

    override suspend fun getAlagamento(alagamentoId: String): Alagamento {
        delay(300)
        return getAlagamentos().first { it.id == alagamentoId }
    }

    override suspend fun resolverAlagamento(alagamentoId: String): Alagamento {
        delay(300)
        return getAlagamento(alagamentoId)
    }

    override suspend fun getIpp(cameraId: String): IppData {
        delay(500)
        return IppData(cameraId, "Câmera $cameraId", 0.75, NivelAlagamento.ALTO)
    }

    override suspend fun getIppRanking(): List<IppData> {
        delay(700)
        return listOf(
            IppData("cam1", "Câmera Centro", 0.85, NivelAlagamento.CRITICO),
            IppData("cam4", "Câmera Consolação", 0.72, NivelAlagamento.ALTO),
            IppData("cam2", "Câmera Liberdade", 0.45, NivelAlagamento.MEDIO),
            IppData("cam5", "Câmera Jardins", 0.20, NivelAlagamento.BAIXO)
        )
    }

    override suspend fun getParaconsistentAnalysis(cameraId: String): ParaconsistentAnalysis {
        delay(600)
        return ParaconsistentAnalysis(cameraId, 0.75, 0.15, 0.60, 0.40, NivelAlagamento.ALTO)
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    override suspend fun getDashboardStats(): DashboardStats {
        delay(600)
        return DashboardStats(
            totalAlagamentosAtivos = 4,
            totalCamerasAtivas = 3,
            totalAlertasHoje = 6,
            alagamentosPorRegiao = listOf(
                RegiaoCount("Centro", 2),
                RegiaoCount("Zona Sul", 1),
                RegiaoCount("Zona Norte", 1)
            )
        )
    }

    override suspend fun getHistorico(dias: Int): List<HistoricoEntry> {
        delay(600)
        return listOf(
            HistoricoEntry("2024-01-12", 4, 28.0),
            HistoricoEntry("2024-01-13", 1, 12.0),
            HistoricoEntry("2024-01-14", 3, 35.0),
            HistoricoEntry("2024-01-15", 6, 52.0)
        )
    }

    override suspend fun getAlertas(limit: Int): List<Alerta> {
        delay(600)
        return listOf(
            Alerta(id = "1", alagamentoId = 1, mensagem = "Centro: 12 ruas afetadas", enviado = true, dataEnvio = "2024-01-15 14:30"),
            Alerta(id = "2", alagamentoId = 2, mensagem = "Liberdade: 5 ruas afetadas", enviado = true, dataEnvio = "2024-01-15 13:00"),
            Alerta(id = "3", alagamentoId = 3, mensagem = "Vila Nova: 2 ruas afetadas", enviado = true, dataEnvio = "2024-01-15 12:15")
        ).take(limit)
    }

    // ── Reports (stubs) ───────────────────────────────────────────────────────

    override suspend fun criarReport(
        latitude: RequestBody,
        longitude: RequestBody,
        descricao: RequestBody?,
        foto: MultipartBody.Part
    ): AlagamentoReportado {
        delay(500)
        return AlagamentoReportado(id = 0)
    }

    override suspend fun getReports(): List<AlagamentoReportado> {
        delay(300)
        return emptyList()
    }

    override suspend fun getMeusReports(): List<AlagamentoReportado> {
        delay(300)
        return emptyList()
    }

    override suspend fun removerReport(id: Int): Response<Unit> {
        delay(200)
        return Response.success(Unit)
    }

    // ── Perfil / Auth extras (stubs) ──────────────────────────────────────────

    override suspend fun atualizarPerfil(request: UpdatePerfilRequest): Usuario {
        delay(200)
        return fakeUsuario.copy(
            nome = request.nome ?: fakeUsuario.nome,
            avatarUrl = request.avatarUrl ?: fakeUsuario.avatarUrl
        )
    }

    override suspend fun trocarSenha(request: ChangePasswordRequest): Response<Unit> {
        delay(200)
        return Response.success(Unit)
    }

    override suspend fun esqueciSenha(request: ForgotPasswordRequest) { delay(200) }

    override suspend fun resetarSenha(request: ResetPasswordRequest): Response<Unit> {
        delay(200)
        return Response.success(Unit)
    }

    override suspend fun getAvataresPadrao(): List<AvatarPadrao> {
        delay(150)
        return listOf(
            AvatarPadrao("azul", "Indigo", "https://example.com/azul.svg"),
            AvatarPadrao("laranja", "Laranja", "https://example.com/laranja.svg"),
            AvatarPadrao("verde", "Verde", "https://example.com/verde.svg")
        )
    }

    override suspend fun uploadAvatar(foto: MultipartBody.Part): Usuario {
        delay(300)
        return fakeUsuario
    }
}