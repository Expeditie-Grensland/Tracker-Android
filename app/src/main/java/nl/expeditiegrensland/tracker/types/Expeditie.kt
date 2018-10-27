package nl.expeditiegrensland.tracker.types

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Expeditie(@SerialId(1) val id: String,
                     @SerialId(2) val name: String,
                     @SerialId(3) val subtitle: String,
                     @SerialId(4) val image: String) : Parcelable