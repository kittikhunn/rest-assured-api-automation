package tests;

import clients.UserClient;
import data.UserDataProvider;
import io.restassured.response.Response;
import models.UserRequest;
import models.UserResponse;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.AssertionHelper;
import utils.TestDataGenerator;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;

public class UsersTest {

    private UserClient userClient;
    private int createdUserId;

    @BeforeClass
    public void setUp() {
        userClient = new UserClient();
    }

    @Test
    public void getUsers() {
        Response response = userClient.getUsers();

        response.then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].id", notNullValue())
                .body("[0].name", notNullValue())
                .body("[0].email", notNullValue())
                .body("[0].gender", notNullValue())
                .body("[0].status", notNullValue());
    }

    @Test
    public void getUserById() {
        Response usersResponse = userClient.getUsers();
        int userId = usersResponse.jsonPath()
                                  .getInt("[0].id");
        UserResponse userResponse = userClient.getUser(userId)
                                              .then()
                                              .statusCode(200)
                                              .body(matchesJsonSchemaInClasspath("schemas/user.json"))
                                              .extract()
                                              .as(UserResponse.class);
        assertEquals(userResponse.getId(), userId);
    }

    @Test
    public void createUser() {
        UserRequest userRequest = TestDataGenerator.generateUserRequest();

        UserResponse userResponse = userClient.createUser(userRequest)
                                              .then()
                                              .statusCode(201)
                                              .body(matchesJsonSchemaInClasspath("schemas/user.json"))
                                              .extract()
                                              .as(UserResponse.class);

        createdUserId = userResponse.getId();

        AssertionHelper.assertUserMatches(userResponse, userRequest);
    }

    @Test
    public void createUserWithoutName() {
        UserRequest userRequest = TestDataGenerator.generateUserRequest();
        userRequest.setName("");

        userClient.createUser(userRequest)
                  .then()
                  .statusCode(422)
                  .body(matchesJsonSchemaInClasspath("schemas/error.json"))
                  .body("[0].field", equalTo("name"))
                  .body("[0].message", equalTo("can't be blank"));
    }

    @Test
    public void createUserWithoutEmail() {
        UserRequest userRequest = TestDataGenerator.generateUserRequest();
        userRequest.setEmail(" ");
        userClient.createUser(userRequest)
                  .then()
                  .statusCode(422)
                  .body("[0].field", equalTo("email"))
                  .body("[0].message", equalTo("can't be blank"));
    }

    @Test
    public void createUserWithInvalidEmail() {
        UserRequest userRequest = TestDataGenerator.generateUserRequest();
        userRequest.setEmail("invalidEmail");
        userClient.createUser(userRequest)
                  .then()
                  .statusCode(422)
                  .body(matchesJsonSchemaInClasspath("schemas/error.json"))
                  .body("[0].field", equalTo("email"))
                  .body("[0].message", equalTo("is invalid"));
    }

    @Test
    public void createUserWithDuplicateEmail() {
        UserRequest userRequest = TestDataGenerator.generateUserRequest();

        createdUserId = userClient.createUser(userRequest)
                                  .then()
                                  .statusCode(201)
                                  .extract()
                                  .jsonPath()
                                  .getInt("id");

        userClient.createUser(userRequest)
                  .then()
                  .statusCode(422)
                  .body(matchesJsonSchemaInClasspath("schemas/error.json"))
                  .body("[0].field", equalTo("email"))
                  .body("[0].message", equalTo("has already been taken"));
    }

    @Test
    public void updateUser() {
        UserRequest createRequest = TestDataGenerator.generateUserRequest();
        Response createResponse = userClient.createUser(createRequest)
                                            .then()
                                            .statusCode(201)
                                            .extract()
                                            .response();

        createdUserId = createResponse.jsonPath()
                                      .getInt("id");
        UserRequest updateRequest = TestDataGenerator.generateUserRequest();

        Response updateResponse = userClient.updateUser(createdUserId, updateRequest);

        UserResponse userResponse = updateResponse.then()
                                                  .statusCode(200)
                                                  .body(matchesJsonSchemaInClasspath("schemas/user.json"))
                                                  .extract()
                                                  .as(UserResponse.class);

        AssertionHelper.assertUserMatches(userResponse, updateRequest);
    }

    @Test
    public void updateUserWithoutName() {
        UserRequest createRequest = TestDataGenerator.generateUserRequest();

        createdUserId = userClient.createUser(createRequest)
                                  .then()
                                  .statusCode(201)
                                  .extract()
                                  .jsonPath()
                                  .getInt("id");

        UserRequest updateRequest = TestDataGenerator.generateUserRequest();
        updateRequest.setName("");

        userClient.updateUser(createdUserId, updateRequest)
                  .then()
                  .statusCode(422)
                  .body(matchesJsonSchemaInClasspath("schemas/error.json"))
                  .body("[0].field", equalTo("name"))
                  .body("[0].message", equalTo("can't be blank"));
    }

    @Test
    public void deleteUser() {
        UserRequest request = TestDataGenerator.generateUserRequest();

        int userId = userClient.createUser(request)
                               .then()
                               .statusCode(201)
                               .extract()
                               .jsonPath()
                               .getInt("id");

        userClient.deleteUser(userId)
                  .then()
                  .statusCode(204);
    }

    @Test
    public void getDeletedUser() {
        UserRequest request = TestDataGenerator.generateUserRequest();
        int userId = userClient.createUser(request)
                               .then()
                               .statusCode(201)
                               .extract()
                               .jsonPath()
                               .getInt("id");

        userClient.deleteUser(userId)
                  .then()
                  .statusCode(204);

        userClient.getUser(userId)
                  .then()
                  .statusCode(404)
                  .body("message", equalTo("Resource not found"));
    }

    @Test(dataProvider = "invalidUserIds", dataProviderClass = UserDataProvider.class)
    public void getUserWithInvalidId(int userId) {
        userClient.getUser(userId)
                  .then()
                  .statusCode(404)
                  .body("message", equalTo("Resource not found"));
    }

    @Test
    public void updateNonExistentUser() {
        UserRequest request = TestDataGenerator.generateUserRequest();

        userClient.updateUser(1212312121, request)
                  .then()
                  .statusCode(404)
                  .body("message", equalTo("Resource not found"));
    }

    @Test
    public void createUserWithoutAuth_() {
        UserRequest request =
                TestDataGenerator.generateUserRequest();

        userClient.createUserWithoutAuth(request)
                  .then()
                  .statusCode(401)
                  .body("message", equalTo("Authentication failed"));
    }

    @AfterMethod
    public void tearDown() {
        if (createdUserId > 0) {
            userClient.deleteUser(createdUserId)
                      .then()
                      .statusCode(204);
            createdUserId = 0;
        }
    }
}