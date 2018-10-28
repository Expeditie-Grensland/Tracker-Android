package nl.expeditiegrensland.tracker.backend

import android.util.Log
import nl.expeditiegrensland.tracker.types.Expeditie

sealed class Result<out V> {

    class Value<out V>(val value: V) : Result<V>()
    class Error(val error: BackendException) : Result<Nothing>()

    fun <R> runIfValue(action: (V) -> R) =
            if (this is Value) action(value)
            else null

    fun <R> runIfError(action: (BackendException) -> R) =
            if (this is Error) action(error)
            else null

    companion object {
        fun <V> value(value: V): Result<V> = Result.Value(value)
        fun error(error: BackendException): Result<Nothing> = Result.Error(error)

        fun <V> catchError(action: () -> V): Result<V> =
                try {
                    value(action())
                } catch (error: BackendException) {
                    Log.e("BackendException", error.message)
                    Log.e("BackendException", Log.getStackTraceString(error))
                    error(error)
                }
    }

}

class BackendException(val code: Int,
                       error: String = "Unspecified")
    : Exception("$code: $error")


typealias AuthResult = Result<AuthResponse>

data class AuthResponse(val token: String = "",
                        val name: String = "")


typealias ExpeditiesResult = Result<List<Expeditie>>