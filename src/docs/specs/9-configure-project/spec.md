# 9. As a product owner or scrum master I want to configure the project, so that I can define the way of working.

## Test 1

Given a scrum master, a project and a sprint length  
When the scrum master enters the information into the configure project form  
Then the sprint length should be set

## Test 2

Given a scrum master of another project, a project and a sprint length  
When the scrum master enters the information into the configure project form  
Then he receives an error that he is not the scrum master of this project

## Test 3

Given a developer, a project and a sprint length  
When the developer enters the information into the configure project form  
Then he receives an error that he has no permission to change the sprint length

## Test 4

Given a scrum master, a project and a sprint length smaller than 1  
When the scrum master enters the information into the configure project form  
Then he receives an error that the sprint length can not smaller than 1

## Test 5

Given a scrum master, a project and a sprint length bigger than 4  
When the scrum master enters the information into the configure project form  
Then he receives an error that the sprint length can not be bigger than 4

## Test 6

Given a product owner, a project and a product goal  
When the product owner enters the information into the configure project form  
Then the product goal should be set

## Test 7

Given a product owner of another project, a project and a product goal  
When the product owner enters the information into the configure project form  
Then he receives an error that he is not the product owner of this project

## Test 8

Given a developer, a project and a product goal  
When the developer enters the information into the configure project form  
Then he receives an error that he has no permission to change the product goal

## Test 9

Given a product owner, a project and a blank product goal  
When the product owner enters the information into the configure project form  
Then the product goal should be null

## Test 10

Given a scrum master, a project and a definition of done  
When the scrum master enters the information into the configure project form  
Then the definition of done should be set

## Test 11

Given a scrum master of another project, a project and a definition of done  
When the scrum master enters the information into the configure project form  
Then he receives an error that he is not the scrum master of this project

## Test 12

Given a developer, a project and a definition of done  
When the developer enters the information into the configure project form  
Then he receives an error that he has no permission to change the definition of done

## Test 13

Given a scrum master, a project and a blank definition of done  
When the scrum master enters the information into the configure project form  
Then the definition of done should be null