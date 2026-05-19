package br.edu.fatecpg.projetorualivremobile

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration
import java.io.File

@HiltAndroidApp
class RuaLivreApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Inicializa OSMDroid usando cache interno (sem WRITE_EXTERNAL_STORAGE)
        Configuration.getInstance().apply {
            load(this@RuaLivreApp, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            osmdroidTileCache = File(cacheDir, "osmdroid")
            userAgentValue = packageName
        }
    }
}