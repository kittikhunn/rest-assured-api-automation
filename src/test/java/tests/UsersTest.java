package tests;

import clients.UserClient;
import io.restassured.response.Response;
import models.UserRequest;
import models.UserResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.AssertionHelper;
import utils.TestDataGenerator;

import static org.testng.Assert.assertEquals;

public class UsersTest {

    @BeforeClass

    @Test
    public void getUsers() {
        UserClient userClient = new UserClient();
        userClient.getUsers()
                  .then()
                  .statusCode(200);
    }

    @Test
    public void getUserById() {
        UserClient userClient = new UserClient();
        Response usersResponse = userClient.getUsers();
        int userId = usersResponse.jsonPath()
                                  .getInt("[0].id");
        UserResponse userResponse = userClient.getUser(userId)
                                              .then()
                                              .statusCode(200)
                                              .extract()
                                              .as(UserResponse.class);
        assertEquals(userResponse.getId(), userId);
    }

    @Test
    public void createUser() {
        UserClient userClient = new UserClient();

        UserRequest userRequest = TestDataGenerator.generateUserRequest();

        UserResponse userResponse = userClient.createUser(userRequest)
                                              .then()
                                              .statusCode(201)
                                              .extract()
                                              .as(UserResponse.class);
        AssertionHelper.assertUserMatches(userResponse, userRequest);
    }

    @Test
    public void createUserWithoutName() {
        UserClient userClient = new UserClient();

        UserRequest userRequest = TestDataGenerator.generateUserRequest();
        userRequest.setName("");

        userClient.createUser(userRequest)
                  .then()
                  .statusCode(422);
    }

    @Test
    public void createUserWithoutEmail() {
        UserClient userClient = new UserClient();

        UserRequest userRequest = TestDataGenerator.generateUserRequest();
        userRequest.setEmail("");
        userClient.createUser(userRequest)
                  .then()
                  .statusCode(422);
    }

    @Test
    public void createUserWithInvalidEmail() {
        UserClient userClient = new UserClient();

        UserRequest userRequest = TestDataGenerator.generateUserRequest();
        userRequest.setEmail("invalidEmail");
        userClient.createUser(userRequest)
                  .then()
                  .statusCode(422);
    }

    @Test
    public void createUserWithDuplicateEmail() {
        UserClient userClient = new UserClient();
        UserRequest userRequest = TestDataGenerator.generateUserRequest();

        userClient.createUser(userRequest)
                  .then()
                  .statusCode(201);

        userClient.createUser(userRequest)
                  .then()
                  .statusCode(422);
    }

    @Test
    public void updateUser() {
        UserClient userClient = new UserClient();
        UserRequest createRequest = TestDataGenerator.generateUserRequest();
        Response createResponse = userClient.createUser(createRequest)
                                            .then()
                                            .statusCode(201)
                                            .extract()
                                            .response();

        int useId = createResponse.jsonPath()
                                  .getInt("id");
        UserRequest updateRequest = TestDataGenerator.generateUserRequest();

        Response updateResponse = userClient.updateUser(useId, updateRequest);

        models.UserResponse userResponse = updateResponse.then()
                                                         .statusCode(200)
                                                         .extract()
                                                         .as(UserResponse.class);

        AssertionHelper.assertUserMatches(userResponse, updateRequest);
    }

    @Test
    public void deleteUser() {
        UserClient userClient = new UserClient();

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
        UserClient userClient = new UserClient();

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
                  .statusCode(404);
    }

    @Test
    public void getNonExistentUser() {
        UserClient userClient = new UserClient();

        int nonExistentId = 1212312121;

        userClient.getUser(nonExistentId)
                  .then()
                  .statusCode(404);
    }

    @Test
    public void updateNonExistentUser() {
        UserClient userClient = new UserClient();

        UserRequest request = TestDataGenerator.generateUserRequest();

        userClient.updateUser(1212312121, request)
                  .then()
                  .statusCode(404);
    }

    @Test
    public void createUserWithoutAuth_() {
        UserClient userClient = new UserClient();

        UserRequest request =
                TestDataGenerator.generateUserRequest();

        userClient.createUserWithoutAuth(request)
                  .then()
                  .statusCode(401);
    }
}