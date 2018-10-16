package nl.expeditiegrensland.tracker.helpers

import android.util.Log
import nl.expeditiegrensland.tracker.Constants
import nl.expeditiegrensland.tracker.types.AuthenticateResult
import nl.expeditiegrensland.tracker.types.AuthenticationException
import nl.expeditiegrensland.tracker.types.BackendResult
import nl.expeditiegrensland.tracker.types.ExpeditiesResult
import org.json.JSONException
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

object BackendHelper {
    private enum class RequestType {
        GET, POST
    }

    private fun request(requestType: RequestType, relativeURL: String, json: JSONObject? = null, token: String? = null): BackendResult {
        val url = URL(Constants.BACKEND_URL + relativeURL)

        var responseCode: Int? = null
        var content: String? = null

        try {
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 10000

            token?.let {
                Log.e("BEARERTOKEN", "Bearer $token")
                connection.setRequestProperty("Authorization", "Bearer $token")
            }

            when (requestType) {
                RequestType.GET -> {
                    connection.requestMethod = "GET"
                }
                RequestType.POST -> {
                    val data: ByteArray = json.toString().toByteArray(StandardCharsets.UTF_8)

                    connection.requestMethod = "POST"
                    connection.doOutput = true

                    connection.setRequestProperty("Content-Length", data.size.toString())
                    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")

                    val dataOutputStream = DataOutputStream(connection.outputStream)
                    dataOutputStream.write(data)
                    dataOutputStream.flush()
                }
            }

            responseCode = connection.responseCode
            Log.e("RESPONSECODE", responseCode.toString())
            content = connection.inputStream.bufferedReader().use { it.readText() }
        } catch (err: Throwable) {
            Log.e("BACKEND", Log.getStackTraceString(err))
        }

        return BackendResult(
                responseCode = responseCode
                        ?.let { it }
                        ?: HttpURLConnection.HTTP_CLIENT_TIMEOUT,
                content = content
        )
    }

    private fun getRequest(relativeURL: String, token: String? = null) =
            request(RequestType.GET, relativeURL, token = token)

    private fun postRequest(relativeURL: String, json: JSONObject, token: String? = null) =
            request(RequestType.POST, relativeURL, json, token)

    fun authenticate(username: String, password: String, cancel: (Boolean) -> Boolean): AuthenticateResult {
        val json = JSONObject()
                .put("username", username)
                .put("password", password)

        val backendResult = postRequest("/authenticate", json)

        if (backendResult.responseCode == HttpURLConnection.HTTP_UNAUTHORIZED)
            return AuthenticateResult(false)

        if (backendResult.responseCode == HttpURLConnection.HTTP_OK && backendResult.content != null)
            try {
                val content = JSONObject(backendResult.content)

                return AuthenticateResult(
                        token = content.getString("token"),
                        name = content.getString("name")
                )
            } catch (err: JSONException) {
                Log.e("Authenticate", Log.getStackTraceString(err))
            }

        cancel(true)
        return AuthenticateResult()
    }

    @Throws(AuthenticationException::class)
    fun getExpedities(token: String) : String? {
        val backendResult = getRequest("/expedities", token)

        if (backendResult.responseCode == HttpURLConnection.HTTP_UNAUTHORIZED)
            throw AuthenticationException()

        return backendResult.content
    }
}