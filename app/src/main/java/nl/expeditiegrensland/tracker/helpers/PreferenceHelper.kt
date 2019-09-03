package nl.expeditiegrensland.tracker.helpers

import android.content.Context
import kotlinx.serialization.SerializationException
import kotlinx.serialization.protobuf.ProtoBuf
import nl.expeditiegrensland.tracker.types.Expeditie

object PreferenceHelper {
    private const val FILE = "Preferences"
    private const val KEY_TOKEN = "TOKEN"
    private const val KEY_EXPEDITIE = "EXPEDITIE"
    const val KEY_IS_REQUESTING_UPDATES = "IS_REQUESTING_UPDATES"

    fun getPreferences(context: Context?) = context
            ?.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    private fun getString(context: Context?, key: String, default: String? = "") = getPreferences(context)
            ?.getString(key, default)
            ?: default

    private fun getBoolean(context: Context?, key: String, default: Boolean) = getPreferences(context)
            ?.getBoolean(key, default)
            ?: false

    private fun setString(context: Context?, key: String, value: String?) = getPreferences(context)
            ?.edit()
            ?.putString(key, value)
            ?.commit()
            ?: false

    private fun setBoolean(context: Context?, key: String, value: Boolean) = getPreferences(context)
            ?.edit()
            ?.putBoolean(key, value)
            ?.commit()
            ?: false

    private fun remove(context: Context?, key: String) = getPreferences(context)
            ?.edit()
            ?.remove(key)
            ?.commit()
            ?: false


    fun getToken(context: Context?) =
            getString(context, KEY_TOKEN)

    fun setToken(context: Context?, value: String) =
            setString(context, KEY_TOKEN, value)

    fun removeToken(context: Context?) =
            remove(context, KEY_TOKEN)


    fun getExpeditie(context: Context?) =
            try {
                getString(context, KEY_EXPEDITIE, null)?.let {
                    ProtoBuf.loads<Expeditie>(it)
                }
            } catch (err: SerializationException) {
                null
            }

    fun setExpeditie(context: Context?, value: Expeditie) =
            setString(context, KEY_EXPEDITIE, ProtoBuf.dumps(value))

    fun removeExpeditie(context: Context?) =
            remove(context, KEY_EXPEDITIE)


    fun getIsRequestingUpdates(context: Context?) =
            getBoolean(context, KEY_IS_REQUESTING_UPDATES, false)

    fun setIsRequestingUpdates(context: Context?, value: Boolean) =
            setBoolean(context, KEY_IS_REQUESTING_UPDATES, value)

}