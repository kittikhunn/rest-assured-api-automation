# REST Assured API Automation

API automation test suite built with Java, REST Assured, TestNG, and Maven for testing the GoREST Users API.

The main goal of this project is to test the GoREST Users API and cover the main user operations, including creating, retrieving, updating, and deleting users.

I also included negative test cases for validation and authentication to make sure the API handles invalid requests correctly.
## How to Run the Tests

### Prerequisites

Make sure the following are installed:

- Java 17+
- Maven 3.8+
- Git

Verify the installations:

    java -version
    mvn -version

### Clone the Repository

    git clone https://github.com/kittikhunn/rest-assured-api-automation.git
    cd rest-assured-api-automation

### Add Your GoRest Token
    
    GOREST_API_TOKEN=your_token_here

You can use you token as GOREST_API_TOKEN by 
 - create new .env file 
 - use .env.example in my repository and remove .example

### Run All Tests

    mvn test

Maven will compile the project and execute the TestNG test suite.

### Run a Specific Test Class

    mvn -Dtest=UsersTest test

### Run a Specific Test Method

    mvn -Dtest=UsersTest#createUser test

# Architecture

I separated the project into a few different layers so the test cases are easier to read and maintain.

The basic flow is:

    Test
      ↓
    UserClient
      ↓
    RequestSpec
      ↓
    API
      ↓
    Response
      ↓
    Assertions / Schema Validation

### Config

The config package contains the common API configuration.

`Config.java` contains the base configuration. `RequestSpec.java` is used to create reusable REST Assured request specifications.

I use request specifications so I don't have to repeat things below in every test:

- Base URI
- Authentication
- Content type
- Logging

I also configured logging so the Authorization header is not exposed when request/response information is logged.

### Clients

`UserClient.java` contains the actual API calls.

For example:

- Get users
- Get user by ID
- Create user
- Update user
- Delete user

I decided to keep the API calls outside the test class because I want the test to focus on what I am testing rather than how the request is built.

### Models

I use two models:

- `UserRequest`
- `UserResponse`

`UserRequest` is used when sending data to the API, and `UserResponse` is used when reading the response.

This makes the code easier to read and avoids having to work with raw JSON.

### Test Data

`UserDataProvider.java` contains reusable test data for different scenarios.

I use TestNG DataProvider so that the same test logic can be reused with different input values.

For data that needs to be unique, such as email addresses, I use `TestDataGenerator` to generate new values.

### Utils

I have a couple of reusable utility classes.

`AssertionHelper` contains common assertions for comparing the returned user with the data that was sent.

`TestDataGenerator` is used to generate unique test data.

### JSON Schema

I also added JSON Schema validation for the user response.

The schema is stored under:

    src/test/resources/schemas/user.json

This gives me another level of validation instead of only checking individual fields.

## Why I Designed It This Way

I wanted the test cases to be as simple and readable as possible.

For example, I don't want every test to contain:

- Request specification
- Authentication
- Endpoint
- Request body creation
- The same assertions

Instead, these responsibilities are separated into different classes.

This also makes it easier to update the framework later. If the API configuration changes, I can update it in one place instead of changing every test.

# Test Scenarios

I focused on the main CRUD operations and some important negative scenarios.
I grouped the tests by API operation so it is easier to see what is covered.

    The current test suite contains 18 test methods:

    GET USERS
    - `getUsers`
    - `getUserById`
    - `getUserWithInvalidId`
    CREATE USER
    - `createUser`
    - `createUserWithoutName`
    - `createUserWithoutEmail`
    - `createUserWithInvalidEmail`
    - `createUserWithDuplicateEmail`
    - `createUserWithInvalidGender`
    - `createUserWithInvalidStatus`
    - `createUserWithoutAuth`
    UPDATE USER
    - `updateUser`
    - `updateUserWithoutName`
    - `updateNonExistentUser`
    - `updateUserWithoutAuth`
    DELETE USER
    - `deleteUser`
    - `getDeletedUser`
    - `deleteUserWithoutAuth`

## GET Users

### Get Users

Tests that the API can return a list of users.

I check:

- Status code is `200`
- Response contains at least one user
- ID is present
- Name is present
- Email is present
- Gender is present
- Status is present

### Get User by ID

First, I get an existing user ID and then use that ID to request the user.

I check:

- Status code is `200`
- Returned ID matches the requested ID
- Response matches the user JSON schema

### Get User with Invalid ID

Tests that requesting a user that does not exist returns the expected error.

I check:

- Status code is `404`
- Error message is `Resource not found`

I use a TestNG DataProvider for multiple invalid user IDs.

## CREATE User

### Create User

Tests the normal user creation flow.

I check:

- Status code is `201`
- Response matches the user schema
- User ID is generated
- Returned user data matches the request

The created user ID is saved so it can be cleaned up after the test.

### Create User Without Name

Tests that the API rejects a user when the name is empty.

I check:

- Status code is `422`
- Error schema
- Error field is `name`
- Error message is `can't be blank`

### Create User Without Email

Tests that the API rejects a user when the email is empty.

I check:

- Status code is `422`
- Error field is `email`
- Error message is `can't be blank`

### Create User with Invalid Email

Tests that the API rejects an invalid email format.

Example:

    invalidEmail

I check:

- Status code is `422`
- Error schema
- Error field is `email`
- Error message is `is invalid`

### Create User with Duplicate Email

Tests that the API does not allow the same email to be used twice.

The test:

1. Creates a user
2. Uses the same request again
3. Expects the second request to fail

I check:

- First request returns `201`
- Second request returns `422`
- Error field is `email`
- Error message is `has already been taken`

### Create User with Invalid Gender

Tests server-side validation for the gender field.

I send:

    invalid

I check:

- Status code is `422`
- Error schema
- Error field is `gender`
- API returns the expected validation message

### Create User with Invalid Status

Tests server-side validation for the status field.

I send:

    invalid

I check:

- Status code is `422`
- Error schema
- Error field is `status`
- API returns the expected validation message

### Create User Without Authentication

Tests that a user cannot be created without authentication.

I check:

- Status code is `401`
- Error message is `Authentication failed`

## UPDATE User

### Update User

Tests the normal update flow.

The test first creates a user and then updates it.

I check:

- User is created successfully
- Update returns `200`
- Response matches the user schema
- Returned user data matches the update request

### Update User Without Name

Tests that the API validates the name when updating an existing user.

I send an empty name and check:

- Status code is `422`
- Error schema
- Error field is `name`
- Error message is `can't be blank`


### Update Non-Existent User

Tests that updating a user that does not exist returns the expected error.

I check:

- Status code is `404`
- Error message is `Resource not found`

### Update User Without Authentication

Tests the update endpoint without authentication.

I check that the request is not allowed to successfully update the user.

The current API response is:

- Status code `404`
- Message `Resource not found`

I kept this test because it verifies the behavior of the unauthenticated endpoint in the current API.

## DELETE User

### Delete User

Tests the normal delete flow.

The test:

1. Creates a user
2. Gets the created user ID
3. Deletes the user

I check:

- User creation returns `201`
- Delete returns `204`

### Get Deleted User

Tests that a user cannot be retrieved after being deleted.

The test:

1. Creates a user
2. Deletes the user
3. Tries to get the same user again

I check:

- Delete returns `204`
- Getting the deleted user returns `404`
- Error message is `Resource not found`

I think this is useful because checking only the DELETE response does not prove that the resource was actually removed.

### Delete User Without Authentication

Tests the delete endpoint without authentication.

I check:

- Request does not successfully delete the user
- Current API response is `404`
- Error message is `Resource not found`

## Why I Chose These Scenarios

I wanted to cover more than just the happy path.

The tests cover:

- Basic CRUD operations
- Required field validation
- Invalid input
- Duplicate data
- Invalid email
- Invalid gender
- Invalid status
- Non-existent users
- Authentication
- Response validation
- JSON schema validation
- Delete verification

For me, checking only the status code is not enough.

For successful requests, I also check important response values.

For failed requests, I check the status code and the error response to make sure the API is failing for the expected reason.

For example, when creating a user with a duplicate email, I don't just check for `422`. I also check that the error is related to the `email` field and that the message says the email has already been taken.

## Test Data and Cleanup

I generate unique user data for create and update tests.

This is especially important for email because the API does not allow duplicate email addresses.

When a test creates a user that needs to remain available during the test, I store its ID in:

    createdUserId

The `@AfterMethod` cleanup then deletes the created user after the test.

This helps prevent test data from accumulating and makes the tests less dependent on previous test runs.

## Validation Approach

I use a few different levels of validation.

### 1. Status Code

First, I check whether the API returned the expected HTTP status code.

Examples:

- `200` for successful GET
- `201` for successful CREATE
- `204` for successful DELETE
- `404` for a resource that does not exist
- `422` for validation errors
- `401` for authentication failures

### 2. Response Fields

For important responses, I check specific fields.

For example, when getting users:

- ID
- Name
- Email
- Gender
- Status

For error responses, I check:

- Field
- Error message


### 3. JSON Schema

I also validate the response against a JSON schema.

This helps catch unexpected changes in the response structure even if the individual assertions still pass.

## What I Would Do Differently With More Time

There are a few things I would improve if I had more time.

### 1. Make Cleanup More Consistent

Currently, some tests create a user and store the ID in `createdUserId`, while some tests create and delete the user directly inside the test.

I would make this more consistent by having every test that creates a user store the ID in `createdUserId`.

This would make the cleanup structure more predictable and reduce the chance of leaving test data behind when adding new tests in the future.


### 2. Improve Repeated Assertions

There are some assertions that are repeated across multiple tests.

For example, checking that the user response contains:

- ID
- Name
- Email
- Gender
- Status

I could move these common assertions into `AssertionHelper` instead of keeping them directly in the test.

The same applies to some of the error response assertions.

I believe this would make the test cases easier to read because the test would focus more on the actual test step and expected result, instead of having many individual assertion lines.

My goal is to make each test easy to understand just by looking at the test name and the main steps.


### 3. Improve `getUsers` Response Validation

Currently, `getUsers()` checks that the response contains at least one user and then validates the fields of the first user.

For example:

    .body("size()", greaterThan(0))
    .body("[0].id", notNullValue())
    .body("[0].name", notNullValue())
    .body("[0].email", notNullValue())
    .body("[0].gender", notNullValue())
    .body("[0].status", notNullValue())

This works, but it only validates the first user in the response.

I would improve this by adding a `users.json` schema and validating the whole response against it.

This would give better coverage and would also make the test less dependent on the assumption that the first user is representative of the whole response.

### 4. Reduce Repeated Create User Steps

Some update and delete tests need to create a user first before testing the actual operation.

For example:

    create user
        ↓
    get user ID
        ↓
    update/delete user

I would consider creating a small reusable method for this setup.

This would reduce repeated code while keeping the actual test steps focused on what is being tested.

For example, the update test could be easier to read as:

    create user
    update user
    verify response

instead of having the test contain all the setup details.


### 5. Add More Negative Test Coverage

I would add a few more simple validation cases, such as:

- Missing multiple required fields
- Empty status
- Empty gender
- More invalid email formats
- Updating a user with an invalid email
- Updating a user with an invalid status

I would focus on cases that are relevant to the API rather than trying to test every possible input.