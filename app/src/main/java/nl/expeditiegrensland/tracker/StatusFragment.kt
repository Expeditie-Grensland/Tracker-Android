package nl.expeditiegrensland.tracker

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_status.view.*
import nl.expeditiegrensland.tracker.helpers.PreferenceHelper

class StatusFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_status, container, false)
        val token = PreferenceHelper.getToken(context)
        val expeditieName = PreferenceHelper.getExpeditie(context)?.name
        rootView.section_label.text = """$token
$expeditieName"""
        return rootView
    }

}