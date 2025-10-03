package stellarburgers.steps;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import static stellarburgers.api.ConstantsApiAndUrl.BASE_URL;

public abstract class BaseApi {
    protected static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .setContentType("application/json")
            .build();
}