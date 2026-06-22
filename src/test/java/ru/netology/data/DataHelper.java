package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;
import lombok.With;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DataHelper {
    private final static Faker faker = new Faker();

    private DataHelper() {
    }

    public static String generateMonth(int month) {
        return LocalDate.now().plusMonths(month).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String generateYear(int year) {
        return LocalDate.now().plusYears(year).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String generateOwner() {
        return faker.name().lastName() + " " + faker.name().firstName();
    }

    public static String generateCVC() {
        return faker.number().digits(3);
    }

    public static String getApprovedCard() {
        return "1111 2222 3333 4444";
    }

    public static String getDeclinedCard() {
        return "5555 6666 7777 8888";
    }

    public static String getAlternativeCard() {
        return faker.business().creditCardNumber();
    }

    public static class Payment {
        private Payment() {
        }

        public static CardInfo getValidCardInfo() {
            return new CardInfo(getApprovedCard(), generateMonth(0), generateYear(0), generateOwner(), generateCVC());
        }

        public static CardInfo getDeclinedCardNumberInfo() {
            return new CardInfo(getDeclinedCard(), generateMonth(1), generateYear(1), generateOwner(), generateCVC());
        }

        public static CardInfo getAlternativeCardNumberInfo() {
            return new CardInfo(getAlternativeCard(), generateMonth(1), generateYear(1), generateOwner(), generateCVC());
        }

        public static String getZeroCVC() {
            return "000";
        }
    }

    @Value
    @With
    public static class CardInfo {
        String cardNumber;
        String month;
        String year;
        String owner;
        String cvc;
    }
}
