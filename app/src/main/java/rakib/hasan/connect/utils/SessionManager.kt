package rakib.hasan.connect.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE

import android.content.SharedPreferences


class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences
    private var editor: SharedPreferences.Editor? = null
    private val MY_PREF = "my_preferences"

    init {
        sharedPreferences = context.getSharedPreferences(MY_PREF, MODE_PRIVATE)
    }

    fun firstTimeAsking(permission: String?, isFirstTime: Boolean) {
        doEdit()
        editor!!.putBoolean(permission, isFirstTime)
        doCommit()
    }

    fun isFirstTimeAsking(permission: String?): Boolean {
        return sharedPreferences.getBoolean(permission, true)
    }

    private fun doEdit() {
        if (editor == null) {
            editor = sharedPreferences.edit()
        }
    }

    private fun doCommit() {
        if (editor != null) {
            editor!!.commit()
            editor = null
        }
    }

}