package config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RequestSpec {

    private static final LogConfig LOG_CONFIG =
            LogConfig.logConfig()
                     .enableLoggingOfRequestAndResponseIfValidationFails()
                     .blacklistHeader("Authorization");

    public static RequestSpecification authenticated() {

        return new RequestSpecBuilder()
                .setBaseUri(Config.BASE_URL)
                .addHeader(
                        "Authorization",
                        "Bearer " + Config.TOKEN
                )
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(
                        io.restassured.config.RestAssuredConfig.config()
                                                               .logConfig(LOG_CONFIG)
                )
                .build();
    }

    public static RequestSpecification unauthenticated() {

        return new RequestSpecBuilder()
                .setBaseUri(Config.BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(
                        io.restassured.config.RestAssuredConfig.config()
                                                               .logConfig(LOG_CONFIG)
                )
                .build();
    }
}