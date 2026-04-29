package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.sprint

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SprintEntityRepository : JpaRepository<SprintEntity, Long>
