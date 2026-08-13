package tests;

import clients.UserClient;
import io.restassured.response.Response;
import models.UserRequest;
import models.UserResponse;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.testng.Assert.assertEquals;

public class UsersTest {

    @Test
    public void getUsers() {
        UserClient userClient = new UserClient();
        userClient.getUsers().then().statusCode(200);
    }

    @Test
    public void getUserById() {
        UserClient userClient = new UserClient();
        Response usersResponse = userClient.getUsers();
        int userId = usersResponse.jsonPath()
                                  .getInt("[0].id");
        System.out.println("User id: " + userId);
        Response userResponse = userClient.getUser(userId);
        assertEquals(userResponse.jsonPath()
                                 .getInt("id"), userId);
    }

    @Test
    public void createUser() {
        UserClient userClient = new UserClient();

        String uniqueId = UUID.randomUUID()
                              .toString()
                              .substring(0, 8);

        UserRequest userRequest = new UserRequest(
                "Test User " + uniqueId,
                "test_" + uniqueId + "@example.com",
                "male",
                "active"
        );
        UserResponse userResponse = userClient.createUser(userRequest)
                                              .then()
                                              .statusCode(201)
                                              .extract()
                                              .as(UserResponse.class);
        assertEquals(userResponse.getName(), userRequest.getName());
        assertEquals(userResponse.getEmail(), userRequest.getEmail());
        assertEquals(userResponse.getGender(), userRequest.getGender());
        assertEquals(userResponse.getStatus(), userRequest.getStatus());
    }

    @Test
    public void createUserWithoutName() {
        UserClient userClient = new UserClient();
        String uniqueId = UUID.randomUUID()
                              .toString()
                              .substring(0, 8);

        UserRequest userRequest = new UserRequest(
                "",
                "test_" + uniqueId + "@example.com",
                "male",
                "active"
        );

        userClient.createUser(userRequest).then().statusCode(422);


    }
}