package cl.vitairos.industrial

import android.app.Application
import cl.vitairos.industrial.di.AppContainer

class VitairosApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
