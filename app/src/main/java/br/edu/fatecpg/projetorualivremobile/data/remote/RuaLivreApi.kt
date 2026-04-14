package br.edu.fatecpg.projetorualivremobile.data.remote

import br.edu.fatecpg.projetorualivremobile.data.model.Alagamento
import br.edu.fatecpg.projetorualivremobile.data.model.Alerta
import br.edu.fatecpg.projetorualivremobile.data.model.AuthResponse
import br.edu.fatecpg.projetorualivremobile.data.model.LoginRequest
import br.edu.fatecpg.projetorualivremobile.data.model.NivelAlagamento
import br.edu.fatecpg.projetorualivremobile.data.model.RegisterRequest
import br.edu.fatecpg.projetorualivremobile.data.model.Usuario
import kotlinx.coroutines.delay

interface RuaLivreApi {
    suspend fun login(request: LoginRequest): AuthResponse
    suspend fun register(request: RegisterRequest): AuthResponse
    suspend fun getAlagamentos(): List<Alagamento>
    suspend fun getAlertas(): List<Alerta>
    suspend fun getUsuario(id: String): Usuario
}

class FakeApiService : RuaLivreApi {

    private val fakeUsuario = Usuario(
        id = "1",
        nome = "Usuário",
        email = "usuario@email.com",
        telefone = ""
    )

    override suspend fun login(request: LoginRequest): AuthResponse {
        delay(1000)
        if (request.email.isBlank() || request.senha.isBlank()) {
            throw Exception("Usuário ou senha inválidos")
        }
        return AuthResponse(usuario = fakeUsuario, token = "fake_token_123")
    }

    override suspend fun register(request: RegisterRequest): AuthResponse {
        delay(1000)
        val novoUsuario = fakeUsuario.copy(nome = request.nome, email = request.email)
        return AuthResponse(usuario = novoUsuario, token = "fake_token_456")
    }

    override suspend fun getAlagamentos(): List<Alagamento> {
        delay(800)
        return listOf(
            Alagamento("1", -23.5505, -46.6333, NivelAlagamento.ALTO, "Av. Principal alagada", "Centro", "2024-01-15 14:30"),
            Alagamento("2", -23.5489, -46.6388, NivelAlagamento.MEDIO, "Rua parcialmente bloqueada", "Liberdade", "2024-01-15 13:00"),
            Alagamento("3", -23.5432, -46.6291, NivelAlagamento.BAIXO, "Poça na calçada", "Vila Nova", "2024-01-15 12:15"),
            Alagamento("4", -23.5521, -46.6412, NivelAlagamento.CRITICO, "Rua completamente alagada", "Pinheiros", "2024-01-15 15:00"),
            Alagamento("5", -23.5560, -46.6450, NivelAlagamento.ALTO, "Trânsito bloqueado", "Consolação", "2024-01-15 14:00"),
            Alagamento("6", -23.5480, -46.6300, NivelAlagamento.MEDIO, "Água na rua principal", "Jardins", "2024-01-15 11:30"),
        )
    }

    override suspend fun getAlertas(): List<Alerta> {
        delay(600)
        return listOf(
            Alerta("1", "Centro", "12 ruas afetadas", NivelAlagamento.ALTO, "14:30"),
            Alerta("2", "Liberdade", "5 ruas afetadas", NivelAlagamento.MEDIO, "13:00"),
            Alerta("3", "Vila Nova", "2 ruas afetadas", NivelAlagamento.BAIXO, "12:15"),
        )
    }

    override suspend fun getUsuario(id: String): Usuario {
        delay(500)
        return fakeUsuario
    }
}