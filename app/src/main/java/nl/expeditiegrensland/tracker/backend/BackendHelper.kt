package nl.expeditiegrensland.tracker.backend

import android.util.Log
import nl.expeditiegrensland.tracker.types.Expeditie
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

object BackendHelper {
    const val BASE_URL = "https://expeditiegrensland.nl"
    private const val BACKEND_URL = "$BASE_URL/app-backend"

    private enum class RequestType {
        GET, POST
    }

    private data class BackendResponse(val responseCode: Int,
                                       val content: String? = null)

    private fun request(requestType: RequestType,
                        relativeURL: String,
                        json: JSONObject? = null,
                        token: String? = null): BackendResponse {

        val url = URL(BACKEND_URL + relativeURL)

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

        return BackendResponse(
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

    fun authenticate(username: String, password: String): AuthResponse {
        val json = JSONObject()
                .put("username", username)
                .put("password", password)

        val (responseCode, jsonContent) = postRequest("/authenticate", json)

        if (responseCode != HttpURLConnection.HTTP_OK)
            throw BackendException(responseCode)

        return try {
            JSONObject(jsonContent ?: throw JSONException("Argument 'json' is null")).run {
                AuthResponse(
                        token = getString("token"),
                        name = getString("name")
                )
            }
        } catch (err: JSONException) {
            throw BackendException(responseCode, "Invalid response: ${err.message}")
        }
    }

    private fun parseExpeditie(jsonExpeditie: JSONObject?) =
            try {
                jsonExpeditie?.run {
                    Expeditie(
                            id = getString("id"),
                            name = getString("name"),
                            subtitle = getString("subtitle"),
                            color = getString("color"),
                            image = getString("image")
                    )
                }
            } catch (err: JSONException) {
                Log.e("ParseExpeditie", Log.getStackTraceString(err))
                null
            }

    fun getExpedities(token: String): List<Expeditie> {
        val (responseCode, jsonContent) = getRequest("/expedities", token)

        if (responseCode != HttpURLConnection.HTTP_OK)
            throw BackendException(responseCode)

        return try {
            JSONArray(jsonContent ?: throw JSONException("Argument 'json' is null")).run {
                val expedities = mutableListOf<Expeditie>()

                for (i in 0 until length())
                    parseExpeditie(optJSONObject(i))
                            ?.let { expedities.add(it) }

                expedities
            }
        } catch (err: JSONException) {
            throw BackendException(responseCode, "Invalid response: ${err.message}")
        }
    }
}