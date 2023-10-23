package guru.qa.niffler.util;

import com.github.javafaker.Faker;

public class DataUtils {

    public static String getRandomUsername() {
        Faker faker = new Faker();
        return faker.name().username();
    }

    public static String getRandomPassword() {
        Faker faker = new Faker();
        return faker.internet().password(3, 12);
    }
}
