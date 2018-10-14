package nl.expeditiegrensland.tracker.types

class BackendResult<T> (
        val responseCode: Int,
        val content: T
)