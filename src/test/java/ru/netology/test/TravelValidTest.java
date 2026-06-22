package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Description;
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

    TravelPage travelPage;
    String message = "Успешно Операция одобрена Банком.";

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void setUp() {
        SQLHelper.cleanData();
        open("http://localhost:8080/");
        travelPage = new TravelPage();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @Description("Проверка кейса TC-1")
    public void shouldToPerformSuccessTransactionWithApprovedCard() {
        var info = DataHelper.Payment.getValidCardInfo();
        travelPage.successTransaction(info, message);
        var dbInfo = SQLHelper.getPaymentStatus();
        assertEquals("APPROVED", dbInfo);
    }

    @ParameterizedTest
    @Description("Проверка кейсов: TC-25, TC-26, TC-27")
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
    @Description("Проверка кейса TC-35")
    void shouldToPerformSuccessTransactionWithThreeZeroInCVC() {
        var info = DataHelper.Payment.getValidCardInfo().withCvc(DataHelper.Payment.getZeroCVC());
        travelPage.successTransaction(info, message);
    }
}