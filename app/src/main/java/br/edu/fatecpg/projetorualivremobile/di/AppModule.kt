package br.edu.fatecpg.projetorualivremobile.di

import br.edu.fatecpg.projetorualivremobile.data.remote.RuaLivreApi
import br.edu.fatecpg.projetorualivremobile.data.repository.TokenStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // 10.0.2.2 = localhost do PC no emulador Android. Altere para a URL real do servidor.
    private const val BASE_URL = "http://10.0.2.2:8000/"

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenStore: TokenStore): Interceptor = Interceptor { chain ->
        val request = tokenStore.token?.let { token ->
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } ?: chain.request()
        chain.proceed(request)
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
    // Para usar dados offline/fake, substitua pela linha abaixo:
    // FakeApiService()
}