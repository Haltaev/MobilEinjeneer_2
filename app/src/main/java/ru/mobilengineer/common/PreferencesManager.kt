package ru.mobilengineer.common

import android.content.SharedPreferences
import com.google.gson.Gson

class PreferencesManager(val pref: SharedPreferences, val gson: Gson) {
    val editor = pref.edit()

    var status: String?
        get() {
            return pref.getString(STATUS, null)
        }
        set(status) {
            editor.apply {
                putString(STATUS, status)
                commit()
            }
        }

    var isTouchIdAdded: Boolean
        get() {
            return pref.getBoolean(IS_TOUCH_ID_ADDED, false)
        }
        set(isCompleted) {
            editor.apply {
                putBoolean(IS_TOUCH_ID_ADDED, isCompleted)
                commit()
            }
        }

    var isAuthCompleted: Boolean
        get() {
            return pref.getBoolean(IS_AUTH_COMPLETED, false)
        }
        set(isCompleted) {
            editor.apply {
                putBoolean(IS_AUTH_COMPLETED, isCompleted)
                commit()
            }
        }

    var passcode: String?
        get() {
            return pref.getString(PASSCODE, null)
        }
        set(string) {
            editor.apply {
                putString(PASSCODE, string)
                commit()
            }
        }
    companion object {
        const val PREF_NAME = "MatetePrefs"

        private const val USER_ID = "USER_ID"
        private const val PASSCODE = "PASSCODE"
        private const val STATUS = "STATUS"
        private const val IS_AUTH_COMPLETED = "IS_AUTH_COMPLETED"
        private const val IS_TOUCH_ID_ADDED = "IS_TOUCH_ID_ADDED"
    }
}
