package nl.expeditiegrensland.tracker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import nl.expeditiegrensland.tracker.types.Expeditie

class ExpeditieSelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val expedities = intent.getParcelableArrayListExtra<Expeditie>(Constants.BUNDLE_KEY_EXPEDITIES)

        setContentView(R.layout.activity_expeditie_select)
    }
}
