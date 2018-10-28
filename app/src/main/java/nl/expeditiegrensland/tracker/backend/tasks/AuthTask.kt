package nl.expeditiegrensland.tracker.backend.tasks

import android.os.AsyncTask
import kotlinx.android.synthetic.main.activity_login.*
import nl.expeditiegrensland.tracker.LoginActivity
import nl.expeditiegrensland.tracker.R
import nl.expeditiegrensland.tracker.backend.BackendHelper
import nl.expeditiegrensland.tracker.backend.types.AuthResult
import nl.expeditiegrensland.tracker.helpers.ActivityHelper
import nl.expeditiegrensland.tracker.helpers.PreferenceHelper
import java.lang.ref.WeakReference
import java.net.HttpURLConnection

class AuthTask(activity: LoginActivity,
               private val username: String,
               private val password: String) : AsyncTask<Void, Void, AuthResult>() {

    private val activityRef: WeakReference<LoginActivity>? =
            WeakReference(activity)

    override fun doInBackground(vararg params: Void) = AuthResult.catchError {
        BackendHelper.authenticate(username, password)
    }

    override fun onPostExecute(result: AuthResult) {
        activityRef
                ?.get()
                ?.run {
                    authTask = null
                    showProgress(false)

                    result.runIfValue {
                        if (PreferenceHelper.setToken(this, it.token))
                            ActivityHelper.openExpeditieSelect(this)

                        finish()
                    }

                    result.runIfError {
                        if (it.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            username_field.error = getString(R.string.error_incorrect_credentials)
                            password_field.text.clear()
                            username_field.requestFocus()
                        } else {
                            username_field.error = getString(R.string.error_unknown)
                        }
                    }
                }
    }
}