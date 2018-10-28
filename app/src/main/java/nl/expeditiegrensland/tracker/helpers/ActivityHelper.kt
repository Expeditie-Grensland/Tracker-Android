package nl.expeditiegrensland.tracker.helpers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import nl.expeditiegrensland.tracker.layout.expeditieselect.ExpeditieSelectActivity
import nl.expeditiegrensland.tracker.layout.login.LoginActivity
import nl.expeditiegrensland.tracker.layout.main.MainActivity

object ActivityHelper {
    private fun openActivity(context: Context, activityClass: Class<*>, bundle: Bundle? = null) {
        val intent = Intent(context, activityClass)
        bundle?.let {
            intent.putExtras(it)
        }
        context.startActivity(intent)
    }

    fun openMain(context: Context): Unit =
            openActivity(context, MainActivity::class.java)

    fun openExpeditieSelect(context: Context): Unit =
        openActivity(context, ExpeditieSelectActivity::class.java)

    fun openLogin(context: Context): Unit =
            openActivity(context, LoginActivity::class.java)
}