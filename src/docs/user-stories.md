# User stories

## 1. As a user I want to log in, so that I can access the system.

### Test 1

Given an email and a password  
When I enter the email and password  
Then I want to be logged in

### Test 2

Given an email and a password  
When I enter the wrong email  
Then I receive an error

### Test 3

Given an email and a password  
When I enter the wrong password  
Then I receive an error

## 2. As a manager I want to register users, so that I can give them access to the system.

### Test 1

Given a manager, a username, a first name, a last name, an email address and a password  
When the manager enters the information  
Then the manager wants to register a user and see them in the user list

### Test 2

Given a user, a username, a first name, a last name, an email address and a password  
When the user enters the information  
Then he receives an error that he does not have the required permission

### Test 3

Given a manager, an already taken username, a first name, a last name, an email address and a password  
When the manager enters the information  
Then he receives an error that the username is already taken

### Test 4

Given a manager, a username, a first name, a last name, an already taken email address and a password  
When the manager enters the information  
Then he receives an error that the email address is already taken

### Test 5

Given a manager, a blank username, a blank first name, a blank last name, a blank email address and a blank password  
When the manager enters the information  
Then he receives an error that the information is invalid

## 3. As a manager I want to remove users, so that I can remove their access to the system.

## 4. As a user I want to update my user data, so that I can provide up-to-date data.

## 5. As a manager I want to create a project, so that I can track the work that is being done.

### Test 1

Given a manager, a project name, a product owner, a scrum master and developers  
When the manager enters the information into the create project form  
Then he wants to create a project and see it in the projects list

### Test 2

Given a user, a project name, a product owner, a scrum master and developers  
When the manager enters the information into the create project form  
Then he receives an error that he does not have the required permission

### Test 3

Given a manager, a project name, a scrum master and developers  
When the manager enters the information into the create project form  
Then he receives an error that the product owner is missing

### Test 4

Given a manager, a project name, a product owner and developers  
When the manager enters the information into the create project form  
Then he receives an error that the scrum master is missing

### Test 5

Given a manager, an already taken project name, a product owner, a scrum master and developers  
When the manager enters the information into the create project form  
Then he receives an error that the project name is already taken

### Test 6

Given a manager, a blank project name, a product owner, a scrum master and developers  
When the manager enters the information into the create project form  
Then he receives an error that the information is invalid

### Test 7

Given a manager, a project name, a user as product owner, scrum master and developer  
When the manager enters the information into the create project form  
Then he receives an error one user can not have multiple roles

## 6. As a manager I want to update a project, so that I can keep the information up to date.

## 7. As a manager I want to cancel a project, so that I can stop no longer needed projects.

## 8. As a product owner or scrum master I want to configure the project, so that I can define the way of working.

### Test 1

Given a scrum master, a project and a sprint length  
When the scrum master enters the information into the configure project form  
Then the sprint length should be set

### Test 2

Given a scrum master of another project, a project and a sprint length  
When the scrum master enters the information into the configure project form  
Then he receives an error that he is not the scrum master of this project

### Test 3

Given a developer, a project and a sprint length  
When the developer enters the information into the configure project form  
Then he receives an error that he has no permission to change the sprint length

### Test 4

Given a scrum master, a project and a sprint length smaller than 1  
When the scrum master enters the information into the configure project form  
Then he receives an error that the sprint length can not smaller than 1

### Test 5

Given a scrum master, a project and a sprint length bigger than 4  
When the scrum master enters the information into the configure project form  
Then he receives an error that the sprint length can not be bigger than 4

### Test 6

Given a product owner, a project and a product goal  
When the product owner enters the information into the configure project form  
Then the product goal should be set

### Test 7

Given a product owner of another project, a project and a product goal  
When the product owner enters the information into the configure project form  
Then he receives an error that he is not the product owner of this project

### Test 8

Given a developer, a project and a product goal  
When the developer enters the information into the configure project form  
Then he receives an error that he has no permission to change the product goal

### Test 9

Given a product owner, a project and a blank product goal  
When the product owner enters the information into the configure project form  
Then the product goal should be null

### Test 10

Given a scrum master, a project and a definition of done  
When the scrum master enters the information into the configure project form  
Then the definition of done should be set

### Test 11

Given a scrum master of another project, a project and a definition of done  
When the scrum master enters the information into the configure project form  
Then he receives an error that he is not the scrum master of this project

### Test 12

Given a developer, a project and a definition of done  
When the developer enters the information into the configure project form  
Then he receives an error that he has no permission to change the definition of done

### Test 13

Given a scrum master, a project and a blank definition of done  
When the scrum master enters the information into the configure project form  
Then the definition of done should be null

## 9. As a product owner I want to define the product backlog, so that I can list possible improvements.

### Test 1

Given a product owner, a title and a description  
When the product owner enters the information into the define product backlog item form  
Then the product backlog item should be created

### Test 2

Given a product owner of another project, a title and a description  
When the product owner enters the information into the define product backlog item form  
Then he receives an error that he is not the product owner of this project

### Test 3

Given a developer, a title and a description  
When the developer enters the information into the define product backlog item form  
Then he receives an error that he has no permission to create a product backlog item

### Test 4

Given a scrum master, a title and a description  
When the scrum master enters the information into the define product backlog item form  
Then he receives an error that he has no permission to create a product backlog item

### Test 5

Given a product owner, a blank title and a description  
When the product owner enters the information into the define product backlog item form  
Then he should receive an error that the title must not be blank

### Test 6

Given a product owner, a title and a blank description  
When the product owner enters the information into the define product backlog item form  
Then he should receive an error that the description must not be blank

## 10. As a product owner I want to update the product backlog, so that I can keep the information up to date.

## 11. As a product owner I want to remove items from the product backlog, so that I can keep the product backlog organized.

## 12. As a scrum master I want to hold a sprint planning, so that I can organize a sprint.

### Test 1

Given a scrum master, a sprint, a sprint goal and product backlog items  
When the scrum master enters the information into the plan sprint form  
Then the sprint goal should be set and the product backlog items get commited to the sprint

### Test 2

Given a scrum master of another project, a sprint, a sprint goal and product backlog items  
When the scrum master enters the information into the plan sprint form  
Then he receives an error that he is not the product owner of this project

### Test 3

Given a product owner, a sprint, a sprint goal and product backlog items  
When the scrum master enters the information into the plan sprint form  
Then he receives an error that he has no permission to plan the sprint

### Test 4

Given a developer, a sprint, a sprint goal and product backlog items  
When the scrum master enters the information into the plan sprint form  
Then he receives an error that he has no permission to plan the sprint

### Test 5

Given a scrum master, a sprint with sprint status that is not "not planned", a sprint goal and product backlog items  
When the scrum master enters the information into the plan sprint form  
Then he receives an error that the sprint can not be planned

### Test 6

Given a scrum master, a sprint, a sprint goal and product backlog items do not have status "in backlog"
When the scrum master enters the information into the plan sprint form  
Then he receives an error he can not commit already finished backlog items to a sprint

### Test 7

Given a scrum master, a sprint, a sprint goal and product backlog items of another project
When the scrum master enters the information into the plan sprint form  
Then he receives an error he can not commit product backlog items of another project

### Test 8

Given a scrum master, a sprint, a blank sprint goal and product backlog items  
When the scrum master enters the information into the plan sprint form  
Then he receives an error that the sprint goal can not be blank

### Test 9

Given a scrum master, a sprint and a sprint goal and no product backlog items  
When the scrum master enters the information into the plan sprint form  
Then the scrum master should receive an error that the sprint backlog can not be empty

## 13. As a product owner I want to cancel a sprint, so that I can stop a sprint that will not produce any value.

## 14. As a developer I want to mark a sprint backlog item as done, so that I can track the progress of the sprint.

### Test 1

Given a developer, a sprint with status "in progress" and a sprint backlog item with status "To-Do"  
When the sprint backlog item gets moved right  
Then the status should be "In progress" and the developer should be assigned  

### Test 2

Given a developer, a sprint with status "in progress" and a sprint backlog item with status "In progress"  
When the sprint backlog item gets moved right  
Then the status should be "Done" and the developer should be assigned  

### Test 3

Given a developer, a sprint with status "in progress" and a sprint backlog item with status "Done"  
When the sprint backlog item gets moved right  
Then the status should be "Done"  

### Test 4

Given a developer, a sprint with status "in progress" and a sprint backlog item with status "Done"  
When the sprint backlog item gets moved left  
Then the status should be "In progress" and the developer should be assigned  

### Test 5

Given a developer, a sprint with status "in progress" and a sprint backlog item with status "In progress"  
When the sprint backlog item gets moved left  
Then the status should be "To-Do" and no developer should be assigned  

### Test 6

Given a developer, a sprint with status "in progress" and a sprint backlog item with status "To-Do"  
When the sprint backlog item gets moved left  
Then the status should be "To-Do"

### Test 7

Given a developer of another project, a sprint with status "in progress" and a sprint backlog item  
When the sprint backlog item gets moved  
Then he receives an error that he is not a developer of this project

### Test 8

Given a developer, a sprint with status other than "in progress" and a sprint backlog item  
When the sprint backlog item gets moved  
Then he receives an error that sprint backlog items can only be moved when the sprint status is "In progress"

### Test 9

Given a developer, a sprint with status "in progress" and no sprint backlog item  
When the sprint backlog item gets moved  
Then he receives an error that the sprint backlog item can not be found