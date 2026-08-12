package config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RequestSpec {

    public static RequestSpecification authenticated() {

        return new RequestSpecBuilder()
                .setBaseUri(Config.BASE_URL)
                .addHeader(
                        "Authorization",
                        "Bearer " + Config.TOKEN
                )
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();

    }
}