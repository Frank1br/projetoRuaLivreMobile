package br.edu.fatecpg.projetorualivremobile.data.model

data class Usuario(
    val id: String,
    val nome: String,
    val email: String,
    val telefone: String = ""
)

data class Alagamento(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val nivel: NivelAlagamento,
    val descricao: String,
    val bairro: String,
    val dataRegistro: String
)

enum class NivelAlagamento {
    BAIXO, MEDIO, ALTO, CRITICO
}

data class Alerta(
    val id: String,
    val titulo: String,
    val mensagem: String,
    val nivel: NivelAlagamento,
    val dataHora: String
)

data class LoginRequest(val email: String, val senha: String)
data class RegisterRequest(val nome: String, val email: String, val senha: String, val telefone: String)
data class AuthResponse(val usuario: Usuario, val token: String)