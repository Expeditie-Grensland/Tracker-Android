package nl.expeditiegrensland.tracker.helpers

import android.util.Log
import nl.expeditiegrensland.tracker.Constants
import nl.expeditiegrensland.tracker.types.*
import org.json.JSONArray
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

    fun authenticate(username: String, password: String): AuthResult {
        val json = JSONObject()
                .put("username", username)
                .put("password", password)

        val (responseCode, jsonContent) = postRequest("/authenticate", json)

        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED)
            return AuthResult(false)

        if (responseCode == HttpURLConnection.HTTP_OK && jsonContent != null)
            try {
                val content = JSONObject(jsonContent)

                return AuthResult(
                        token = content.getString("token"),
                        name = content.getString("name")
                )
            } catch (err: JSONException) {
                Log.e("Authenticate", Log.getStackTraceString(err))
            }

        throw BackendException()
    }

    private fun parseExpeditie(jsonExpeditie: JSONObject?): Expeditie? {
        if (jsonExpeditie == null) return null

        return try {
            jsonExpeditie.run{
                Expeditie(
                        getString("id"),
                        getString("name"),
                        getString("subtitle"),
                        getString("image")
                )
            }
        } catch (err: JSONException) {
            Log.e("GET_EXPEDITIES", Log.getStackTraceString(err))
            null
        }
    }

    fun getExpedities(token: String): ExpeditiesResult {
        val (responseCode, jsonContent) = getRequest("/expedities", token)

        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED)
            throw AuthException()

        if (responseCode == HttpURLConnection.HTTP_OK && jsonContent != null)
            try {
                val content = JSONArray(jsonContent)
                val expedities = mutableListOf<Expeditie>()

                for (i in 0 until content.length())
                    parseExpeditie(content.optJSONObject(i))
                            ?.let { expedities.add(it) }

                return ExpeditiesResult(
                        expedities = expedities
                )
            } catch (err: JSONException) {
                Log.e("Authenticate", Log.getStackTraceString(err))
            }

        throw BackendException()
    }
}