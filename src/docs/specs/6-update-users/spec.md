# 6. As a user I want to update my user data, so that I can provide up-to-date data.

## Test 1 - Management context

Given a manager and an existing user, a new username, a new first name, a new last name, a new email address and a new password  
When the manager enters the information into the update user form  
Then the user information should be updated and a UserInformationChanged event should be published

## Test 2 - Management context

Given an existing user, a new username, a new first name, a new last name, a new email address and a new password  
When the user enters the information into the update user form of his own user  
Then the user information should be updated and a UserInformationChanged event should be published

## Test 3 - Management context

Given an existing user, a new username, a new first name, a new last name, a new email address and a new password  
When the user enters the information into the update user form of another user  
Then he receives an error that he does not have the required permission to update other users

## Test 4 - Management context

Given a manager and no existing user, a new username, a new first name, a new last name, a new email address and a new password  
When the manager enters the information into the update user form  
Then he receives an error that the user does not exist

## Test 5 - Management context

Given a manager and an existing user, an already taken username, a new first name, a new last name, a new email address and a new password  
When the manager enters the information into the update user form  
Then he receives an error that the username is already taken

## Test 6 - Management context

Given a manager and an existing user, a new username, a new first name, a new last name, an already taken email address and a new password  
When the manager enters the information into the update user form  
Then he receives an error that the email address is already taken

## Test 7 - Management context

Given a manager and an existing user, the user's own current username, a new first name, a new last name, a new email address and a new password  
When the manager enters the information into the update user form  
Then the user information should be updated and a UserInformationChanged event should be published

## Test 8 - Management context

Given a manager and an existing user, a new username, a new first name, a new last name, the user's own current email address and a new password  
When the manager enters the information into the update user form  
Then the user information should be updated and a UserInformationChanged event should be published

## Test 9 - Management context

Given a manager and an existing user, a new username, a new first name, a new last name, a new email address that does not have the right format and a new password  
When the manager enters the information into the update user form  
Then he receives an error that the email address does not have the right format

## Test 10 - Management context

Given a manager and an existing user, a new blank username, a new blank first name, a new blank last name and a new blank email address  
When the manager enters the information into the update user form  
Then he receives an error that the information is invalid

## Test 11 - Management context

Given a manager and an existing user, a new username, a new first name, a new last name, a new email address and a blank password  
When the manager enters the information into the update user form  
Then the user information should be updated, the password should stay unchanged and a UserInformationChanged event should be published

## Test 12 - Scrum context

Given a teammember and a UserInformationChanged event  
When the UserInformationChanged event is received  
Then the teammember information should be updated
