# 12. As a product owner I want to update the product backlog, so that I can keep the information up to date.

## Test 1

Given a product owner, a product backlog item, a title and a description  
When the product owner enters the information into the update product backlog item form of the item  
Then the product backlog item should be updated

## Test 2

Given a product owner of another project, a product backlog item, a title and a description  
When the product owner enters the information into the update product backlog item form of the item  
Then he receives an error that he is not the product owner of this project

## Test 3

Given a developer, a product backlog item, a title and a description  
When the developer enters the information into the update product backlog item form of the item  
Then he receives an error that he has no permission to update a product backlog item

## Test 4

Given a scrum master, a product backlog item, a title and a description  
When the scrum master enters the information into the update product backlog item form of the item  
Then he receives an error that he has no permission to update a product backlog item

## Test 5

Given a product owner, no backlog item, a title and a description  
When the product owner enters the information into the update product backlog item form of the item  
Then he receives an error that the product backlog item does not exist

## Test 6

Given a product owner, a product backlog item, a blank title and a description  
When the product owner enters the information into the update product backlog item form of the item  
Then he receives an error that the title must not be blank

## Test 7

Given a product owner, a product backlog item, a title and a blank description  
When the product owner enters the information into the update product backlog item form of the item  
Then he receives an error that the description must not be blank