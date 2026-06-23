# 10. As a product owner I want to define the product backlog, so that I can list possible improvements.

## Test 1

Given a product owner, a title and a description  
When the product owner enters the information into the define product backlog item form  
Then the product backlog item should be created

## Test 2

Given a product owner of another project, a title and a description  
When the product owner enters the information into the define product backlog item form  
Then he receives an error that he is not the product owner of this project

## Test 3

Given a developer, a title and a description  
When the developer enters the information into the define product backlog item form  
Then he receives an error that he has no permission to create a product backlog item

## Test 4

Given a scrum master, a title and a description  
When the scrum master enters the information into the define product backlog item form  
Then he receives an error that he has no permission to create a product backlog item

## Test 5

Given a product owner, a blank title and a description  
When the product owner enters the information into the define product backlog item form  
Then he should receive an error that the title must not be blank

## Test 6

Given a product owner, a title and a blank description  
When the product owner enters the information into the define product backlog item form  
Then he should receive an error that the description must not be blank
