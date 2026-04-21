package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TeamMemberEntityRepository : JpaRepository<TeamMemberEntity, Long> {
    fun findByProjectIdAndUsername(
        projectId: UUID,
        username: String,
    ): TeamMemberEntity?
}
