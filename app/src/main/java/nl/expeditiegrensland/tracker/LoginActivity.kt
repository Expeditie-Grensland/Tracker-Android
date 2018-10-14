package nl.expeditiegrensland.tracker

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_login.*
import nl.expeditiegrensland.tracker.helpers.ActivityHelper
import nl.expeditiegrensland.tracker.helpers.BackendHelper
import nl.expeditiegrensland.tracker.helpers.PreferenceHelper
import nl.expeditiegrensland.tracker.types.AuthenticateResult
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection


class LoginActivity : AppCompatActivity() {
    private var authTask: AuthTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (PreferenceHelper.getToken(applicationContext) != "") {
            ActivityHelper.openMain(applicationContext)
            finish()
        }

        setContentView(R.layout.activity_login)

        password_field.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        sign_in_button.setOnClickListener { attemptLogin() }

        val token = PreferenceHelper.getToken(applicationContext)
        Log.v("TokenStatus", token)
    }

    private fun attemptLogin() {
        if (authTask != null) {
            return
        }

        // Reset errors.
        username_field.error = null
        password_field.error = null

        // Store values at the time of the login attempt.
        val emailStr = username_field.text.toString()
        val passwordStr = password_field.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(passwordStr)) {
            password_field.error = getString(R.string.error_field_required)
            focusView = password_field
            cancel = true
        }

        if (TextUtils.isEmpty(emailStr)) {
            username_field.error = getString(R.string.error_field_required)
            focusView = username_field
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            authTask = AuthTask(emailStr, passwordStr)
            authTask!!.execute(null as Void?)
        }
    }

    private fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        login_progress.visibility = if (show) View.VISIBLE else View.INVISIBLE
        login_progress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        login_progress.visibility = if (show) View.VISIBLE else View.INVISIBLE
                    }
                })
    }


    inner class AuthTask internal constructor(private val username: String, private val password: String) : AsyncTask<Void, Void, AuthenticateResult>() {
        override fun doInBackground(vararg params: Void) =
                BackendHelper.authenticate(username, password, ::cancel)

        override fun onPostExecute(result: AuthenticateResult) {
            authTask = null
            showProgress(false)

            Log.v("LoginResult", result.toString())

            if (result.success && result.token.length > 64) {
                if (!PreferenceHelper.setToken(applicationContext, result.token)) finish()

                ActivityHelper.openMain(applicationContext)
                finish()
            } else {
                username_field.error = getString(R.string.error_incorrect_credentials)
                password_field.text.clear()
                username_field.requestFocus()
            }
        }

        override fun onCancelled() {
            authTask = null
            showProgress(false)

            username_field.error = getString(R.string.error_unknown)
        }
    }
}
