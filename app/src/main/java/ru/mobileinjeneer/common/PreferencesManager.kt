package ru.mobileinjeneer.common

import android.content.SharedPreferences
import com.google.gson.Gson

class PreferencesManager(val pref: SharedPreferences, val gson: Gson) {
    val editor = pref.edit()

    var status: String?
        get() {
            return pref.getString(SUCK, null)
        }
        set(status) {
            editor.apply {
                putString(SUCK, status)
                commit()
            }
        }

    companion object {
        const val PREF_NAME = "MatetePrefs"

        private const val USER_ID = "USER_ID"
        private const val SUCK = "SUCK"
    }
}
