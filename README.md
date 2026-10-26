# OpenScrum

OpenScrum is a web application for running Scrum projects. Managers administer users and projects, and each project's
Scrum team (product owner, scrum master, developers) runs it through sprints: backlog, sprint planning, a Kanban board
and automatic sprint completion.

It is a server-rendered Spring Boot application written in Kotlin, using Thymeleaf and HTMX for the UI and PostgreSQL
for persistence.

- [Technical setup](#technical-setup)
- [Application workflow](#application-workflow)

---

## Technical setup

OpenScrum is published as a Docker image on Docker Hub (`thomasboigner/openscrum`) and needs a PostgreSQL database.
The easiest way to run both is Docker Compose. Save the following as `docker-compose.yml`:

```yaml
services:
  openscrum:
    image: 'thomasboigner/openscrum:latest'
    container_name: openscrum
    depends_on:
      - openscrum-database
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://openscrum-database:5432/openscrum
      - SPRING_DATASOURCE_USERNAME=openscrum
      - SPRING_DATASOURCE_PASSWORD=<choose-a-password>
      - SPRING_JPA_HIBERNATE_DDL_AUTO=update
    networks:
      - openscrum

  openscrum-database:
    image: 'postgres:17.5'
    container_name: openscrum-database
    environment:
      - POSTGRES_USER=openscrum
      - POSTGRES_PASSWORD=<choose-a-password>
    volumes:
      - openscrum-database:/var/lib/postgresql/data
    networks:
      - openscrum

volumes:
  openscrum-database: {}

networks:
  openscrum:
    driver: bridge
```

Replace both `<choose-a-password>` placeholders with the same password, then start the stack:

```bash
docker compose up -d
```

OpenScrum is by default available at port 8080.

**First login:** on its first start, OpenScrum creates a manager account called `admin` with a random password and
writes the password to the log:

```bash
docker logs openscrum | grep "Generated admin user"
```

Log in with `admin` and that password, then register the other users from the Users page.

---

## Application workflow

### Two bounded contexts

OpenScrum is split into two parts that use the same words with different meanings:

- **Management:** the company view. Who has an account, and which projects exist with which people on them. Roles:
  **Manager** and **User**.
- **Scrum:** the team view. How a project is run day to day. Roles inside a project: **Product Owner**,
  **Scrum Master** and **Developer**.

A *project* in Management is a company project with a team assigned to it. A *project* in Scrum is the same project as
it is run with Scrum: it has a backlog, sprints and a configuration. The full glossary is in
[`src/docs/ubiquitous-language.md`](src/docs/ubiquitous-language.md).

The Scrum context never edits Management data. It keeps its own copy of the projects and team members, which is
updated by events from Management:

```
 Management                                  Scrum
 ──────────                                  ─────
 ProjectCreated            ───────────────▶  Scrum project created (sprint length 2 weeks),
                                             first sprint scheduled
 ProductOwnerAssigned /
 ScrumMasterAssigned /     ───────────────▶  Team member added to / removed from the project
 DeveloperAssigned (+ Unassigned)
 ProjectInformationChanged ───────────────▶  Project name updated
 UserInformationChanged    ───────────────▶  Team member's name updated
 ProjectCanceled           ───────────────▶  Project, its sprints and its backlog deleted
```

Events are processed asynchronously after the publishing transaction commits, so a change made in Management shows up
in Scrum a moment later.

### 1. Management: users and projects

1. **Log in.** Everyone starts at `/login`. The very first manager is the generated `admin` account.

   <img src="src/docs/specs/1-user-login/Login%20form%20-%20Laptop.png" alt="Login form" width="600">

2. **Manage users** (`/users`). A manager can register users, update them, promote a user to manager, demote a
   manager to user, and delete users. A manager can't demote their own account. A regular user can only update their
   own account. Each user in the list has inline buttons to promote or demote, edit and delete, and the round **+**
   button opens the registration form.

   <img src="src/docs/specs/3-remove-users/List%20users%20page%20-%20Laptop.png" alt="Users list" width="600">

   <img src="src/docs/specs/2-register-users/Register%20user%20page%20-%20Laptop.png" alt="Register user form" width="600">

   <img src="src/docs/specs/6-update-users/Update%20user%20page%20-%20Laptop.png" alt="Update user form" width="600">

3. **Create a project** (`/projects/create`, managers only). A project needs a name, a product owner and a scrum
   master. Developers are optional. One person can't hold more than one role in the same project.

   <img src="src/docs/specs/7-create-project/Project%20creation%20page%20-%20Laptop.png" alt="Create project form" width="600">

4. **Update or cancel a project** (managers only). Changing the team or the name sends the matching events to Scrum.
   Cancelling a project removes it from both contexts.

   <img src="src/docs/specs/8-update-project/Project%20update%20page%20-%20Laptop.png" alt="Update project form" width="600">

The project list (`/projects`) shows a manager all projects, and a regular user the projects they are part of.

### 2. Scrum: running a project

Opening a project shows its details: team, sprint length, product goal and definition of done. The sidebar switches
between **Details**, **Backlog** and **Sprints**, and the gear button opens the project configuration. From there, the
team works through the following areas. The permissions below
are checked on the server, not just hidden in the UI.

<img src="src/docs/specs/10-configure-project/Project%20details%20page%20-%20Laptop.png" alt="Project details page" width="600">

| Area                    | Product Owner                         | Scrum Master                               | Developer                     |
|-------------------------|---------------------------------------|--------------------------------------------|-------------------------------|
| Configure project       | Product goal                          | Sprint length (1–4 weeks), definition of done | –                          |
| Product backlog         | Define, update, delete items          | View                                       | View                          |
| Sprint planning         | –                                     | Plan the sprint                            | –                             |
| Kanban board            | View                                  | View                                       | Move items                    |
| Cancel sprint           | Cancel                                | –                                          | –                             |

#### Configuring the project

The scrum master sets the sprint length and the definition of done, and the product owner sets the product goal. Each
field is saved on its own.

<img src="src/docs/specs/10-configure-project/Configure%20project%20page%20-%20Laptop.png" alt="Configure project page" width="600">

#### The product backlog

The product owner fills the product backlog with items (a title and a description). The list can be searched and loads
more items as you scroll.

<img src="src/docs/specs/13-remove-product-backlog-items/List%20product%20backlog%20page%20-%20Laptop.png" alt="Product backlog list" width="600">

<img src="src/docs/specs/11-define-product-backlog/Define%20product%20backlog%20item%20page%20-%20Laptop.png" alt="Define product backlog item form" width="600">

<img src="src/docs/specs/12-update-product-backlog/Update%20product%20backlog%20item%20page%20-%20Laptop.png" alt="Update product backlog item form" width="600">

#### The sprint cycle

A project always has a current sprint. When a project is created, its first sprint is scheduled. Every time a sprint
ends (completed or cancelled), the next one is scheduled automatically, using the sprint length configured at that
moment.

```
          ┌──────────────────────────────────────────────────────────────┐
          ▼                                                              │
   ┌─────────────┐  scrum master plans   ┌─────────────┐  end date passed│
   │ NOT PLANNED │ ────────────────────▶ │ IN PROGRESS │ ───────────────▶ COMPLETED ─┤
   └─────────────┘                       └─────────────┘                             │
          │                                     │      product owner cancels         │
          │ end date passed                     └─────────────────────▶ CANCELLED ───┤
          └────────────────────────────────────────────────▶ COMPLETED ──────────────┘
                                                                        next sprint scheduled
```

1. **Sprint scheduled.** A new sprint ("Sprint N") is created with status *Not planned*. It starts today and lasts
   the project's sprint length.
2. **Sprint planning.** The scrum master opens the sprint's planning page, writes a **sprint goal** and selects the
   product backlog items to work on. Only items that are *In backlog* and belong to this project can be selected,
   and at least one is required. The selected items are copied into the **sprint backlog** and marked *Committed to
   sprint* in the product backlog. The sprint is now *In progress*.

   <img src="src/docs/specs/14-sprint-planning/Sprint%20planning%20page%20-%20Laptop.png" alt="Sprint planning page" width="600">
3. **Working the sprint (Kanban board).** Developers move sprint backlog items across *To do → In progress → Done*,
   and back if needed. Moving an item to *In progress* or *Done* assigns it to the developer who moved it, and moving
   it back to *To do* unassigns it. Moving an item to *Done* marks
   the product backlog item *Committed to sprint (done)*, and moving it back undoes that.

   <img src="src/docs/specs/16-mark-sprint-backlog-item-as-done/Sprint%20kanban%20board%20page%20-%20Laptop.png" alt="Sprint Kanban board" width="600">
4. **Ending the sprint.** A sprint ends in one of two ways:
   - **Completed:** once a week, OpenScrum completes every sprint whose end date has passed. This uses Spring
     Modulith's `WeekHasPassed` event, so completion happens at the next week boundary after the end date.
     Unplanned sprints whose time has run out are completed too, so the cycle keeps going.
   - **Cancelled:** the product owner can cancel a sprint that hasn't ended yet, from the sprint's details page.

   <img src="src/docs/specs/15-cancel-sprint/Sprint%20details%20page%20-%20Laptop.png" alt="Sprint details page" width="600">

   In both cases every product backlog item in the sprint is released. Items that were done become **Done**.
   Unfinished items go back to **In backlog**, ready for the next planning. Then the next sprint is scheduled.

#### Product backlog item lifecycle

```
 IN BACKLOG ──committed in planning──▶ COMMITTED TO SPRINT ──moved to Done──▶ COMMITTED TO SPRINT (DONE)
     ▲                                        │      ◀──moved back from Done──         │
     └────────── sprint ends, not done ───────┘                                        │
                                                                                    sprint ends ──▶ DONE
```

- Only the product owner defines, edits and deletes items. Items that are committed to a sprint can't be deleted, and
  the backlog page hides the edit and delete buttons for them.
- Deleting an item also removes it from any sprint backlog.

### Specifications

Each feature has a specification in [`src/docs/specs/`](src/docs/specs/) (for example `14-sprint-planning/spec.md`),
written as Given/When/Then acceptance criteria. The tests mirror these scenarios: each spec scenario has a matching
test, with the scenario quoted in a comment above it.
