package ru.mobileinjeneer.di.component

import dagger.Component
import ru.mobileinjeneer.App
import ru.mobileinjeneer.MainActivity
import ru.mobileinjeneer.di.module.AppModule
import ru.mobileinjeneer.di.module.NetworkModule
import ru.mobileinjeneer.di.module.VMModule
import ru.mobileinjeneer.di.module.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, ViewModelModule::class, VMModule::class])
interface AppComponent {
    fun inject(application: App)

    fun inject(activity: MainActivity)
}