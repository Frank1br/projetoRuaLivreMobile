package br.edu.fatecpg.projetorualivremobile.di

import br.edu.fatecpg.projetorualivremobile.data.remote.FakeApiService
import br.edu.fatecpg.projetorualivremobile.data.remote.RuaLivreApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRuaLivreApi(): RuaLivreApi = FakeApiService()
}