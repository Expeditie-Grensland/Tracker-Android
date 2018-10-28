package nl.expeditiegrensland.tracker.layout.expeditieselect

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_expeditie_select.*
import nl.expeditiegrensland.tracker.R
import nl.expeditiegrensland.tracker.helpers.ActivityHelper
import nl.expeditiegrensland.tracker.helpers.HelperFunctions
import nl.expeditiegrensland.tracker.helpers.PreferenceHelper
import nl.expeditiegrensland.tracker.types.Expeditie

class ExpeditieSelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expeditie_select)

        setSupportActionBar(toolbar)

        GetExpeditiesTask(this).execute()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_expeditie_select, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.action_sign_out -> signOut()
                else -> super.onOptionsItemSelected(item)
            }

    private fun signOut(): Boolean {
        PreferenceHelper.removeToken(this)
        ActivityHelper.openLogin(this)
        finish()
        return true
    }

    fun showExpedities(expedities: List<Expeditie>) {
        recycler_view?.run {
            adapter = ExpeditieCardRecyclerViewAdapter(expedities, ::onListFragmentInteraction)
        }
    }

    private fun onListFragmentInteraction(item: Expeditie?) {
        item?.let {
            PreferenceHelper.setExpeditie(this, it)
            ActivityHelper.openMain(this)
            finish()
        }
    }

    fun showProgress(show: Boolean) = HelperFunctions.showOrHide(progress_bar, show)
}
