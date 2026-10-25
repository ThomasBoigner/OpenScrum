package at.fhtw.openscrum.management.presentation.forms

import at.fhtw.openscrum.management.application.command.CreateProjectCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class CreateProjectForm(
    @NotBlank(message = "Project name must not be blank!")
    @Size(max = 255, message = "Project name must not be longer than 255 characters!")
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
