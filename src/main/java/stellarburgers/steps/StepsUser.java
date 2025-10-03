package stellarburgers.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import stellarburgers.models.user.User;
import stellarburgers.models.user.UserLogin;

import static io.restassured.RestAssured.given;
import static stellarburgers.api.ConstantsApiAndUrl.*;


public class StepsUser extends BaseApi {

    @Step("Регистрация (создание) пользователя")
    public ValidatableResponse registerUser(User user) {
        return given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post(REGISTER_USER)
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse loginUser(UserLogin userLogin) {
        return given()
                .spec(requestSpec)
                .body(userLogin)
                .when()
                .post(LOGIN_USER)
                .then();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            return null;
        }

        return given()
                .spec(requestSpec)
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE_USER)
                .then();
    }
}