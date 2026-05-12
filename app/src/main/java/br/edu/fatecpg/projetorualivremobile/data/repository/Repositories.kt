package br.edu.fatecpg.projetorualivremobile.data.repository

import br.edu.fatecpg.projetorualivremobile.data.model.*
import br.edu.fatecpg.projetorualivremobile.data.remote.RuaLivreApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val api: RuaLivreApi) {

    private var token: String? = null
    private var usuario: Usuario? = null

    val isLoggedIn: Boolean get() = token != null
    val currentUsuario: Usuario? get() = usuario

    suspend fun login(email: String, senha: String): Result<String> = runCatching {
        val response = api.login(LoginRequest(email, senha))

        token = response.access_token

        usuario = Usuario(
            id = 1,
            nome = "Usuário",
            email = email,
            nivel_acesso = "usuario",
            status = "ativo"
        )

        token!!
    }

    suspend fun register(
        nome: String,
        email: String,
        senha: String,
        telefone: String
    ): Result<String> = runCatching {
        val response = api.register(RegisterRequest(nome, email, senha, telefone))

        token = response.access_token

        usuario = Usuario(
            id = 1,
            nome = nome,
            email = email,
            nivel_acesso = "usuario",
            status = "ativo"
        )

        token!!
    }

    fun logout() {
        token = null
        usuario = null
    }
}

@Singleton
class AlagamentoRepository @Inject constructor(private val api: RuaLivreApi) {

    suspend fun getAlagamentos(): Result<List<Alagamento>> = runCatching {
        api.getAlagamentos()
    }

    suspend fun getAlertas(): Result<List<Alerta>> = runCatching {
        api.getAlertas()
    }
}