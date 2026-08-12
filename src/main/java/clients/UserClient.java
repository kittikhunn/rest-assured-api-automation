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
                .get("/users/{id}")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public Response getUsers() {
        return given(RequestSpec.authenticated())
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public UserResponse createUser(UserRequest userRequest) {

        Response response =
                given(RequestSpec.authenticated())
                        .body(userRequest)
                        .when()
                        .post("/users")
                        .then()
                        .statusCode(201)
                        .extract()
                        .response();

        return response.as(UserResponse.class);
    }
}

