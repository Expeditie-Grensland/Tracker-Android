package nl.expeditiegrensland.tracker.types

import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

@Serializable
data class Expeditie(@SerialId(1) val id: String,
                     @SerialId(2) val name: String,
                     @SerialId(3) val subtitle: String,
                     @SerialId(4) val color: String,
                     @SerialId(5) val image: String)