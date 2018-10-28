package nl.expeditiegrensland.tracker.layout.login

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_login.*
import nl.expeditiegrensland.tracker.R
import nl.expeditiegrensland.tracker.helpers.ActivityHelper
import nl.expeditiegrensland.tracker.helpers.HelperFunctions
import nl.expeditiegrensland.tracker.helpers.PreferenceHelper


class LoginActivity : AppCompatActivity() {
    var authTask: AuthTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (PreferenceHelper.getToken(this)!!.isNotEmpty()) {
            if (PreferenceHelper.getExpeditie(this) != null)
                ActivityHelper.openMain(this)
            else
                ActivityHelper.openExpeditieSelect(this)

            finish()
        }

        setContentView(R.layout.activity_login)

        password_field.setOnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                true
            } else false
        }

        sign_in_button.setOnClickListener {
            attemptLogin()
        }

        val token = PreferenceHelper.getToken(this)

        Log.v("TokenStatus", token)
    }

    private fun attemptLogin() {
        if (authTask == null) {
            authTask = AuthTask(this,
                    username_field.text.toString(),
                    password_field.text.toString())

            authTask?.execute()
        }
    }

    fun showProgress(show: Boolean) = HelperFunctions.showOrHide(progress_bar, show)
}
