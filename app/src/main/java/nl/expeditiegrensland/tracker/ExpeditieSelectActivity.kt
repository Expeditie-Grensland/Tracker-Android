package nl.expeditiegrensland.tracker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import kotlinx.android.synthetic.main.activity_expeditie_select.*
import nl.expeditiegrensland.tracker.helpers.ActivityHelper
import nl.expeditiegrensland.tracker.helpers.PreferenceHelper
import nl.expeditiegrensland.tracker.types.Expeditie

class ExpeditieSelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val expedities = intent.getParcelableArrayListExtra<Expeditie>(Constants.BUNDLE_KEY_EXPEDITIES)

        setContentView(R.layout.activity_expeditie_select)

        if (recyclerView is RecyclerView)
            recyclerView.adapter = ExpeditieCardRecyclerViewAdapter(expedities, ::onListFragmentInteraction)
    }

    private fun onListFragmentInteraction(item: Expeditie?) {
        item?.let {
            PreferenceHelper.setExpeditie(this, it)
            ActivityHelper.openMain(this)
            finish()
        }
    }
}
