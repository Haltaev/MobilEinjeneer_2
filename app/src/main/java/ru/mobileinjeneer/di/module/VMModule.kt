package ru.mobileinjeneer.di.module

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.mobileinjeneer.MainViewModel
import ru.mobileinjeneer.di.ViewModelKey

@Module
abstract class VMModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun mainViewModel(viewModel: MainViewModel): ViewModel
}
