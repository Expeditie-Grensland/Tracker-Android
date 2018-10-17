package nl.expeditiegrensland.tracker.types

data class AuthenticateResult(val success: Boolean = true,
                              val token: String = "",
                              val name: String = "",
                              val expedities: ExpeditiesResult? = null)