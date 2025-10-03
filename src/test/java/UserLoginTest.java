import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.models.user.User;
import stellarburgers.models.user.UserLogin;
import stellarburgers.steps.StepsUser;
import utils.UserGenerator;

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
        accessToken = response.extract().path("accessToken");

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
    @DisplayName("Авторизация существующего пользователя. 200 ок и токен")
    public void loginUserTest() {
        UserLogin credential = UserGenerator.getCredentials(registeredUser);

        ValidatableResponse response = stepsUser.loginUser(credential);

        response.statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Авторизация с неверным логином и паролем. Ошибка 401")
    public void loginFakeUserTest() {
        UserLogin fakeCredentials = UserGenerator.getInvalidCredentials();

        ValidatableResponse response = stepsUser.loginUser(fakeCredentials);

        response.statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));

    }

    @Test
    @DisplayName("Авторизация с верным логином, но неверным паролем. Ошибка 401")
    public void loginFakePasswordUserTest() {
        UserLogin invalidCredentials = UserGenerator.getCredentialsWithFakePassword(registeredUser);

        ValidatableResponse response = stepsUser.loginUser(invalidCredentials);

        response.statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("(\"Авторизация без поля Email.Ошибка 401")
    public void loginNullEmailUserTest() {
        String validPassword = registeredUser.getPassword();

        UserLogin credentials = new UserLogin(null, validPassword);

        ValidatableResponse response = stepsUser.loginUser(credentials);

        response.statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));


    }

    @Test
    @DisplayName("Авторизация без поля Password должна вернуть ошибку 401")
    public void loginNullPasswordUserTest() {
        String validEmail = registeredUser.getEmail();

        UserLogin credentials = new UserLogin(validEmail, null);

        ValidatableResponse response = stepsUser.loginUser(credentials);

        response.statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}