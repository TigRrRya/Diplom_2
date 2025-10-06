import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.models.user.User;
import stellarburgers.models.user.UserLogin;
import stellarburgers.steps.StepsUser;
import utils.UserGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserLoginTest {
    private StepsUser stepsUser;
    private User registeredUser;
    private String accessToken = null;

    @Before
    public void steUp() {
        stepsUser = new StepsUser();
        registeredUser = UserGenerator.getValidUser();

        ValidatableResponse response = stepsUser.registerUser(registeredUser);


        response.statusCode(SC_OK);


        if (response.extract().path("success")) {
            accessToken = response.extract().path("accessToken");
        }
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            stepsUser.deleteUser(accessToken)
                    .statusCode(SC_ACCEPTED)
                    .body("message", equalTo("User successfully removed"));
        }
    }

    @Test
    @DisplayName("Авторизация существующего пользователя")
    @Description("Проверяет успешный вход в систему с валидными учетными данными. Ожидается статус 200 OK и наличие токена.")
    public void loginUserTest() {
        UserLogin credential = UserGenerator.getCredentials(registeredUser);

        ValidatableResponse response = stepsUser.loginUser(credential);

        response.statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Авторизация с неверным логином и паролем")
    @Description("Проверяет попытку входа с учетными данными несуществующего пользователя. Ожидается ошибка 401 Unauthorized.")
    public void loginFakeUserTest() {
        UserLogin fakeCredentials = UserGenerator.getInvalidCredentials();

        ValidatableResponse response = stepsUser.loginUser(fakeCredentials);

        response.statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация с верным логином, но неверным паролем")
    @Description("Проверяет попытку входа существующего пользователя с неправильным паролем. Ожидается ошибка 401 Unauthorized.")
    public void loginFakePasswordUserTest() {
        UserLogin invalidCredentials = UserGenerator.getCredentialsWithFakePassword(registeredUser);

        ValidatableResponse response = stepsUser.loginUser(invalidCredentials);

        response.statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация без поля Email")
    @Description("Проверяет попытку входа с отсутствующим полем Email. Ожидается ошибка 401 Unauthorized.")
    public void loginNullEmailUserTest() {
        String validPassword = registeredUser.getPassword();

        UserLogin credentials = new UserLogin(null, validPassword);

        ValidatableResponse response = stepsUser.loginUser(credentials);

        response.statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация без поля Password")
    @Description("Проверяет попытку входа с отсутствующим полем Password. Ожидается ошибка 401 Unauthorized.")
    public void loginNullPasswordUserTest() {
        String validEmail = registeredUser.getEmail();

        UserLogin credentials = new UserLogin(validEmail, null);

        ValidatableResponse response = stepsUser.loginUser(credentials);

        response.statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}