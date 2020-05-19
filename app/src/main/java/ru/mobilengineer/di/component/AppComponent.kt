package ru.mobilengineer.di.component

import dagger.Component
import ru.mobilengineer.App
import ru.mobilengineer.MainActivity
import ru.mobilengineer.di.module.AppModule
import ru.mobilengineer.di.module.NetworkModule
import ru.mobilengineer.di.module.VMModule
import ru.mobilengineer.di.module.ViewModelModule
import ru.mobilengineer.ui.activity.AuthorizationActivity
import ru.mobilengineer.ui.activity.BaseActivity
import ru.mobilengineer.ui.activity.HomeActivity
import ru.mobilengineer.ui.activity.MyProfileActivity
import ru.mobilengineer.ui.fragment.authorization.AddTouchIdFragment
import ru.mobilengineer.ui.fragment.authorization.AuthorizationCodeFragment
import ru.mobilengineer.ui.fragment.authorization.AuthorizationFragment
import ru.mobilengineer.ui.fragment.home.HomeFragment
import ru.mobilengineer.ui.fragment.profile.EditProfileNameFragment
import ru.mobilengineer.ui.fragment.profile.MyProfileFragment
import ru.mobilengineer.ui.fragment.profile.ProfileSettingsFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, ViewModelModule::class, VMModule::class])
interface AppComponent {
    fun inject(application: App)

    fun inject(activity: MainActivity)
    fun inject(activity: BaseActivity)
    fun inject(activity: AuthorizationFragment)
    fun inject(activity: AuthorizationActivity)
    fun inject(activity: AuthorizationCodeFragment)
    fun inject(activity: AddTouchIdFragment)
    fun inject(activity: MyProfileFragment)
    fun inject(activity: ProfileSettingsFragment)
    fun inject(activity: EditProfileNameFragment)
    fun inject(activity: MyProfileActivity)
    fun inject(activity: HomeActivity)
    fun inject(activity: HomeFragment)
}