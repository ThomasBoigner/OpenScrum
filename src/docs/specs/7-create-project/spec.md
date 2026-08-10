# 7. As a manager I want to create a project, so that I can track the work that is being done.

## Test 1 - Management context

Given a manager, a project name, a product owner, a scrum master and developers  
When the manager enters the information into the create project form  
Then he wants to create a project and see it in the projects list

## Test 2 - Management context

Given a user, a project name, a product owner, a scrum master and developers  
When the manager enters the information into the create project form  
Then he receives an error that he does not have the required permission

## Test 3 - Management context

Given a manager, a project name, a scrum master and developers  
When the manager enters the information into the create project form  
Then he receives an error that the product owner is missing

## Test 4 - Management context

Given a manager, a project name, a product owner and developers  
When the manager enters the information into the create project form  
Then he receives an error that the scrum master is missing

## Test 5 - Management context

Given a manager, an already taken project name, a product owner, a scrum master and developers  
When the manager enters the information into the create project form  
Then he receives an error that the project name is already taken

## Test 6 - Management context

Given a manager, a blank project name, a product owner, a scrum master and developers  
When the manager enters the information into the create project form  
Then he receives an error that the information is invalid

## Test 7 - Management context

Given a manager, a project name, a user as product owner, scrum master and developer  
When the manager enters the information into the create project form  
Then he receives an error one user can not have multiple roles

## Test 8 - Scrum context

Given a project and a ProductOwnerAssigned event  
When the ProductOwnerAssigned event is received  
Then the product owner should be added to the project

## Test 9 - Scrum context

Given a project and a ScrumMasterAssigned event  
When the ScrumMasterAssigned event is received  
Then the scrum master should be added to the project

## Test 10 - Scrum context

Given a project and a DeveloperAssigned event  
When the DeveloperAssigned event is received  
Then the developer should be added to the project