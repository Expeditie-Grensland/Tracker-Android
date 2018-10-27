package nl.expeditiegrensland.tracker.helpers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

fun showOrHide(view: View, show: Boolean) {
    view.run {
        visibility = View.VISIBLE

        animate()
                .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = if (show) View.VISIBLE else View.INVISIBLE
                    }
                })
    }
}