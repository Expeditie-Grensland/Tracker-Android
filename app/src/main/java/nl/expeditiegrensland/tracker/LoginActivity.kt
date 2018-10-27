package nl.expeditiegrensland.tracker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_login.*
import nl.expeditiegrensland.tracker.helpers.ActivityHelper
import nl.expeditiegrensland.tracker.helpers.PreferenceHelper
import nl.expeditiegrensland.tracker.helpers.showOrHide
import nl.expeditiegrensland.tracker.tasks.AuthTask


class LoginActivity : AppCompatActivity() {
    var authTask: AuthTask? = null

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
            authTask = AuthTask(this, emailStr, passwordStr)
            authTask!!.execute(null as Void?)
        }
    }

    fun showProgress(show: Boolean) = showOrHide(progress_bar, show)
}
