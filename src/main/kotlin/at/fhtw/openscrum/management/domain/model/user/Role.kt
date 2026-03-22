package at.fhtw.openscrum.management.domain.model.user

enum class Role(
    val roleName: String,
    val isManager: Boolean,
) {
    USER("ROLE_USER", false),
    MANAGER("ROLE_ADMIN", true),
}
