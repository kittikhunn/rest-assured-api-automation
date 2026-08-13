package utils;

import models.UserRequest;
import java.util.UUID;

public class TestDataGenerator {

    public static UserRequest generateUserRequest() {

        String uniqueId = UUID.randomUUID()
                              .toString()
                              .substring(0, 8);

        return new UserRequest(
                "Test User " + uniqueId,
                "test_" + uniqueId + "@example.com",
                "male",
                "active"
        );
    }
}