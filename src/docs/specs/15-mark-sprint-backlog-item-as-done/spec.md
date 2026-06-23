# 15. As a developer I want to mark a sprint backlog item as done, so that I can track the progress of the sprint.

## Test 1

Given a developer, a sprint with status "in progress" and a sprint backlog item with status "To-Do"  
When the sprint backlog item gets moved right  
Then the status should be "In progress" and the developer should be assigned

## Test 2

Given a developer, a sprint with status "in progress" and a sprint backlog item with status "In progress"  
When the sprint backlog item gets moved right  
Then the status should be "Done" and the developer should be assigned

## Test 3

Given a developer, a sprint with status "in progress" and a sprint backlog item with status "Done"  
When the sprint backlog item gets moved right  
Then the status should be "Done"

## Test 4

Given a developer, a sprint with status "in progress" and a sprint backlog item with status "Done"  
When the sprint backlog item gets moved left  
Then the status should be "In progress" and the developer should be assigned

## Test 5

Given a developer, a sprint with status "in progress" and a sprint backlog item with status "In progress"  
When the sprint backlog item gets moved left  
Then the status should be "To-Do" and no developer should be assigned

## Test 6

Given a developer, a sprint with status "in progress" and a sprint backlog item with status "To-Do"  
When the sprint backlog item gets moved left  
Then the status should be "To-Do"

## Test 7

Given a developer of another project, a sprint with status "in progress" and a sprint backlog item  
When the sprint backlog item gets moved  
Then he receives an error that he is not a developer of this project

## Test 8

Given a developer, a sprint with status other than "in progress" and a sprint backlog item  
When the sprint backlog item gets moved  
Then he receives an error that sprint backlog items can only be moved when the sprint status is "In progress"

## Test 9

Given a developer, a sprint with status "in progress" and no sprint backlog item  
When the sprint backlog item gets moved  
Then he receives an error that the sprint backlog item can not be found