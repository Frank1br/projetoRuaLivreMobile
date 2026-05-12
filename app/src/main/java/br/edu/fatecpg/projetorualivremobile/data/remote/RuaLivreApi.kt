package br.edu.fatecpg.projetorualivremobile.data.remote

import br.edu.fatecpg.projetorualivremobile.data.model.*
import retrofit2.http.*

interface RuaLivreApi {

    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): TokenResponse

    @GET("/alagamentos")
    suspend fun getAlagamentos(): List<Alagamento>

    @GET("/alertas")
    suspend fun getAlertas(): List<Alerta>

    @GET("/usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Int): Usuario
}