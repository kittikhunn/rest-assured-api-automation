package utils;

import io.restassured.response.Response;
import models.UserRequest;
import models.UserResponse;
import org.testng.Assert;

public class AssertionHelper {

    public static void assertUserMatches(UserResponse actual, UserRequest expected) {
//        UserResponse actual = response.as(UserResponse.class);

        Assert.assertEquals(actual.getName(), expected.getName());
        Assert.assertEquals(actual.getEmail(), expected.getEmail());
        Assert.assertEquals(actual.getGender(), expected.getGender());
        Assert.assertEquals(actual.getStatus(), expected.getStatus());
    }

    private AssertionHelper() {
    }
}