package nl.expeditiegrensland.tracker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_expeditie_select.*
import nl.expeditiegrensland.tracker.dummy.DummyContent
import nl.expeditiegrensland.tracker.types.Expeditie

class ExpeditieSelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val expedities = intent.getParcelableArrayListExtra<Expeditie>(Constants.BUNDLE_KEY_EXPEDITIES)

        setContentView(R.layout.activity_expeditie_select)

        if (recyclerView is RecyclerView)
            recyclerView.adapter = ExpeditieCardRecyclerViewAdapter(DummyContent.ITEMS, ::onListFragmentInteraction)
    }

    private fun onListFragmentInteraction(item: DummyContent.DummyItem?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
