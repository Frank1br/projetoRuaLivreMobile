package br.edu.fatecpg.projetorualivremobile.data.repository

import br.edu.fatecpg.projetorualivremobile.data.model.Alagamento
import br.edu.fatecpg.projetorualivremobile.data.model.Alerta
import br.edu.fatecpg.projetorualivremobile.data.model.AuthResponse
import br.edu.fatecpg.projetorualivremobile.data.model.LoginRequest
import br.edu.fatecpg.projetorualivremobile.data.model.RegisterRequest
import br.edu.fatecpg.projetorualivremobile.data.model.Usuario
import br.edu.fatecpg.projetorualivremobile.data.remote.RuaLivreApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val api: RuaLivreApi) {

    private var currentUser: Usuario? = null

    val isLoggedIn: Boolean get() = currentUser != null
    val currentUsuario: Usuario? get() = currentUser

    suspend fun login(email: String, senha: String): Result<AuthResponse> = runCatching {
        val response = api.login(LoginRequest(email, senha))
        currentUser = response.usuario
        response
    }

    suspend fun register(nome: String, email: String, senha: String, telefone: String): Result<AuthResponse> = runCatching {
        val response = api.register(RegisterRequest(nome, email, senha, telefone))
        currentUser = response.usuario
        response
    }

    fun logout() {
        currentUser = null
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