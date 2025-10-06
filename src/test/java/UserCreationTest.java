import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.models.user.User;
import stellarburgers.steps.StepsUser;
import utils.UserGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserCreationTest {
    private StepsUser stepsUser;
    private User validUser;
    private ValidatableResponse registrationResponse = null;
    private String accessToken = null;

    @Before
    public void steUp() {
        stepsUser = new StepsUser();
        validUser = UserGenerator.getValidUser();
    }

    @After
    public void tearDown() {
        if (registrationResponse != null && accessToken == null) {
            try {
                String extractedToken = registrationResponse.extract().path("accessToken");
                if (extractedToken != null) {
                    accessToken = extractedToken;
                }
            } catch (Exception ignored) {

            }
        }

        if (accessToken != null) {
            stepsUser.deleteUser(accessToken)
                    .statusCode(SC_ACCEPTED)
                    .body("message", equalTo("User successfully removed"));
        }
    }


    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверяет успешную регистрацию пользователя с валидными данными. Ожидается статус 200 OK и наличие токена.")
    public void createUniqueUserTest() {
        registrationResponse = stepsUser.registerUser(validUser);

        registrationResponse.statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Повторная регистрация существующего пользователя")
    @Description("Проверяет попытку повторной регистрации с теми же данными. Ожидается ошибка 403 Forbidden.")
    public void createExistingUserFailTest() {
        ValidatableResponse firstResponse = stepsUser.registerUser(validUser);

        accessToken = firstResponse.extract().path("accessToken");

        ValidatableResponse secondResponse = stepsUser.registerUser(validUser);
        secondResponse.statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без Email")
    @Description("Проверяет невозможность регистрации, если не передан обязательный параметр Email. Ожидается ошибка 403 Forbidden.")
    public void createUserNoEmailTest() {
        User userNoEmail = UserGenerator.getUserWithMissingEmail();

        ValidatableResponse response = stepsUser.registerUser(userNoEmail);

        response.statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без Name")
    @Description("Проверяет невозможность регистрации, если не передан обязательный параметр Name. Ожидается ошибка 403 Forbidden.")
    public void createUserNoName() {
        User userNoName = UserGenerator.getUserWithMissingName();

        ValidatableResponse response = stepsUser.registerUser(userNoName);

        response.statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без Password")
    @Description("Проверяет невозможность регистрации, если не передан обязательный параметр Password. Ожидается ошибка 403 Forbidden.")
    public void createUserNoPassword() {
        User userNoPassword = UserGenerator.getUserWithMissingPassword();

        ValidatableResponse response = stepsUser.registerUser(userNoPassword);

        response.statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}