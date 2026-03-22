package at.fhtw.openscrum.management.domain.model.user

enum class Role(
    val roleName: String,
    val displayName: String,
    val isManager: Boolean,
) {
    USER("ROLE_USER", "User", false),
    MANAGER("ROLE_MANAGER", "Manager", true),
}
