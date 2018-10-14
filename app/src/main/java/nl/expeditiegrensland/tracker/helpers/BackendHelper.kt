package nl.expeditiegrensland.tracker.helpers

import android.util.Log
import nl.expeditiegrensland.tracker.Constants
import nl.expeditiegrensland.tracker.types.AuthenticateResult
import nl.expeditiegrensland.tracker.types.BackendResult
import org.json.JSONException
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

object BackendHelper {
    private fun postRequest(relativeURL: String, json: JSONObject): BackendResult<JSONObject> {
        val url = URL(Constants.BACKEND_URL + relativeURL)
        val data: ByteArray = json.toString().toByteArray(StandardCharsets.UTF_8)

        var responseCode: Int? = null
        var content: JSONObject? = null

        try {
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.connectTimeout = 10000
            connection.doOutput = true

            connection.setRequestProperty("charset", "utf-8")
            connection.setRequestProperty("content-length", data.size.toString())
            connection.setRequestProperty("content-type", "application/json")

            val dataOutputStream = DataOutputStream(connection.outputStream)
            dataOutputStream.write(data)
            dataOutputStream.flush()

            responseCode = connection.responseCode
            content = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
        } catch (err: Throwable) {
            Log.e("LOGIN", "ERRORERRORERROR")
        }

        return BackendResult(
                responseCode = responseCode
                        ?.let { it }
                        ?: HttpURLConnection.HTTP_CLIENT_TIMEOUT,
                content = content
                        ?.let { it }
                        ?: JSONObject()
        )
    }

    fun authenticate(username: String, password: String, cancel: (Boolean) -> Boolean): AuthenticateResult {
        val json = JSONObject()
                .put("username", username)
                .put("password", password)

        val backendResult = postRequest("/authenticate", json)

        if (backendResult.responseCode == HttpURLConnection.HTTP_UNAUTHORIZED)
            return AuthenticateResult(false)

        if (backendResult.responseCode == HttpURLConnection.HTTP_OK)
            try {
                return AuthenticateResult(
                        token = backendResult.content.getString("token"),
                        name = backendResult.content.getString("name")
                )
            } catch (err: JSONException) {
                Log.v("Authenticate", Log.getStackTraceString(err))
            }

        cancel(true)
        return AuthenticateResult()
    }
}