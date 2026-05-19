package br.edu.fatecpg.projetorualivremobile.data.model

import com.google.gson.annotations.SerializedName

// ─── Auth ─────────────────────────────────────────────────────────────────────

data class Usuario(
    @SerializedName("id") val id: Int,
    @SerializedName("nome") val nome: String,
    @SerializedName("email") val email: String,
    @SerializedName("nivel_acesso") val nivelAcesso: String = "usuario",
    @SerializedName("status") val status: String = "ativo"
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("senha") val senha: String
)

data class RegisterRequest(
    @SerializedName("nome") val nome: String,
    @SerializedName("email") val email: String,
    @SerializedName("senha") val senha: String
)

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String = "bearer"
)

// ─── Alagamentos / Flood ──────────────────────────────────────────────────────

enum class NivelAlagamento { BAIXO, MEDIO, ALTO, CRITICO }

data class Alagamento(
    @SerializedName("id") val id: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("nivel") val nivel: NivelAlagamento,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("bairro") val bairro: String,
    @SerializedName("data_registro") val dataRegistro: String
)

data class FloodAnalysis(
    @SerializedName("camera_id") val cameraId: String,
    @SerializedName("alagamento_detectado") val alagamentoDetectado: Boolean,
    @SerializedName("nivel_confianca") val nivelConfianca: Double,
    @SerializedName("timestamp") val timestamp: String
)

data class IppData(
    @SerializedName("camera_id") val cameraId: String,
    @SerializedName("camera_nome") val cameraNome: String,
    @SerializedName("ipp") val ipp: Double,
    @SerializedName("nivel") val nivel: NivelAlagamento
)

data class ParaconsistentAnalysis(
    @SerializedName("camera_id") val cameraId: String,
    @SerializedName("grau_evidencia") val grauEvidencia: Double,
    @SerializedName("grau_contradicao") val grauContradicao: Double,
    @SerializedName("certeza") val certeza: Double,
    @SerializedName("incerteza") val incerteza: Double,
    @SerializedName("nivel") val nivel: NivelAlagamento
)

// ─── Câmeras ──────────────────────────────────────────────────────────────────

enum class StatusCamera { ATIVA, INATIVA, MANUTENCAO }

data class Camera(
    @SerializedName("id") val id: String,
    @SerializedName("nome") val nome: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("bairro") val bairro: String,
    @SerializedName("status") val status: StatusCamera = StatusCamera.ATIVA,
    @SerializedName("ativa") val ativa: Boolean = true
)

data class CreateCameraRequest(
    @SerializedName("nome") val nome: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("bairro") val bairro: String
)

data class StatusCameraRequest(
    @SerializedName("status") val status: String
)

data class LocalizacaoCameraRequest(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)

// ─── Dashboard ────────────────────────────────────────────────────────────────

data class Alerta(
    @SerializedName("id") val id: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("mensagem") val mensagem: String,
    @SerializedName("nivel") val nivel: NivelAlagamento,
    @SerializedName("data_hora") val dataHora: String
)

data class DashboardStats(
    @SerializedName("total_cameras") val totalCameras: Int,
    @SerializedName("cameras_ativas") val camerasAtivas: Int,
    @SerializedName("alagamentos_ativos") val alagamentosAtivos: Int,
    @SerializedName("alagamentos_hoje") val alagamentosHoje: Int,
    @SerializedName("nivel_medio") val nivelMedio: NivelAlagamento? = null
)

data class HistoricoEntry(
    @SerializedName("data") val data: String,
    @SerializedName("alagamentos") val alagamentos: Int,
    @SerializedName("cameras_ativas") val camerasAtivas: Int
)