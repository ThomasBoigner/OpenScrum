package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwnerRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@SpringBootTest
@ActiveProfiles("postgres")
class JpaProductOwnerRepositoryTest {
    @Autowired
    lateinit var productOwnerRepository: ProductOwnerRepository

    @Autowired
    lateinit var productOwnerEntityRepository: ProductOwnerEntityRepository

    @BeforeEach
    fun cleanUp() {
        productOwnerEntityRepository.deleteAll()
    }

    @Test
    fun ensureFindByProjectIdWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "jsmith",
                fullName = FullName(firstName = "Jane", lastName = "Smith"),
            )
        productOwnerRepository.save(productOwner)

        // When
        val result = productOwnerRepository.findByProjectId(projectId)

        // Then
        assertThat(result).isNotNull
        assertThat(result).isEqualTo(productOwner)
    }

    @Test
    fun ensureSaveWorksProperly() {
        // Given
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = UUID.randomUUID()),
                username = "jsmith",
                fullName = FullName(firstName = "Jane", lastName = "Smith"),
            )

        // When
        productOwnerRepository.save(productOwner)

        // Then
        val savedEntities = productOwnerEntityRepository.findAll()
        assertThat(savedEntities).hasSize(1)
        val savedProductOwner = savedEntities.first().toProductOwner()
        assertThat(savedProductOwner.id).isNotNull
        assertThat(savedProductOwner).isEqualTo(productOwner)
    }

    @Test
    fun ensureFindByUsernameWorksProperly() {
        // Given
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = UUID.randomUUID()),
                username = "jsmith",
                fullName = FullName(firstName = "Jane", lastName = "Smith"),
            )
        productOwnerRepository.save(productOwner)

        // When
        val result = productOwnerRepository.findByUsername("jsmith")

        // Then
        assertThat(result).isNotNull
        assertThat(result).isEqualTo(productOwner)
    }
}
