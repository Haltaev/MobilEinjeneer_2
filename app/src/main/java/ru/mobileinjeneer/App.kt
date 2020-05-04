package ru.mobileinjeneer

import android.app.Application
import ru.mobileinjeneer.di.component.AppComponent
import ru.mobileinjeneer.di.component.DaggerAppComponent
import ru.mobileinjeneer.di.module.AppModule

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