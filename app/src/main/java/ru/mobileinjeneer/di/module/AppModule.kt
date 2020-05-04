package ru.mobileinjeneer.di.module

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ru.mobileinjeneer.App
import ru.mobileinjeneer.common.PreferencesManager
import ru.mobileinjeneer.common.PreferencesManager.Companion.PREF_NAME
import javax.inject.Singleton

@Module
class AppModule(private val application: App) {
    @Provides
    internal fun provideContext(): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    internal fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        return Gson()
    }
    @Provides
    @Singleton
    internal fun provideSharedPreferencesHelper(sharedPreferences: SharedPreferences, gson: Gson): PreferencesManager {
        return PreferencesManager(sharedPreferences, gson)
    }

}