package nl.expeditiegrensland.tracker.tasks

import android.os.AsyncTask
import android.util.Log
import nl.expeditiegrensland.tracker.ExpeditieSelectActivity
import nl.expeditiegrensland.tracker.helpers.BackendHelper
import nl.expeditiegrensland.tracker.helpers.PreferenceHelper
import nl.expeditiegrensland.tracker.types.ExpeditiesResult
import java.lang.ref.WeakReference

class GetExpeditiesTask(activity: ExpeditieSelectActivity) : AsyncTask<Void, Void, ExpeditiesResult>() {

    private val activityReference: WeakReference<ExpeditieSelectActivity>? =
            WeakReference(activity)

    override fun doInBackground(vararg params: Void) =
            try {
                val token = activityReference?.get()?.run {
                    PreferenceHelper.getToken(this)
                }

                if (token != null && token.isNotEmpty())
                    BackendHelper.getExpedities(token)
                else
                    ExpeditiesResult(false)
            } catch (err: Throwable) {
                ExpeditiesResult(false)
            }

    override fun onPostExecute(result: ExpeditiesResult) {
        activityReference
                ?.get()
                ?.run {
                    showProgress(false)

                    Log.v("ExpeditiesResult", result.toString())

                    if (result.success)
                        showExpedities(result.expedities)
                }
    }
}