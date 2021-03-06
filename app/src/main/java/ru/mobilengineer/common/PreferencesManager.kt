package ru.mobilengineer.common

import android.content.SharedPreferences
import com.google.gson.Gson

class PreferencesManager(val pref: SharedPreferences, val gson: Gson) {
    val editor = pref.edit()

    var warehouseId: String?
        get() {
            return pref.getString(WAREHOUSE_ID, "")
        }
        set(status) {
            editor.apply {
                putString(WAREHOUSE_ID, status)
                commit()
            }
        }

    var fullName: String?
        get() {
            return pref.getString(FULL_NAME, "")
        }
        set(status) {
            editor.apply {
                putString(FULL_NAME, status)
                commit()
            }
        }

    var firstName: String?
        get() {
            return pref.getString(FIRST_NAME, "Иванов")
        }
        set(status) {
            editor.apply {
                putString(FIRST_NAME, status)
                commit()
            }
        }

    var lastName: String?
        get() {
            return pref.getString(LAST_NAME, "Виктор")
        }
        set(status) {
            editor.apply {
                putString(LAST_NAME, status)
                commit()
            }
        }

    var patronymic: String?
        get() {
            return pref.getString(PATRONYMIC, "Иванович")
        }
        set(status) {
            editor.apply {
                putString(PATRONYMIC, status)
                commit()
            }
        }

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

    var isConsiderSerialNumber: Boolean
        get() {
            return pref.getBoolean(CONSIDER_SERIAL_NUMBER, false)
        }
        set(isCompleted) {
            editor.apply {
                putBoolean(CONSIDER_SERIAL_NUMBER, isCompleted)
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

    var isPasscodeChanging: Boolean
        get() {
            return pref.getBoolean(IS_PASSCODE_CHANGING, false)
        }
        set(isCompleted) {
            editor.apply {
                putBoolean(IS_PASSCODE_CHANGING, isCompleted)
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

    var filterMode: String?
        get() {
            return pref.getString(FILTER_MODE, "n")
        }
        set(string) {
            editor.apply {
                putString(FILTER_MODE, string)
                commit()
            }
        }

    var profileImagePath: String?
        get() {
            return pref.getString(PROFILE_IMAGE_PATH, null)
        }
        set(string) {
            editor.apply {
                putString(PROFILE_IMAGE_PATH, string)
                commit()
            }
        }

    var resultOfScanning: String?
        get() {
            return pref.getString(RESULT_OF_SCANNING, null)
        }
        set(string) {
            editor.apply {
                putString(RESULT_OF_SCANNING, string)
                commit()
            }
        }

    var token: String?
        get() {
            return pref.getString(TOKEN, null)
        }
        set(string) {
            editor.apply {
                putString(TOKEN, string)
                commit()
            }
        }


    companion object {
        const val PREF_NAME = "MobEngineerPrefs"

        private const val USER_ID = "USER_ID"
        private const val TOKEN = "TOKEN"
        private const val PASSCODE = "PASSCODE"
        private const val STATUS = "STATUS"
        private const val FIRST_NAME = "FIRST_NAME"
        private const val LAST_NAME = "LAST_NAME"
        private const val PATRONYMIC = "PATRONYMIC"
        private const val FULL_NAME = "FULL_NAME"
        private const val IS_AUTH_COMPLETED = "IS_AUTH_COMPLETED"
        private const val IS_PASSCODE_CHANGING = "IS_PASSCODE_CHANGING"
        private const val IS_TOUCH_ID_ADDED = "IS_TOUCH_ID_ADDED"
        private const val PROFILE_IMAGE_PATH = "PROFILE_IMAGE_PATH"
        private const val CONSIDER_SERIAL_NUMBER = "CONSIDER_SERIAL_NUMBER"
        private const val RESULT_OF_SCANNING = "RESULT_OF_SCANNING"
        private const val FILTER_MODE = "FILTER_MODE"
        private const val WAREHOUSE_ID = "WAREHOUSE_ID"
    }
}
