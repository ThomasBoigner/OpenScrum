package at.fhtw.openscrum.management.domain.model.user

class User(
    val id: Long? = null,
    val userId: UserId = UserId(),
    var username: String,
    var emailAddress: EmailAddress,
    var fullName: FullName,
    val password: String,
    var role: Role,
) {
    init {
        require(username.isNotBlank()) { "Username must not be blank!" }
    }

    override fun toString(): String = "User(userId=$userId, username='$username', emailAddress=$emailAddress, fullName=$fullName)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        return userId == other.userId
    }

    override fun hashCode(): Int = userId.hashCode()
}
