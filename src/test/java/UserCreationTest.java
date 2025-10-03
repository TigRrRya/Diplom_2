import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.models.user.User;
import stellarburgers.steps.StepsUser;
import utils.UserGenerator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserCreationTest {
    private StepsUser stepsUser;
    private User validUser;
    private String accessToken = null;

    @Before
    public void steUp() {
        stepsUser = new StepsUser();
        validUser = UserGenerator.getValidUser();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            stepsUser.deleteUser(accessToken)
                    .statusCode(202)
                    .body("message", equalTo("User successfully removed"));
        }
    }


    @Test
    @DisplayName("Создание пользователя, статус 200 и токен в ответе")
    public void createUniqueUserTest() {
        ValidatableResponse response = stepsUser.registerUser(validUser);

        response.statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());

        accessToken = response.extract().path("accessToken");
    }

    @Test
    @DisplayName("Повторная регистрация существующего пользователя. Ошибка 403")
    public void createExistingUserFailTest() {
        ValidatableResponse firstResponse = stepsUser.registerUser(validUser);
        accessToken = firstResponse.extract().path("accessToken");

        ValidatableResponse secondResponse = stepsUser.registerUser(validUser);
        secondResponse.statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без Email")
    public void createUserNoEmailTest() {
        User userNoEmail = UserGenerator.getUserWithMissingEmail();

        ValidatableResponse response = stepsUser.registerUser(userNoEmail);

        response.statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без Name")
    public void createUserNoName() {
        User userNoName = UserGenerator.getUserWithMissingName();

        ValidatableResponse response = stepsUser.registerUser(userNoName);

        response.statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без Password")
    public void createUserNoPassword() {
        User userNoPassword = UserGenerator.getUserWithMissingPassword();

        ValidatableResponse response = stepsUser.registerUser(userNoPassword);

        response.statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }


}
