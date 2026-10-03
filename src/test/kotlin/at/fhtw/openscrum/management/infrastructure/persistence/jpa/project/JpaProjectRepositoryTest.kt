package at.fhtw.openscrum.management.infrastructure.persistence.jpa.project

import at.fhtw.openscrum.management.domain.model.project.Project
import at.fhtw.openscrum.management.domain.model.project.ProjectRepository
import at.fhtw.openscrum.management.domain.model.user.UserId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("postgres")
class JpaProjectRepositoryTest {
    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var projectEntityRepository: ProjectEntityRepository

    @BeforeEach
    fun cleanUp() {
        projectEntityRepository.deleteAll()
    }

    @Test
    fun ensureFindAllWorksProperly() {
        // Given
        val openScrum =
            Project(
                projectName = "OpenScrum",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        val scrumBoard =
            Project(
                projectName = "ScrumBoard",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        val webShop =
            Project(
                projectName = "WebShop",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        projectRepository.save(openScrum)
        projectRepository.save(scrumBoard)
        projectRepository.save(webShop)

        // When
        val result = projectRepository.findAll("sCRUM")

        // Then
        assertThat(result).containsExactlyInAnyOrder(openScrum, scrumBoard)
    }

    @Test
    fun ensureFindAllReturnsEmptyListWhenNothingMatches() {
        // Given
        val project =
            Project(
                projectName = "OpenScrum",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        projectRepository.save(project)

        // When
        val result = projectRepository.findAll("Kanban")

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun ensureFindAllReturnsAllProjectsForEmptyName() {
        // Given
        val openScrum =
            Project(
                projectName = "OpenScrum",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        val webShop =
            Project(
                projectName = "WebShop",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        projectRepository.save(openScrum)
        projectRepository.save(webShop)

        // When
        val result = projectRepository.findAll("")

        // Then
        assertThat(result).containsExactlyInAnyOrder(openScrum, webShop)
    }

    @Test
    fun ensureFindAllReturnsAllProjectsForNullName() {
        // Given
        val openScrum =
            Project(
                projectName = "OpenScrum",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        val webShop =
            Project(
                projectName = "WebShop",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        projectRepository.save(openScrum)
        projectRepository.save(webShop)

        // When
        val result = projectRepository.findAll()

        // Then
        assertThat(result).containsExactlyInAnyOrder(openScrum, webShop)
    }

    @Test
    fun ensureSaveWorksProperly() {
        // Given
        val project =
            Project(
                projectName = "OpenScrum",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
                developerIds = setOf(UserId()),
            )

        // When
        projectRepository.save(project)

        // Then
        val savedProjects = projectRepository.findAll()
        assertThat(savedProjects).hasSize(1)
        assertThat(savedProjects.first()).isEqualTo(project)
    }

    @Test
    fun ensureDeleteWorksProperly() {
        // Given
        val project =
            Project(
                projectName = "OpenScrum",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
                developerIds = setOf(UserId()),
            )
        projectRepository.save(project)

        // When
        projectRepository.delete(projectRepository.findByProjectId(project.projectId)!!)

        // Then
        assertThat(projectRepository.findByProjectId(project.projectId)).isNull()
        assertThat(projectRepository.findAll()).isEmpty()
    }

    @Test
    fun ensureFindProjectsOfUserReturnsProjectWhereUserIsProductOwner() {
        // Given
        val userId = UserId()
        val userProject =
            Project(
                projectName = "UserProject",
                productOwnerId = userId,
                scrumMasterId = UserId(),
            )
        val otherProject =
            Project(
                projectName = "OtherProject",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        projectRepository.save(userProject)
        projectRepository.save(otherProject)

        // When
        val result = projectRepository.findProjectsOfUser(userId)

        // Then
        assertThat(result).containsExactly(userProject)
    }

    @Test
    fun ensureFindProjectsOfUserReturnsProjectWhereUserIsScrumMaster() {
        // Given
        val userId = UserId()
        val userProject =
            Project(
                projectName = "UserProject",
                productOwnerId = UserId(),
                scrumMasterId = userId,
            )
        val otherProject =
            Project(
                projectName = "OtherProject",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        projectRepository.save(userProject)
        projectRepository.save(otherProject)

        // When
        val result = projectRepository.findProjectsOfUser(userId)

        // Then
        assertThat(result).containsExactly(userProject)
    }

    @Test
    fun ensureFindProjectsOfUserReturnsProjectWhereUserIsDeveloper() {
        // Given
        val userId = UserId()
        val userProject =
            Project(
                projectName = "UserProject",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
                developerIds = setOf(userId),
            )
        val otherProject =
            Project(
                projectName = "OtherProject",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        projectRepository.save(userProject)
        projectRepository.save(otherProject)

        // When
        val result = projectRepository.findProjectsOfUser(userId)

        // Then
        assertThat(result).containsExactly(userProject)
    }

    @Test
    fun ensureFindProjectsOfUserFiltersByNameQueryIgnoringCase() {
        // Given
        val userId = UserId()
        val openScrum =
            Project(
                projectName = "OpenScrum",
                productOwnerId = userId,
                scrumMasterId = UserId(),
            )
        val webShop =
            Project(
                projectName = "WebShop",
                productOwnerId = UserId(),
                scrumMasterId = userId,
            )
        val scrumBoard =
            Project(
                projectName = "ScrumBoard",
                productOwnerId = userId,
                scrumMasterId = UserId(),
            )
        projectRepository.save(openScrum)
        projectRepository.save(webShop)
        projectRepository.save(scrumBoard)

        // When
        val result = projectRepository.findProjectsOfUser(userId, "sCRUM")

        // Then
        assertThat(result).containsExactly(openScrum, scrumBoard)
    }

    @Test
    fun ensureFindProjectsOfUserReturnsEmptyListWhenNothingMatches() {
        // Given
        val userId = UserId()
        val openScrum =
            Project(
                projectName = "OpenScrum",
                productOwnerId = userId,
                scrumMasterId = UserId(),
            )
        projectRepository.save(openScrum)

        // When
        val result = projectRepository.findProjectsOfUser(userId, "Kanban")

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun ensureFindProjectsOfUserReturnsAllProjectsOfUserForEmptyNameQuery() {
        // Given
        val userId = UserId()
        val openScrum =
            Project(
                projectName = "OpenScrum",
                productOwnerId = userId,
                scrumMasterId = UserId(),
            )
        val webShop =
            Project(
                projectName = "WebShop",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
                developerIds = setOf(userId),
            )
        val scrumBoard =
            Project(
                projectName = "ScrumBoard",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        projectRepository.save(openScrum)
        projectRepository.save(webShop)
        projectRepository.save(scrumBoard)

        // When
        val result = projectRepository.findProjectsOfUser(userId, "")

        // Then
        assertThat(result).containsExactlyInAnyOrder(openScrum, webShop)
    }

    @Test
    fun ensureFindProjectsOfUserReturnsAllProjectsOfUserForNullNameQuery() {
        // Given
        val userId = UserId()
        val openScrum =
            Project(
                projectName = "OpenScrum",
                productOwnerId = userId,
                scrumMasterId = UserId(),
            )
        val webShop =
            Project(
                projectName = "WebShop",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
                developerIds = setOf(userId),
            )
        val scrumBoard =
            Project(
                projectName = "ScrumBoard",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        projectRepository.save(openScrum)
        projectRepository.save(webShop)
        projectRepository.save(scrumBoard)

        // When
        val result = projectRepository.findProjectsOfUser(userId, null)

        // Then
        assertThat(result).containsExactlyInAnyOrder(openScrum, webShop)
    }

    @Test
    fun ensureExistsByProjectNameWorksProperly() {
        // Given
        val project =
            Project(
                projectName = "OpenScrum",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        projectRepository.save(project)

        // When
        val result = projectRepository.existsByProjectName(project.projectName)

        // Then
        assertThat(result).isTrue()
    }
}
