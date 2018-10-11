package nl.expeditiegrensland.tracker.helpers

import android.content.Context
import nl.expeditiegrensland.tracker.Constants

object PrefsHelper {
    private fun getPreferences(context: Context?) = context
            ?.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)

    fun getToken(context: Context?) = getPreferences(context)
            ?.getString(Constants.PREF_KEY_TOKEN, "")
            ?: ""

    fun setToken(context: Context?, value: String) = getPreferences(context)
            ?.edit()
            ?.putString(Constants.PREF_KEY_TOKEN, value)
            ?.commit()
            ?: false

    fun removeToken(context: Context?) = getPreferences(context)
            ?.edit()
            ?.remove(Constants.PREF_KEY_TOKEN)
            ?.commit()
            ?: false
}