package ru.mobileinjeneer.di.module

import android.app.Activity
import dagger.Module
import dagger.Provides
import ru.mobileinjeneer.di.module.AppModule

@Module(includes = [AppModule::class])
class ActivityModule(private var activity: Activity) {
    @Provides
    fun provideActivity(): Activity {
        return activity
    }
}