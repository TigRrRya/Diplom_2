package utils;

import com.github.javafaker.Faker;
import stellarburgers.models.user.User;
import stellarburgers.models.user.UserLogin;

import java.util.Locale;

public class UserGenerator {

    private static final Faker FAKER = new Faker(new Locale("en"));


    public static User getValidUser() {
        String email = FAKER.internet().emailAddress();
        String password = FAKER.internet().password(8, 12);
        String name = FAKER.name().firstName();

        return new User(email, password, name);
    }


    public static User getUserWithMissingEmail() {

        String password = FAKER.internet().password(8, 12);
        String name = FAKER.name().firstName();

        return new User(null, password, name);
    }


    public static User getUserWithMissingName() {
        String email = FAKER.internet().emailAddress();
        String password = FAKER.internet().password(8, 12);

        return new User(email, password, null);
    }

    public static User getUserWithMissingPassword() {
        String email = FAKER.internet().emailAddress();
        String name = FAKER.name().firstName();

        return new User(email, null, name);
    }


    public static UserLogin getCredentials(User user) {
        return new UserLogin(user.getEmail(), user.getPassword());
    }


    public static UserLogin getInvalidCredentials() {
        String invalidEmail = FAKER.internet().emailAddress();
        String invalidPassword = FAKER.internet().password(8, 12);

        return new UserLogin(invalidEmail, invalidPassword);
    }

    public static UserLogin getCredentialsWithFakePassword(User user) {
        String invalidPassword = FAKER.internet().password(8, 12);
        while (invalidPassword.equals(user.getPassword())) {
            invalidPassword = FAKER.internet().password(8, 12);
        }
        return new UserLogin(user.getEmail(), invalidPassword);
    }
}