package nl.expeditiegrensland.tracker.types

class AuthenticateResult(
        val success: Boolean = true,
        val token: String = "",
        val name: String = "",
        var expedities: String? = null
)