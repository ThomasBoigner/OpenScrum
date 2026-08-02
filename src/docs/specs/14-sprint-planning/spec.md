# 14. As a scrum master I want to hold a sprint planning, so that I can organize a sprint.

## Test 1

Given a scrum master, a sprint, a sprint goal and product backlog items  
When the scrum master enters the information into the plan sprint form  
Then the sprint goal should be set and the product backlog items get commited to the sprint

## Test 2

Given a scrum master of another project, a sprint, a sprint goal and product backlog items  
When the scrum master enters the information into the plan sprint form  
Then he receives an error that he is not the product owner of this project

## Test 3

Given a product owner, a sprint, a sprint goal and product backlog items  
When the scrum master enters the information into the plan sprint form  
Then he receives an error that he has no permission to plan the sprint

## Test 4

Given a developer, a sprint, a sprint goal and product backlog items  
When the scrum master enters the information into the plan sprint form  
Then he receives an error that he has no permission to plan the sprint

## Test 5

Given a scrum master, a sprint with sprint status that is not "not planned", a sprint goal and product backlog items  
When the scrum master enters the information into the plan sprint form  
Then he receives an error that the sprint can not be planned

## Test 6

Given a scrum master, a sprint, a sprint goal and product backlog items do not have status "in backlog"
When the scrum master enters the information into the plan sprint form  
Then he receives an error he can not commit already finished backlog items to a sprint

## Test 7

Given a scrum master, a sprint, a sprint goal and product backlog items of another project
When the scrum master enters the information into the plan sprint form  
Then he receives an error he can not commit product backlog items of another project

## Test 8

Given a scrum master, a sprint, a blank sprint goal and product backlog items  
When the scrum master enters the information into the plan sprint form  
Then he receives an error that the sprint goal can not be blank

## Test 9

Given a scrum master, a sprint and a sprint goal and no product backlog items  
When the scrum master enters the information into the plan sprint form  
Then the scrum master should receive an error that the sprint backlog can not be empty
