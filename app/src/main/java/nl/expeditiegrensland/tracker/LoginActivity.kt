package nl.expeditiegrensland.tracker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_login.*
import nl.expeditiegrensland.tracker.helpers.ActivityHelper
import nl.expeditiegrensland.tracker.helpers.PreferenceHelper
import nl.expeditiegrensland.tracker.helpers.showOrHide
import nl.expeditiegrensland.tracker.backend.tasks.AuthTask


class LoginActivity : AppCompatActivity() {
    var authTask: AuthTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (PreferenceHelper.getToken(this).isNotEmpty()) {
            if (PreferenceHelper.getExpeditie(this) != null) {
                ActivityHelper.openMain(this)
            } else {
                ActivityHelper.openExpeditieSelect(this)
            }
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

        val token = PreferenceHelper.getToken(this)

        Log.v("TokenStatus", token)
    }

    private fun attemptLogin() {
        if (authTask != null) {
            return
        }

        username_field.error = null
        password_field.error = null

        val emailStr = username_field.text.toString()
        val passwordStr = password_field.text.toString()

        var cancel = false
        var focusView: View? = null

        if (passwordStr.isEmpty()) {
            password_field.error = getString(R.string.error_field_required)
            focusView = password_field
            cancel = true
        }

        if (emailStr.isEmpty()) {
            username_field.error = getString(R.string.error_field_required)
            focusView = username_field
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            showProgress(true)
            authTask = AuthTask(this, emailStr, passwordStr)
            authTask!!.execute(null as Void?)
        }
    }

    fun showProgress(show: Boolean) = showOrHide(progress_bar, show)
}
