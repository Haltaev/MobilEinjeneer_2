package ru.mobilengineer.di.module

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.mobilengineer.MainViewModel
import ru.mobilengineer.di.ViewModelKey
import ru.mobilengineer.ui.fragment.installindevice.InstallInDeviceConfirmViewModel
import ru.mobilengineer.ui.fragment.installindevice.InstallInDeviceViewModel
import ru.mobilengineer.viewmodel.MyProfileViewModel
import ru.mobilengineer.viewmodel.ReturnToWarehouseConfirmViewModel
import ru.mobilengineer.viewmodel.WarehousesViewModel
import ru.mobilengineer.viewmodel.TransferToEngineerConfirmViewModel
import ru.mobilengineer.viewmodel.TransferToEngineerViewModel
import ru.mobilengineer.viewmodel.*

@Module
abstract class VMModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun mainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthorizationViewModel::class)
    internal abstract fun authorizationViewModel(viewModel: AuthorizationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyStockViewModel::class)
    internal abstract fun myStockViewModel(viewModel: MyStockViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(MyProfileViewModel::class)
    internal abstract fun myProfileViewModel(viewModel: MyProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TransferToEngineerViewModel::class)
    internal abstract fun anotherEngineersViewModel(viewModel: TransferToEngineerViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(TransferToEngineerConfirmViewModel::class)
    internal abstract fun anotherEngineersConfirmViewModel(viewModel: TransferToEngineerConfirmViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WarehousesViewModel::class)
    internal abstract fun warehousesViewModel(viewModel: WarehousesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InstallInDeviceConfirmViewModel::class)
    internal abstract fun installInDeviceConfirmViewModel(viewModel: InstallInDeviceConfirmViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IncommingSkuFromWarehouseViewModel::class)
    internal abstract fun incommingSkuFromWarehouseViewModel(viewModel: IncommingSkuFromWarehouseViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(IncommingSkuFromEngimeerViewModel::class)
    internal abstract fun incommingSkuFromEngineerViewModel(viewModel: IncommingSkuFromEngimeerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReturnToWarehouseConfirmViewModel::class)
    internal abstract fun returnToWarehouseConfirmViewModel(viewModel: ReturnToWarehouseConfirmViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TransferFromEngineerViewModel::class)
    internal abstract fun transferFromEngineerViewModel(viewModel: TransferFromEngineerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReturnFromWarehouseViewModel::class)
    internal abstract fun returnFromWarehouseViewModel(viewModel: ReturnFromWarehouseViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AvailableWarehouseViewModel::class)
    internal abstract fun availableWarehouseViewModel(viewModel: AvailableWarehouseViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ScannerViewModel::class)
    internal abstract fun scannerViewModel(viewModel: ScannerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InstallInDeviceViewModel::class)
    internal abstract fun installInDeviceViewModel(viewModel: InstallInDeviceViewModel): ViewModel
}
