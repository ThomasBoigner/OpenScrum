package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.project

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository("scrumProjectEntityRepository")
interface ProjectEntityRepository : JpaRepository<ProjectEntity, Long>
