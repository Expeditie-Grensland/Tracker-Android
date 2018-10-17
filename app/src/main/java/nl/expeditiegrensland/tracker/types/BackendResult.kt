package nl.expeditiegrensland.tracker.types

data class BackendResult(val responseCode: Int,
                         val content: String? = null)