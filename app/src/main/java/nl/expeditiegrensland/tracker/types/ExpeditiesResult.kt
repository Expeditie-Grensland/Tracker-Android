package nl.expeditiegrensland.tracker.types

class ExpeditiesResult(
        val success: Boolean = true,
        val expedities: MutableList<Expeditie> = mutableListOf()
)