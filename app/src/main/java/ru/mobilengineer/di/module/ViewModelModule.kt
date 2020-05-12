package ru.mobilengineer.di.module

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.mobilengineer.common.ViewModelFactory

@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}