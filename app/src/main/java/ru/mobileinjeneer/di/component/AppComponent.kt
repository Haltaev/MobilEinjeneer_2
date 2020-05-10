package ru.mobileinjeneer.di.component

import dagger.Component
import ru.mobileinjeneer.App
import ru.mobileinjeneer.MainActivity
import ru.mobileinjeneer.di.module.AppModule
import ru.mobileinjeneer.di.module.NetworkModule
import ru.mobileinjeneer.di.module.VMModule
import ru.mobileinjeneer.di.module.ViewModelModule
import ru.mobileinjeneer.ui.activity.BaseActivity
import ru.mobileinjeneer.ui.fragment.authorization.AddTouchIdFragment
import ru.mobileinjeneer.ui.fragment.authorization.AuthorizationCodeFragment
import ru.mobileinjeneer.ui.fragment.authorization.AuthorizationFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, ViewModelModule::class, VMModule::class])
interface AppComponent {
    fun inject(application: App)

    fun inject(activity: MainActivity)
    fun inject(activity: BaseActivity)
    fun inject(activity: AuthorizationFragment)
    fun inject(activity: AuthorizationCodeFragment)
    fun inject(activity: AddTouchIdFragment)
}