# 12. As a product owner I want to remove items from the product backlog, so that I can keep the product backlog organized.

## Test 1

Given a product owner and a product backlog item that is not committed to a sprint
When the product owner clicks the delete product backlog item button
Then the product backlog item should be deleted

## Test 2

Given a product owner of another project and a product backlog item that is not commited to a sprint
When the product owner clicks the delete product backlog item button
Then he receives an error that he is not the product owner of this project

## Test 3

Given a scrum master and a product backlog item that is not committed to a sprint
When the scrum master clicks the delete product backlog item button
Then he receives an error that he has no permission to delete the product backlog item

## Test 4

Given a developer and a product backlog item that is not committed to a sprint
When the developer clicks the delete product backlog item button
Then he receives an error that he has no permission to delete the product backlog item

## Test 5

Given a product owner and a product backlog item that is committed to a sprint
When the product owner clicks the delete product backlog item button
Then the product backlog item must not be commited to a sprint