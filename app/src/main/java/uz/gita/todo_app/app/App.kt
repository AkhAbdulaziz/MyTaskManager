package uz.gita.todo_app.app

import android.app.Application
import android.os.strictmode.InstanceCountViolation
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        lateinit var instance : App
        private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}