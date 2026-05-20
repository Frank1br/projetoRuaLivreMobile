package br.edu.fatecpg.projetorualivremobile.di

import br.edu.fatecpg.projetorualivremobile.BuildConfig
import br.edu.fatecpg.projetorualivremobile.data.remote.RuaLivreApi
import br.edu.fatecpg.projetorualivremobile.data.repository.TokenStore
import br.edu.fatecpg.projetorualivremobile.util.AuthEventBus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Vem de BuildConfig (gerado a partir de api.base_url em local.properties).
    private val BASE_URL: String = BuildConfig.API_BASE_URL

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenStore: TokenStore,
        authEventBus: AuthEventBus
    ): Interceptor = Interceptor { chain ->
        val original = chain.request()
        val request = tokenStore.token?.let { token ->
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } ?: original

        val response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            authEventBus.notifyError("Sem conexão com o servidor. Verifique sua internet.")
            throw e
        }

        // 401 em endpoint protegido → sessão expirou.
        // Ignora /auth/login e /auth/register (401 ali é credencial inválida, não sessão expirada).
        if (response.code == 401) {
            val path = request.url.encodedPath
            val isAuthAttempt = path.endsWith("/auth/login") || path.endsWith("/auth/register")
            if (!isAuthAttempt && tokenStore.token != null) {
                tokenStore.token = null
                authEventBus.notifySessionExpired()
            }
        }

        response
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            )
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideRuaLivreApi(retrofit: Retrofit): RuaLivreApi =
        retrofit.create(RuaLivreApi::class.java)
}
