package nl.expeditiegrensland.tracker.types

class BackendResult (
        val responseCode: Int,
        val content: String? = null
)