package nl.expeditiegrensland.tracker.helpers

import android.content.Context
import android.content.Intent
import nl.expeditiegrensland.tracker.LoginActivity
import nl.expeditiegrensland.tracker.MainActivity

object ActivityHelper {
    private fun openActivity(context: Context, activityClass: Class<*>): Unit =
            context.startActivity(Intent(context, activityClass))

    fun openMain(context: Context): Unit =
            openActivity(context, MainActivity::class.java)

    fun openLogin(context: Context): Unit =
            openActivity(context, LoginActivity::class.java)
}