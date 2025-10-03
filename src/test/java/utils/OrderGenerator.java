package utils;


import io.restassured.response.ValidatableResponse;
import stellarburgers.models.user.Order;
import stellarburgers.steps.StepsOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class OrderGenerator {


    private static final StepsOrder STEPS_ORDER = new StepsOrder();


    private static List<String> getAllValidIngredientHashes() {
        ValidatableResponse response = STEPS_ORDER.getIngredients();

        return response.extract().path("data._id");
    }


    public static Order getValidOrderWithTwoIngredients() {
        List<String> allHashes = getAllValidIngredientHashes();


        Random random = new Random();
        List<String> selectedHashes = new ArrayList<>();

        int index1 = random.nextInt(allHashes.size());
        int index2 = random.nextInt(allHashes.size());

        selectedHashes.add(allHashes.get(index1));
        selectedHashes.add(allHashes.get(index2));

        return new Order(selectedHashes);
    }


    public static Order getOrderWithoutIngredients() {
        return new Order(List.of());
    }


    public static Order getOrderWithInvalidHash() {
        String invalidHash = "invalidHash" + new Random().nextInt(1000);
        return new Order(List.of(invalidHash));
    }
}