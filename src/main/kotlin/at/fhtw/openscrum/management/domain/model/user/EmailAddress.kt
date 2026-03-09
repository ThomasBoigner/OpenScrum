package at.fhtw.openscrum.management.domain.model.user

import java.util.regex.Pattern

data class EmailAddress(
    val emailAddress: String,
) {
    init {
        require(emailAddress.isNotBlank()) { "Email address must not be blank!" }
        require(emailAddress.length < 128) { "Email address must not be longer than 128 characters!" }
        require(Pattern.matches("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*", emailAddress)) {
            "Email format is not valid!"
        }
    }
}
