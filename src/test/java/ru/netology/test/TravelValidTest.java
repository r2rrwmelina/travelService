package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.TravelPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TravelValidTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void setUp() {
        SQLHelper.cleanData();
        open("http://localhost:8080/");
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    TravelPage travelPage = new TravelPage();
    String message = "Успешно Операция одобрена Банком.";

    @Test
    public void shouldToPerformSuccessTransactionWithApprovedCard() {
        var info = DataHelper.Payment.getValidCardInfo();
        travelPage.successTransaction(info, message);
        var dbInfo = SQLHelper.getPaymentStatus();
        assertEquals("APPROVED", dbInfo);
    }

    @ParameterizedTest
    @CsvSource({
            "IVANOV IVAN-VICTOR",
            "IVANOV IVAN VICTOR",
            "IVANOV I'VAN"
    })
    void shouldToPerformSuccessTransactionWithDifferentTypesOfNames(String name) {
        var info = DataHelper.Payment.getValidCardInfo().withOwner(name);
        travelPage.successTransaction(info, message);
    }

    @Test
    void shouldToPerformSuccessTransactionWithThreeZeroInCVC() {
        var info = DataHelper.Payment.getValidCardInfo().withCvc("000");
        travelPage.successTransaction(info, message);
    }
}