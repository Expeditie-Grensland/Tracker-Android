package nl.expeditiegrensland.tracker.types

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Expeditie(val id: String,
                     val name: String,
                     val subtitle: String,
                     val image: String) : Parcelable