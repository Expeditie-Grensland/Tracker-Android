package nl.expeditiegrensland.tracker.layout.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_expeditie_card.view.*
import kotlinx.android.synthetic.main.fragment_status.*
import kotlinx.android.synthetic.main.fragment_status.view.*
import nl.expeditiegrensland.tracker.Constants
import nl.expeditiegrensland.tracker.R
import nl.expeditiegrensland.tracker.glide.GlideApp
import nl.expeditiegrensland.tracker.helpers.PreferenceHelper
import nl.expeditiegrensland.tracker.types.Expeditie

class StatusFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_status, container, false)

        val linearLayout = rootView.linear_layout

        val expeditie = PreferenceHelper.getExpeditie(context)

        expeditie!!
        linearLayout.addView(getExpeditieCard(inflater, linearLayout, expeditie))

        return rootView
    }

    private fun getExpeditieCard(inflater: LayoutInflater, parent: LinearLayout, expeditie: Expeditie) =
            inflater.inflate(R.layout.fragment_expeditie_card, parent, false).run {
                isClickable = false
                isFocusable = false

                name.text = expeditie.name
                subtitle.text = expeditie.subtitle

                GlideApp.with(this)
                        .load(Constants.BASE_URL + expeditie.image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(ColorDrawable(Color.parseColor(expeditie.color)))
                        .into(image)

                this
            }
}