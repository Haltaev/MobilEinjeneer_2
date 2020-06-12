package ru.mobilengineer.di.component

import dagger.Component
import ru.mobilengineer.App
import ru.mobilengineer.MainActivity
import ru.mobilengineer.di.module.AppModule
import ru.mobilengineer.di.module.NetworkModule
import ru.mobilengineer.di.module.VMModule
import ru.mobilengineer.di.module.ViewModelModule
import ru.mobilengineer.ui.activity.*
import ru.mobilengineer.ui.fragment.authorization.AddTouchIdFragment
import ru.mobilengineer.ui.fragment.authorization.AuthorizationCodeFragment
import ru.mobilengineer.ui.fragment.authorization.AuthorizationFragment
import ru.mobilengineer.ui.fragment.available.AvailableWarehouseFragment
import ru.mobilengineer.ui.fragment.engineer.IncommingSkuFromEngineerFragment
import ru.mobilengineer.ui.fragment.engineer.PickEngineerFragment
import ru.mobilengineer.ui.fragment.filters.FiltersFragment
import ru.mobilengineer.ui.fragment.home.MyStockFragment
import ru.mobilengineer.ui.fragment.installindevice.InstallInDeviceConfirmFragment
import ru.mobilengineer.ui.fragment.installindevice.InstallInDeviceFragment
import ru.mobilengineer.ui.fragment.profile.EditProfileNameFragment
import ru.mobilengineer.ui.fragment.profile.MyProfileFragment
import ru.mobilengineer.ui.fragment.profile.ProfileSettingsFragment
import ru.mobilengineer.ui.fragment.returntowarehouse.PickWarehouseFragment
import ru.mobilengineer.ui.fragment.returntowarehouse.ReturnFromWarehouseFragment
import ru.mobilengineer.ui.fragment.returntowarehouse.ReturnToWarehouseConfirmFragment
import ru.mobilengineer.ui.fragment.returntowarehouse.ReturnToWarehouseFragment
import ru.mobilengineer.ui.fragment.transfertoengineer.TransferFromEngineerFragment
import ru.mobilengineer.ui.fragment.transfertoengineer.TransferToEngineerConfirmFragment
import ru.mobilengineer.ui.fragment.transfertoengineer.TransferToEngineerFragment
import ru.mobilengineer.ui.fragment.warehouse.IncommingSkuFromWarehouseFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, ViewModelModule::class, VMModule::class])
interface AppComponent {
    fun inject(application: App)

    fun inject(activity: MainActivity)
    fun inject(activity: BaseActivity)
    fun inject(fragment: AuthorizationFragment)
    fun inject(activity: AuthorizationActivity)
    fun inject(fragment: AuthorizationCodeFragment)
    fun inject(fragment: AddTouchIdFragment)
    fun inject(fragment: MyProfileFragment)
    fun inject(fragment: ProfileSettingsFragment)
    fun inject(fragment: EditProfileNameFragment)
    fun inject(activity: MyProfileActivity)
    fun inject(activity: MyStockActivity)
    fun inject(activity: AvailableWarehouseActivity)
    fun inject(fragment: MyStockFragment)
    fun inject(fragment: AvailableWarehouseFragment)
    fun inject(fragment: FiltersFragment)
    fun inject(activity: ReturnToWarehouseActivity)
    fun inject(activity: ScannerActivity)
    fun inject(activity: ActionWithItemActivity)
    fun inject(fragment: ReturnToWarehouseFragment)
    fun inject(fragment: ReturnToWarehouseConfirmFragment)
    fun inject(fragment: InstallInDeviceFragment)
    fun inject(fragment: TransferToEngineerFragment)
    fun inject(fragment: InstallInDeviceConfirmFragment)
    fun inject(fragment: TransferToEngineerConfirmFragment)
    fun inject(fragment: PickWarehouseFragment)
    fun inject(fragment: IncommingSkuFromWarehouseFragment)
    fun inject(fragment: PickEngineerFragment)
    fun inject(fragment: IncommingSkuFromEngineerFragment)
    fun inject(fragment: TransferFromEngineerFragment)
    fun inject(fragment: ReturnFromWarehouseFragment)
}