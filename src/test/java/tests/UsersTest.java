package tests;

import clients.UserClient;
import io.restassured.response.Response;
import models.UserResponse;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class UsersTest {

    @Test
    public void getUsers() {
        UserClient userClient = new UserClient();
        userClient.getUsers();
    }

    @Test
    public void getUserById() {
        UserClient userClient = new UserClient();
        Response usersResponse = userClient.getUsers();
        int userId = usersResponse.jsonPath()
                             .getInt("[0].id");
        System.out.println("User id: " + userId);
        Response userResponse = userClient.getUser(userId);
        assertEquals(userResponse.jsonPath().getInt("id"),userId);
    }
}