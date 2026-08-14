package data;

import org.testng.annotations.DataProvider;

public class UserDataProvider {

    @DataProvider(name = "invalidUserIds")
    public static Object[][] invalidUserIds() {
        return new Object[][]{
                {-1},
                {0},
                {1212312121}
        };
    }
}
