# 14. As a product owner I want to cancel a sprint, so that I can stop a sprint that will not produce any value.

## Test 1

Given a product owner and a sprint with status "in progress" or "not planned"  
When the product owner clicks the cancel sprint button  
Then the sprint status should be updated to canceled, not finished sprint backlog items should have status
"in product backlog" and the next sprint should be scheduled

## Test 2

Given a product owner of another project and a sprint with status "in progress" or "not planned"    
When the product owner clicks the cancel sprint button
Then he receives an error that he is not the product owner of this project

## Test 3

Given a scrum master and a sprint with status "in progress" or "not planned"    
When the scrum master clicks the cancel sprint button
Then he receives an error that he has no permission to cancel the sprint

## Test 4

Given a developer and a sprint with status "in progress" or "not planned"    
When the developer clicks the cancel sprint button
Then he receives an error that he has no permission to cancel the sprint

## Test 5

Given a product owner and a sprint with status "done" or "canceled"  
When the product owner clicks the cancel sprint button
Then he receives an error that the sprint is already finished