package br.edu.fatecpg.projetorualivremobile.data.model

data class Usuario(
    val id: Int,
    val nome: String,
    val email: String,
    val nivel_acesso: String,
    val status: String
)

data class Alagamento(
    val id: Int,
    val camera_id: Int,
    val regiao_id: Int,
    val nivel_agua: Double,
    val confianca: Double,
    val status: String,
    val data_hora: String
)

data class Alerta(
    val id: Int,
    val alagamento_id: Int,
    val nivel_risco_id: Int?,
    val mensagem: String,
    val enviado: Boolean,
    val data_envio: String?
)

data class LoginRequest(
    val email: String,
    val senha: String
)

data class RegisterRequest(
    val nome: String,
    val email: String,
    val senha: String,
    val telefone: String
)

data class TokenResponse(
    val access_token: String,
    val token_type: String
)