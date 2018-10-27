package nl.expeditiegrensland.tracker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_expeditie_select.*
import nl.expeditiegrensland.tracker.helpers.ActivityHelper
import nl.expeditiegrensland.tracker.helpers.PreferenceHelper
import nl.expeditiegrensland.tracker.helpers.showOrHide
import nl.expeditiegrensland.tracker.tasks.GetExpeditiesTask
import nl.expeditiegrensland.tracker.types.Expeditie

class ExpeditieSelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expeditie_select)

        val expedities =
                intent.getParcelableArrayListExtra<Expeditie>(Constants.BUNDLE_KEY_EXPEDITIES)

        if (expedities != null)
            showExpedities(expedities)
        else {
            showProgress(true)
            GetExpeditiesTask(this).execute()
        }
    }

    fun showExpedities(expedities: List<Expeditie>) =
            recyclerView?.run {
                adapter = ExpeditieCardRecyclerViewAdapter(expedities, ::onListFragmentInteraction)
            }

    private fun onListFragmentInteraction(item: Expeditie?) {
        item?.let {
            PreferenceHelper.setExpeditie(this, it)
            ActivityHelper.openMain(this)
            finish()
        }
    }

    fun showProgress(show: Boolean) = showOrHide(progress_bar, show)
}
