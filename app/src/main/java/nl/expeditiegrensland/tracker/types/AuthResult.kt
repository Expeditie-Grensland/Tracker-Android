package nl.expeditiegrensland.tracker.types

data class AuthResult(val success: Boolean = true,
                      val token: String = "",
                      val name: String = "",
                      val expedities: ExpeditiesResult? = null)