package nl.expeditiegrensland.tracker.layout.expeditieselect

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_expeditie_card.view.*
import nl.expeditiegrensland.tracker.Constants
import nl.expeditiegrensland.tracker.R
import nl.expeditiegrensland.tracker.helpers.DownloadImageTask
import nl.expeditiegrensland.tracker.types.Expeditie


class ExpeditieCardRecyclerViewAdapter(private val expedities: List<Expeditie>,
                                       private val mListener: (Expeditie?) -> Unit)
    : RecyclerView.Adapter<ExpeditieCardRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.fragment_expeditie_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = expedities[pos]

        with(holder.view) {
            name.text = item.name
            subtitle.text = item.subtitle

            DownloadImageTask(image)
                    .execute(Constants.BASE_URL + item.image)


            setOnClickListener {
                mListener(item)
            }
        }
    }

    override fun getItemCount(): Int = expedities.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}
