# 5. As a manager I want to create a project, so that I can track the work that is being done.

## Test 1

Given a manager, a project name, a product owner, a scrum master and developers  
When the manager enters the information into the create project form  
Then he wants to create a project and see it in the projects list

## Test 2

Given a user, a project name, a product owner, a scrum master and developers  
When the manager enters the information into the create project form  
Then he receives an error that he does not have the required permission

## Test 3

Given a manager, a project name, a scrum master and developers  
When the manager enters the information into the create project form  
Then he receives an error that the product owner is missing

## Test 4

Given a manager, a project name, a product owner and developers  
When the manager enters the information into the create project form  
Then he receives an error that the scrum master is missing

## Test 5

Given a manager, an already taken project name, a product owner, a scrum master and developers  
When the manager enters the information into the create project form  
Then he receives an error that the project name is already taken

## Test 6

Given a manager, a blank project name, a product owner, a scrum master and developers  
When the manager enters the information into the create project form  
Then he receives an error that the information is invalid

## Test 7

Given a manager, a project name, a user as product owner, scrum master and developer  
When the manager enters the information into the create project form  
Then he receives an error one user can not have multiple roles