package br.edu.fatecpg.projetorualivremobile.di

import br.edu.fatecpg.projetorualivremobile.data.remote.RuaLivreApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "http://10.0.2.2:8000/"
    // 10.0.2.2 = localhost do PC no emulador

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideRuaLivreApi(retrofit: Retrofit): RuaLivreApi =
        retrofit.create(RuaLivreApi::class.java)
}