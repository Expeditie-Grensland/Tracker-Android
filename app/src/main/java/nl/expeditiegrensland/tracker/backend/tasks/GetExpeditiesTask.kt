package nl.expeditiegrensland.tracker.backend.tasks

import android.os.AsyncTask
import nl.expeditiegrensland.tracker.ExpeditieSelectActivity
import nl.expeditiegrensland.tracker.backend.BackendHelper
import nl.expeditiegrensland.tracker.backend.types.BackendException
import nl.expeditiegrensland.tracker.backend.types.ExpeditiesResult
import nl.expeditiegrensland.tracker.helpers.PreferenceHelper
import java.lang.ref.WeakReference
import java.net.HttpURLConnection

class GetExpeditiesTask(activity: ExpeditieSelectActivity) : AsyncTask<Void, Void, ExpeditiesResult>() {

    private val activityRef: WeakReference<ExpeditieSelectActivity>? =
            WeakReference(activity)

    override fun doInBackground(vararg params: Void) = ExpeditiesResult.catchError {
        val token = activityRef
                ?.get()
                ?.run { PreferenceHelper.getToken(this) }

        if (token == null || token.isEmpty())
            throw BackendException(HttpURLConnection.HTTP_UNAUTHORIZED)

        BackendHelper.getExpedities(token)
    }

    override fun onPostExecute(result: ExpeditiesResult) {
        activityRef
                ?.get()
                ?.run {
                    showProgress(false)

                    result.runIfValue {
                        showExpedities(it)
                    }
                }
    }
}