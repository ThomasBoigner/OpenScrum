package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DeveloperEntityRepository : JpaRepository<DeveloperEntity, Long>
