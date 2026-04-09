package at.fhtw.openscrum.management.application.dtos

enum class RoleDto(
    val displayName: String,
    val isManager: Boolean,
) {
    USER("User", false),
    MANAGER("Manager", true),
}
