package br.edu.fatecpg.projetorualivremobile.data.model

import com.google.gson.annotations.SerializedName

// ─── Auth ─────────────────────────────────────────────────────────────────────

data class Usuario(
    @SerializedName("id") val id: Int,
    @SerializedName("nome") val nome: String,
    @SerializedName("email") val email: String,
    @SerializedName("nivel_acesso") val nivelAcesso: String = "usuario",
    @SerializedName("status") val status: String = "ativo",
    @SerializedName("avatar_url") val avatarUrl: String? = null
)

data class AvatarPadrao(
    @SerializedName("id") val id: String = "",
    @SerializedName("nome") val nome: String = "",
    @SerializedName("url") val url: String = ""
)

data class ForgotPasswordRequest(
    @SerializedName("email") val email: String
)

data class ResetPasswordRequest(
    @SerializedName("email") val email: String,
    @SerializedName("codigo") val codigo: String,
    @SerializedName("nova_senha") val novaSenha: String
)

data class ChangePasswordRequest(
    @SerializedName("senha_atual") val senhaAtual: String,
    @SerializedName("nova_senha") val novaSenha: String
)

data class UpdatePerfilRequest(
    @SerializedName("nome") val nome: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null
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

// Espelha AlagamentoResponse da API. latitude/longitude/bairro/municipio
// vêm da câmera associada e podem ser nulos (câmera sem coords cadastradas).
data class Alagamento(
    @SerializedName("id") val id: String = "",
    @SerializedName("camera_id") val cameraId: Int = 0,
    @SerializedName("regiao_id") val regiaoId: Int = 0,
    @SerializedName("nivel_agua") val nivelAgua: Double = 0.0,
    @SerializedName("confianca") val confianca: Double = 0.0,
    @SerializedName("status") val status: String = "ativo",
    @SerializedName("data_hora") val dataRegistro: String = "",
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null,
    @SerializedName("bairro") val bairro: String? = null,
    @SerializedName("municipio") val municipio: String? = null,
    @SerializedName("descricao") val descricao: String = ""
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

// Espelha CameraResponse da API. A API não envia "nome" nem um enum de
// status — derivamos ambos. latitude/longitude podem vir nulos.
data class Camera(
    @SerializedName("id") val id: String = "",
    @SerializedName("regiao_id") val regiaoId: Int = 0,
    @SerializedName("endereco_rtsp") val enderecoRtsp: String = "",
    @SerializedName("localizacao") val localizacao: String? = null,
    @SerializedName("status") val statusRaw: String = "ativo",
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null,
    @SerializedName("altitude_m") val altitudeM: Double? = null,
    @SerializedName("bairro") val bairro: String? = null,
    @SerializedName("municipio") val municipio: String? = null
) {
    val nome: String
        get() = localizacao?.takeIf { it.isNotBlank() }
            ?: bairro?.takeIf { it.isNotBlank() }
            ?: "Câmera $id"

    val status: StatusCamera
        get() = when (statusRaw.lowercase()) {
            "inativo", "inativa" -> StatusCamera.INATIVA
            "manutencao", "manutenção" -> StatusCamera.MANUTENCAO
            else -> StatusCamera.ATIVA
        }
}

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

// Reportes de alagamento feitos por usuários (com foto). TTL de 24h no backend.
data class AlagamentoReportado(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("usuario_id") val usuarioId: Int = 0,
    @SerializedName("latitude") val latitude: Double = 0.0,
    @SerializedName("longitude") val longitude: Double = 0.0,
    @SerializedName("descricao") val descricao: String? = null,
    @SerializedName("foto_url") val fotoUrl: String = "",
    @SerializedName("criado_em") val criadoEm: String = "",
    @SerializedName("expira_em") val expiraEm: String = "",
    @SerializedName("status") val status: String = "ativo"
)

// Espelha o retorno de /dashboard/alertas (sem campo de nível).
data class Alerta(
    @SerializedName("id") val id: String = "",
    @SerializedName("alagamento_id") val alagamentoId: Int = 0,
    @SerializedName("mensagem") val mensagem: String = "",
    @SerializedName("enviado") val enviado: Boolean = false,
    @SerializedName("data_envio") val dataEnvio: String? = null
)

data class RegiaoCount(
    @SerializedName("regiao") val regiao: String = "",
    @SerializedName("total") val total: Int = 0
)

// Espelha /dashboard/stats.
data class DashboardStats(
    @SerializedName("total_alagamentos_ativos") val totalAlagamentosAtivos: Int = 0,
    @SerializedName("total_cameras_ativas") val totalCamerasAtivas: Int = 0,
    @SerializedName("total_alertas_hoje") val totalAlertasHoje: Int = 0,
    @SerializedName("total_bairros_monitorados") val totalBairrosMonitorados: Int = 0,
    @SerializedName("alagamentos_por_regiao") val alagamentosPorRegiao: List<RegiaoCount> = emptyList()
)

// Espelha /dashboard/historico.
data class HistoricoEntry(
    @SerializedName("data") val data: String = "",
    @SerializedName("total_ocorrencias") val totalOcorrencias: Int = 0,
    @SerializedName("nivel_agua_medio") val nivelAguaMedio: Double = 0.0
)