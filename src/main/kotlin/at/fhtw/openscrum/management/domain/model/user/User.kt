package at.fhtw.openscrum.management.domain.model.user

class User(
    val id: Long? = null,
    val userId: UserId = UserId(),
    username: String,
    emailAddress: EmailAddress,
    fullName: FullName,
    password: String,
    role: Role = Role.USER,
    val userInformationChangedEvents: MutableList<UserInformationChanged> = mutableListOf(),
) {
    var username: String = ""
        private set(value) {
            require(value.isNotBlank()) { "Username must not be blank!" }
            field = value
        }

    var emailAddress: EmailAddress = emailAddress
        private set

    var fullName: FullName = fullName
        private set

    var password: String = ""
        private set(value) {
            require(value.isNotBlank()) { "Password must not be blank!" }
            field = value
        }

    var role: Role = role
        private set

    init {
        this.username = username
        this.password = password
    }

    fun update(
        authenticatedUser: User,
        username: String,
        emailAddress: EmailAddress,
        fullName: FullName,
        password: String?,
    ) {
        require(authenticatedUser.role.isManager || authenticatedUser.userId == userId) {
            "You have no permission to update other users!"
        }
        this.username = username
        this.emailAddress = emailAddress
        this.fullName = fullName
        password?.let { this.password = password }
        userInformationChangedEvents.add(UserInformationChanged(userId, username, emailAddress, fullName))
    }

    fun promote(authenticatedUser: User) {
        require(authenticatedUser.role.isManager) { "You have no permission to promote users!" }
        role = Role.MANAGER
    }

    fun demote(authenticatedUser: User) {
        require(authenticatedUser.role.isManager) { "You have no permission to demote users!" }
        require(authenticatedUser.userId != userId) { "You can not demote your own account!" }
        role = Role.USER
    }

    override fun toString(): String =
        "User(userId=$userId, username='$username', emailAddress=$emailAddress, fullName=$fullName, role=$role)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        return userId == other.userId
    }

    override fun hashCode(): Int = userId.hashCode()
}
