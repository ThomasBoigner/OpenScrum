# 8 As a manager I want to update a project, so that I can keep the information up to date.

## Test 1 - Management context

Given a manager, an existing project, a new project name, a new product owner, a new scrum master and new developers  
When the manager enters the information into the update project form  
Then the project information should be updated and ProductOwnerUnassigned, ProductOwnerAssigned, ScrumMasterUnassigned ScrumMasterAssigned, DeveloperUnassigned, DeveloperAssigned events should be published 

## Test 2 - Management context

Given a user, an existing project, a new project name, a new product owner, a new scrum master and new developers  
When the user enters the information into the update project form  
Then he receives an error that he does not have the required permission

## Test 3 - Management context

Given a manager, no existing project, a new project name, a new product owner, a new scrum master and new developers  
When the user enters the information into the update project form  
Then he receives an error that the project does not exist

## Test 4 - Management context

Given a manager, an existing project, a new project name, a new scrum master and new developers  
When the manager enters the information into the update project form  
Then he receives an error that the product owner is missing

## Test 5 - Management context

Given a manager, an existing project, a new project name, a new product owner and new developers  
When the manager enters the information into the update project form  
Then he receives an error that the scrum master is missing

## Test 6 - Management context

Given a manager, an existing project, a new already taken project name, a new product owner, a new scrum master and new developers  
When the manager enters the information into the update project form  
Then he receives an error that the project name is already taken

## Test 7 - Management context

Given a manager, an existing project, a blank project name, a new product owner, a new scrum master and new developers  
When the manager enters the information into the update project form  
Then he receives an error that the project name can not be blank

## Test 8 - Management context

Given a manager, an existing project, a new project name, a user as the new product owner, the new scrum master and a new developer  
When the manager enters the information into the update project form  
Then he receives an error one user can not have multiple roles

## Test 9 - Scrum context

Given a project with a product owner and a ProductOwnerUnassigned event  
When the ProductOwnerUnassigned event is received  
Then the product owner should be removed

## Test 10 - Scrum context

Given a project with a scrum master and a ScrumMasterUnassigned event  
When the ScrumMasterUnassigned event is received  
Then the scrum master should be removed

## Test 11 - Scrum context

Given a project with a developer and a DeveloperUnassigned event  
When the DeveloperUnassigned event is received  
Then the developer should be removed