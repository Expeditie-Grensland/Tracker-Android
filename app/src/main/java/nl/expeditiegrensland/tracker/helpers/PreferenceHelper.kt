package nl.expeditiegrensland.tracker.helpers

import android.content.Context
import nl.expeditiegrensland.tracker.Constants

object PreferenceHelper {
    private fun getPreferences(context: Context?) = context
            ?.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)

    private fun getString(context: Context?, key: String, default: String = "") = getPreferences(context)
            ?.getString(key, default)
            ?: default

    private fun setString(context: Context?, key: String, value: String) = getPreferences(context)
            ?.edit()
            ?.putString(key, value)
            ?.commit()
            ?: false

    private fun remove(context: Context?, key: String) = getPreferences(context)
            ?.edit()
            ?.remove(key)
            ?.commit()
            ?: false

    fun getToken(context: Context?) = getString(context, Constants.PREF_KEY_TOKEN)
    fun setToken(context: Context?, value: String) = setString(context, Constants.PREF_KEY_TOKEN, value)
    fun removeToken(context: Context?) = remove(context, Constants.PREF_KEY_TOKEN)

    fun getExpeditie(context: Context?) = getString(context, Constants.PREF_KEY_EXPEDITIE)
    fun setExpeditie(context: Context?, value: String) = setString(context, Constants.PREF_KEY_EXPEDITIE, value)
    fun removeExpeditie(context: Context?) = remove(context, Constants.PREF_KEY_EXPEDITIE)
}