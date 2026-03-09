package at.fhtw.openscrum.management.infrastructure.persistence.jpa.user

import at.fhtw.openscrum.management.domain.model.user.FullName
import jakarta.persistence.Embeddable

@Embeddable
data class FullNameEmbeddable(
    val firstName: String,
    val lastName: String,
) {
    constructor(fullName: FullName) : this(fullName.firstName, fullName.lastName)
    constructor() : this(firstName = "", lastName = "")

    fun toFullName() = FullName(firstName, lastName)
}
