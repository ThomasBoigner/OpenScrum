package at.fhtw.openscrum.management.presentation.forms

import at.fhtw.openscrum.management.application.command.CreateProjectCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class CreateProjectForm(
    @NotBlank(message = "Project name must not be blank!")
    var projectName: String = "",
    @NotNull(message = "A product owner must be selected!")
    var productOwnerId: UUID? = null,
    @NotNull(message = "A scrum master must be selected!")
    var scrumMasterId: UUID? = null,
    var developerIds: Set<UUID> = setOf(),
) {
    fun toCreateProjectCommand(): CreateProjectCommand =
        CreateProjectCommand(
            projectName = projectName,
            productOwnerId = productOwnerId!!,
            scrumMasterId = scrumMasterId!!,
            developerIds = developerIds,
        )
}
