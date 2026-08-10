# 9. As a manager I want to cancel a project, so that I can stop no longer needed projects.

## Test 1 - Management context

Given a manager and a project with a product owner, a scrum master and developers
When the manager clicks on the cancel project button
Then the project should be removed and ProjectCanceled, ProductOwnerUnassigned, ScrumMasterUnassigned and DeveloperUnassigned events should be published

## Test 2 - Management context

Given a user and a project with a product owner, a scrum master and developers
When the user clicks on the cancel project button
Then he receives an error that he has no permission to cancel projects

## Test 3 - scrum context

Given a project
When the ProjectCanceled event is received
Then the project should be removed
