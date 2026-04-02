package at.fhtw.openscrum.management.application

import at.fhtw.openscrum.management.application.command.CreateProjectCommand
import at.fhtw.openscrum.management.domain.model.project.Project
import at.fhtw.openscrum.management.domain.model.project.ProjectRepository
import at.fhtw.openscrum.management.domain.model.project.ProjectService
import at.fhtw.openscrum.management.domain.model.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProjectApplicationService(
    private val projectService: ProjectService,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
) {
    @Transactional(readOnly = false)
    fun createProject(
        authenticatedUser: String,
        command: CreateProjectCommand,
    ): Project = throw NotImplementedError()
}
