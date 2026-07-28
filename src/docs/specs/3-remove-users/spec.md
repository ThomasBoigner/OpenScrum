# 3. As a manager I want to remove users, so that I can remove their access to the system.

## Test 1

Given a manager and a user that is not assigned to a project  
When the manager clicks the delete user button  
Then the user should be deleted

## Test 2

Given a user and a user that is not assigned to a project  
When the user clicks the delete user button  
Then he receives an error that he has no permission to delete users

## Test 3

Given a manager and a user that is assigned to a project  
When the manager clicks the delete user button  
Then he receives an error that the user must not be assigned to a project in order to be deleted

## Test 4

Given a manager  
When the manager clicks the delete user button for his user  
Then he receives an error that he can not delete his own account
