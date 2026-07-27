# 2. As a manager I want to register users, so that I can give them access to the system.

## Test 1

Given a manager, a username, a first name, a last name, an email address and a password  
When the manager enters the information  
Then the manager wants to register a user and see them in the user list

## Test 2

Given a user, a username, a first name, a last name, an email address and a password  
When the user enters the information  
Then he receives an error that he does not have the required permission

## Test 3

Given a manager, an already taken username, a first name, a last name, an email address and a password  
When the manager enters the information  
Then he receives an error that the username is already taken

## Test 4

Given a manager, a username, a first name, a last name, an already taken email address and a password  
When the manager enters the information  
Then he receives an error that the email address is already taken

## Test 5

Given a manager, a blank username, a blank first name, a blank last name, a blank email address and a blank password  
When the manager enters the information  
Then he receives an error that the information is invalid