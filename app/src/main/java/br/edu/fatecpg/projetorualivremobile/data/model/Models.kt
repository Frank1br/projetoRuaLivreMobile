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

// Espelha AlagamentoResponse da API. latitude/longitude/descricao/bairro
// ainda não são expostos pela API — ficam com default até o backend enviá-los.
data class Alagamento(
    @SerializedName("id") val id: String = "",
    @SerializedName("camera_id") val cameraId: Int = 0,
    @SerializedName("regiao_id") val regiaoId: Int = 0,
    @SerializedName("nivel_agua") val nivelAgua: Double = 0.0,
    @SerializedName("confianca") val confianca: Double = 0.0,
    @SerializedName("status") val status: String = "ativo",
    @SerializedName("data_hora") val dataRegistro: String = "",
    @SerializedName("latitude") val latitude: Double = 0.0,
    @SerializedName("longitude") val longitude: Double = 0.0,
    @SerializedName("descricao") val descricao: String = "",
    @SerializedName("bairro") val bairro: String = ""
) {
    // A API não envia um enum de nível; derivamos da cobertura de água (0–100%).
    val nivel: NivelAlagamento
        get() = when {
            nivelAgua >= 70.0 -> NivelAlagamento.CRITICO
            nivelAgua >= 45.0 -> NivelAlagamento.ALTO
            nivelAgua >= 20.0 -> NivelAlagamento.MEDIO
            else -> NivelAlagamento.BAIXO
        }
}

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

// Espelha o retorno de /dashboard/alertas (sem campo de nível).
data class Alerta(
    @SerializedName("id") val id: String = "",
    @SerializedName("alagamento_id") val alagamentoId: Int = 0,
    @SerializedName("mensagem") val mensagem: String = "",
    @SerializedName("enviado") val enviado: Boolean = false,
    @SerializedName("data_envio") val dataEnvio: String? = null
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