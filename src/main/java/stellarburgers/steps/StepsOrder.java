package stellarburgers.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import stellarburgers.models.user.Order;

import static io.restassured.RestAssured.given;
import static stellarburgers.api.ConstantsApiAndUrl.CREATE_ORDERS;
import static stellarburgers.api.ConstantsApiAndUrl.DATE_INGREDIENTS;


public class StepsOrder extends BaseApi {

    public StepsOrder() {
        super();
    }

    @Step("Отправка GET-запроса на /ingredients для получения хешей ингредиентов")
    public ValidatableResponse getIngredients() {
        return given()
                .spec(requestSpec)
                .when()
                .get(DATE_INGREDIENTS)
                .then();
    }

    @Step("Отправка POST-запроса на /orders для создания заказа с авторизацией")
    public ValidatableResponse createOrderWithAuth(Order order, String accessToken) {
        return given()
                .spec(requestSpec)
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(CREATE_ORDERS)
                .then();
    }

    @Step("Отправка POST-запроса на /orders для создания заказа без авторизации")
    public ValidatableResponse createOrderWithoutAuth(Order order) {
        return given()
                .spec(requestSpec)
                .body(order)
                .when()
                .post(CREATE_ORDERS)
                .then();
    }
}
