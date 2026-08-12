package config;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {

    private static final Dotenv dotenv = Dotenv.load();

    public static final String BASE_URL =
            "https://gorest.co.in/public/v2";

    public static final String TOKEN =
            dotenv.get("GOREST_API_TOKEN");

    static {
        if (TOKEN == null || TOKEN.isBlank()) {
            throw new IllegalStateException(
                    "GOREST_API_TOKEN is not configured"
            );
        }
    }

}
