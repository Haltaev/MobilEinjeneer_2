package ru.mobilengineer

import android.app.Application
import ru.mobilengineer.di.component.AppComponent
import ru.mobilengineer.di.component.DaggerAppComponent
import ru.mobilengineer.di.module.AppModule

class App : Application() {

    companion object {
        private lateinit var component: AppComponent
        private lateinit var app: App

        fun getComponent(): AppComponent {
            return component
        }

        fun buildAppComponent() {
            component = DaggerAppComponent.builder()
                .appModule(AppModule(app))
                .build()
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        buildAppComponent()
    }
}