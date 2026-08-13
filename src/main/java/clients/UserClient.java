package clients;

import config.RequestSpec;
import io.restassured.response.Response;
import models.UserRequest;
import models.UserResponse;

import static io.restassured.RestAssured.given;

public class UserClient {

    public Response getUser(int userId) {
        return given(RequestSpec.authenticated())
                .pathParam("id", userId)
                .when()
                .get("/users/{id}");
    }

    public Response getUsers() {
        return given(RequestSpec.authenticated())
                .when()
                .get("/users");
    }

    public Response createUser(UserRequest userRequest) {
        return given(RequestSpec.authenticated())
                        .body(userRequest)
                        .when()
                        .post("/users");
    }

    public Response createUserWithoutAuth(UserRequest userRequest) {
        return given(RequestSpec.unauthenticated())
                .body(userRequest)
                .when()
                .post("/users");
    }

    public Response updateUser(int userId, UserRequest userRequest) {
        return given(RequestSpec.authenticated())
                .pathParam("id", userId)
                .body(userRequest)
                .when()
                .put("/users/{id}", userId);
    }

    public Response deleteUser(int userId) {
        return given(RequestSpec.authenticated())
                .pathParam("id", userId)
                .when()
                .delete("/users/{id}");
    }
}

