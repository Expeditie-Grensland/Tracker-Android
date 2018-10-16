package nl.expeditiegrensland.tracker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_expeditie_select.*

class ExpeditieSelectActivity(val expedities: String? = null) : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val expedities = intent.getStringExtra(Constants.BUNDLE_KEY_EXPEDITIES)

        setContentView(R.layout.activity_expeditie_select)

        if (expedities != null)
            textView.text = expedities
    }
}
