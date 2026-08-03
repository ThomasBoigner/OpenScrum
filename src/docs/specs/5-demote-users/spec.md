# 5. As a manager I want to demote managers to users, so that I can manage who can manage projects.

## Test 1

Given a manager and a manager  
When the manager clicks on the demote user button  
Then the user should have the role of user

## Test 2

Given a user and a manager  
When the user clicks the demote user button  
Then he receives an error that he has no permission to demote users