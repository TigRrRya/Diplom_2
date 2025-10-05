import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.models.user.Order;
import stellarburgers.models.user.User;
import stellarburgers.steps.StepsOrder;
import stellarburgers.steps.StepsUser;
import utils.OrderGenerator;
import utils.UserGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderCreationTest {
    private StepsUser stepsUser;
    private StepsOrder stepsOrder;
    private User registeredUser;
    private String accessToken = null;

    @Before
    public void stepUp() {
        stepsUser = new StepsUser();
        stepsOrder = new StepsOrder();

        registeredUser = UserGenerator.getValidUser();
        ValidatableResponse response = stepsUser.registerUser(registeredUser);

        if (response.extract().path("success")) {
            accessToken = response.extract().path("accessToken");
        }
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            stepsUser.deleteUser(accessToken).statusCode(SC_ACCEPTED);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и валидными ингредиентами")
    @Description("Проверяет успешное создание заказа авторизованным пользователем с корректным списком ингредиентов. Ожидается статус 200 OK.")
    public void createOrderLoginAndValidIngredientTest() {
        Order validOrder = OrderGenerator.getValidOrderWithTwoIngredients();

        ValidatableResponse response = stepsOrder.createOrderWithAuth(validOrder, accessToken);

        response.statusCode(SC_OK).body("success", equalTo(true)).body("name", notNullValue()).body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверяет невозможность создания заказа без передачи токена авторизации. Ожидается статус 401 Unauthorized.")
    public void createOrderWithoutAuthReturns401() {
        Order validOrder = OrderGenerator.getValidOrderWithTwoIngredients();

        ValidatableResponse response = stepsOrder.createOrderWithoutAuth(validOrder);

        response.statusCode(SC_UNAUTHORIZED).body("success", equalTo(false)).body("message", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов (пустой массив)")
    @Description("Проверяет, что система возвращает ошибку при попытке создать заказ с пустым списком ингредиентов. Ожидается статус 400 Bad Request.")
    public void createOrderWithAuthWithoutIngredientsReturns400() {
        Order emptyOrder = OrderGenerator.getOrderWithoutIngredients();

        ValidatableResponse response = stepsOrder.createOrderWithAuth(emptyOrder, accessToken);

        response.statusCode(SC_BAD_REQUEST).body("success", equalTo(false)).body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с невалидным хешем ингредиента")
    @Description("Проверяет обработку запроса, содержащего ингредиент с некорректным или несуществующим хешем. Ожидается статус 500 Internal Server Error (согласно текущему поведению API).")
    public void createOrderWithAuthAndInvalidHashReturns500() {
        Order invalidOrder = OrderGenerator.getOrderWithInvalidHash();

        ValidatableResponse response = stepsOrder.createOrderWithAuth(invalidOrder, accessToken);

        response.statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}