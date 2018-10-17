package nl.expeditiegrensland.tracker.types

data class ExpeditiesResult(val success: Boolean = true,
                            val expedities: MutableList<Expeditie> = mutableListOf())