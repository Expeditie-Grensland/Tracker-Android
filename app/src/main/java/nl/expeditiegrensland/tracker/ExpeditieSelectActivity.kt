package nl.expeditiegrensland.tracker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_expeditie_select.*
import nl.expeditiegrensland.tracker.types.Expeditie

class ExpeditieSelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val expedities = intent.getParcelableArrayListExtra<Expeditie>(Constants.BUNDLE_KEY_EXPEDITIES)

        setContentView(R.layout.activity_expeditie_select)

//        var text = expedities.toString()
//
//        if (expedities != null)
//            for (expeditie in expedities) {
//                text += expeditie.name
//            }

        textView.text = expedities.toString()
    }
}
