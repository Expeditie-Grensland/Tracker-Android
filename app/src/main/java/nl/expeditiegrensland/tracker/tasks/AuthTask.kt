package nl.expeditiegrensland.tracker.tasks

import android.os.AsyncTask
import android.util.Log
import kotlinx.android.synthetic.main.activity_login.*
import nl.expeditiegrensland.tracker.LoginActivity
import nl.expeditiegrensland.tracker.R
import nl.expeditiegrensland.tracker.helpers.ActivityHelper
import nl.expeditiegrensland.tracker.helpers.BackendHelper
import nl.expeditiegrensland.tracker.helpers.PreferenceHelper
import nl.expeditiegrensland.tracker.types.AuthResult
import nl.expeditiegrensland.tracker.types.AuthException
import nl.expeditiegrensland.tracker.types.BackendException
import java.lang.ref.WeakReference

class AuthTask(activity: LoginActivity, private val username: String, private val password: String) : AsyncTask<Void, Void, AuthResult>() {
    private val activityReference: WeakReference<LoginActivity>? =
            WeakReference(activity)

    override fun doInBackground(vararg params: Void): AuthResult {
        try {
            val result = BackendHelper.authenticate(username, password)

            if (result.success && result.token.isNotEmpty()) {
                val expedities = BackendHelper.getExpedities(result.token)
                return result.copy(expedities = expedities)
            }
            return result
        } catch (err: AuthException) {
            cancel(true)
        } catch (err: BackendException) {
            cancel(true)
        }

        return AuthResult(false)
    }

    override fun onPostExecute(result: AuthResult) {
        activityReference
                ?.get()
                ?.run {
                    authTask = null
                    showProgress(false)

                    Log.v("LoginResult", result.toString())

                    if (result.success && result.token.isNotEmpty()) {
                        if (PreferenceHelper.setToken(applicationContext, result.token))
                            ActivityHelper.openExpeditieSelect(applicationContext, result.expedities?.expedities)

                        finish()
                    } else {
                        username_field.error = getString(R.string.error_incorrect_credentials)
                        password_field.text.clear()
                        username_field.requestFocus()
                    }
                }
    }

    override fun onCancelled() {
        activityReference
                ?.get()
                ?.run {
                    authTask = null
                    showProgress(false)

                    username_field.error = getString(R.string.error_unknown)
                }
    }
}