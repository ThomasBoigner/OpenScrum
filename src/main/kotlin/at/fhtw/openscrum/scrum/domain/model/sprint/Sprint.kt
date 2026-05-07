package at.fhtw.openscrum.scrum.domain.model.sprint

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class Sprint(
    val id: Long? = null,
    val sprintId: SprintId,
    val sprintName: String,
    val startDate: LocalDate = LocalDate.now(),
    endDate: LocalDate,
    status: SprintStatus = SprintStatus.NOT_PLANNED,
    sprintGoal: String? = null,
    val sprintBacklogItems: MutableSet<SprintBacklogItem> = mutableSetOf(),
    val productBacklogItemCommittedEvents: MutableList<ProductBacklogItemCommitted> = mutableListOf(),
) {
    constructor(
        sprintId: SprintId,
        sprintNumber: Int,
        startDate: LocalDate = LocalDate.now(),
        sprintLength: Long,
    ) : this(
        sprintId = sprintId,
        sprintName = "Sprint $sprintNumber",
        startDate = startDate,
        endDate = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).plusWeeks(sprintLength).minusDays(1),
    )

    var endDate: LocalDate = endDate
        private set

    var status: SprintStatus = status
        private set

    var sprintGoal: String? = sprintGoal
        private set

    fun planSprint(
        scrumMaster: ScrumMaster?,
        sprintGoal: String,
        productBacklogItems: Set<ProductBacklogItem>,
    ) {
        require(sprintGoal.isNotBlank()) { "Sprint goal cannot be blank" }
        require(productBacklogItems.isNotEmpty()) { "Sprint backlog cannot be empty" }
        require(scrumMaster?.teamMemberId?.projectId == this.sprintId.projectId) {
            "You are not the scrum master of this project"
        }
        require(!status.isPlanned) { "The sprint cannot be planned" }
        require(productBacklogItems.all { it.productBacklogItemId.projectId == sprintId.projectId }) {
            "Cannot commit product backlog items of another project to this sprint"
        }

        this.sprintGoal = sprintGoal
        this.status = SprintStatus.IN_PROGRESS

        productBacklogItems.forEach { pbi ->
            sprintBacklogItems.add(
                SprintBacklogItem(
                    productBacklogItemId = pbi.productBacklogItemId,
                    title = pbi.title,
                    description = pbi.description,
                ),
            )
            productBacklogItemCommittedEvents.add(
                ProductBacklogItemCommitted(
                    productBacklogItemId = pbi.productBacklogItemId,
                ),
            )
        }
    }

    fun getSprintBacklogItems(status: SprintBacklogItemStatus): List<SprintBacklogItem> = sprintBacklogItems.filter { it.status == status }

    override fun toString(): String = "Sprint(sprintId=$sprintId, startDate=$startDate, endDate=$endDate, status=$status)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sprint

        return sprintId == other.sprintId
    }

    override fun hashCode(): Int = sprintId.hashCode()
}
