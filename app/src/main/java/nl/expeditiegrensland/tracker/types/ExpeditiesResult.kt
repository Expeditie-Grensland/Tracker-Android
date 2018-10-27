package nl.expeditiegrensland.tracker.types

data class ExpeditiesResult(val success: Boolean = true,
                            val expedities: List<Expeditie> = listOf())