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
            stepsUser.deleteUser(accessToken).statusCode(202);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и валидными ингредиентами. 200 Ok")
    public void createOrderLoginAndValidIngredientTest() {
        Order validOrder = OrderGenerator.getValidOrderWithTwoIngredients();

        ValidatableResponse response = stepsOrder.createOrderWithAuth(validOrder, accessToken);
        response.statusCode(200).body("success", equalTo(true)).body("name", notNullValue()).body("order.number", notNullValue());

    }

    @Test
    @DisplayName("Создание заказа без авторизации. ошибка 401")
    public void createOrderWithoutAuthReturns401() {
        Order validOrder = OrderGenerator.getValidOrderWithTwoIngredients();

        ValidatableResponse response = stepsOrder.createOrderWithoutAuth(validOrder);

        response.statusCode(401).body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов (пустой массив). Ошибка 400")
    public void createOrderWithAuthWithoutIngredientsReturns400() {
        Order emptyOrder = OrderGenerator.getOrderWithoutIngredients();

        ValidatableResponse response = stepsOrder.createOrderWithAuth(emptyOrder, accessToken);

        response.statusCode(400).body("success", equalTo(false)).body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с невалидным хешем ингредиента. Ошибка  500")
    public void createOrderWithAuthAndInvalidHashReturns500() {
        Order invalidOrder = OrderGenerator.getOrderWithInvalidHash();

        ValidatableResponse response = stepsOrder.createOrderWithAuth(invalidOrder, accessToken);

        response.statusCode(500);
    }


}