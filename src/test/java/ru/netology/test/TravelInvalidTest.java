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
import static org.junit.jupiter.api.Assertions.*;

public class TravelInvalidTest {

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

    @Test
    void shouldToPerformErrorTransactionWithAlternativeCard() {
        var info = DataHelper.Payment.getAlternativeCardNumberInfo();
        var message = "Ошибка Ошибка! Банк отказал в проведении операции.";
        travelPage.errorTransaction(info, message);
        var dbInfo = SQLHelper.getPaymentStatus();
        assertNull(dbInfo);
    }

    @Test
    void shouldSendFormWithEmptyFields() {
        travelPage.sendEmptyForm();
        travelPage.cardNumberError("Неверный формат");
        travelPage.monthError("Неверный формат");
        travelPage.yearError("Неверный формат");
        travelPage.ownerError("Поле обязательно для заполнения");
        travelPage.cvcError("Неверный формат");
    }

    // Поле номера карты
    @ParameterizedTest
    @CsvSource({
            "1111 2222 3333 444, Неверный формат",
            " , Неверный формат"
    })
    void shouldShowErrorWithShortAndEmptyCardNumber(String cardNumber, String message) {
        var info = DataHelper.Payment.getValidCardInfo().withCardNumber(cardNumber);
        travelPage.completeCardForm(info);
        travelPage.sendForm();
        travelPage.cardNumberError(message);
    }

    // Поле месяца
    @ParameterizedTest
    @CsvSource({
            "13, Неверно указан срок действия карты",
            "9, Неверный формат",
            "00, Неверно указан срок действия карты",
            " , Неверный формат"
    })
    void shouldShowErrorWithInvalidMonthAndEmptyField(String month, String message) {
        var info = DataHelper.Payment.getValidCardInfo().withMonth(month);
        travelPage.completeCardForm(info);
        travelPage.sendForm();
        travelPage.monthError(message);
    }

    @Test
    void shouldNotAllowedPastMonth() {
        var pastMonth = DataHelper.generateMonth(-1);
        var info = DataHelper.Payment.getValidCardInfo().withMonth(pastMonth);
        travelPage.completeCardForm(info);
        travelPage.sendForm();
        travelPage.monthError("Неверно указан срок действия карты");
    }

    // Поле года
    @ParameterizedTest
    @CsvSource({
            "2, Неверный формат",
            "00, Истёк срок действия карты",
            " , Неверный формат"
    })
    void shouldShowErrorMessageWithInvalidYearAndEmptyField(String year, String message) {
        var info = DataHelper.Payment.getValidCardInfo().withYear(year);
        travelPage.completeCardForm(info);
        travelPage.sendForm();
        travelPage.yearError(message);
    }

    @Test
    void shouldNotAllowedPastYears() {
        var pastYear = DataHelper.generateYear(-1);
        var info = DataHelper.Payment.getValidCardInfo().withYear(pastYear);
        travelPage.completeCardForm(info);
        travelPage.sendForm();
        travelPage.yearError("Истёк срок действия карты");
    }

    @Test
    void shouldNotAllowedYearAfterSixYearsOrMore() {
        var futureYear = DataHelper.generateYear(6);
        var info = DataHelper.Payment.getValidCardInfo().withYear(futureYear);
        travelPage.completeCardForm(info);
        travelPage.sendForm();
        travelPage.yearError("Неверно указан срок действия карты");
    }

    // Владелец
    @Test
    void shouldShowErrorWithEmptyOwnerField() {
        var info = DataHelper.Payment.getValidCardInfo().withOwner("");
        travelPage.completeCardForm(info);
        travelPage.sendForm();
        travelPage.ownerError("Поле обязательно для заполнения");
    }

    // CVC
    @ParameterizedTest
    @CsvSource({
            "23, Неверный формат",
            " , Неверный формат"
    })
    void shouldShowErrorMessageWithInvalidCVCAndEmptyField(String cvc, String message) {
        var info = DataHelper.Payment.getValidCardInfo().withCvc(cvc);
        travelPage.completeCardForm(info);
        travelPage.sendForm();
        travelPage.cvcError(message);
    }

    @ParameterizedTest
    @CsvSource({
            "**** ++++ //** $$$$, ''",
            "ПРИВ ЕЕЕТ ПРИВ ЕЕЕТ, ''",
            "1111 2222 3333 4444 5, 1111 2222 3333 4444"
    })
    void shouldIgnoreInvalidCardNumberInput(String value, String expected) {
        var info = DataHelper.Payment.getValidCardInfo().withCardNumber(value);
        travelPage.completeCardForm(info);
        assertEquals(expected, travelPage.getValueOfCardNumberField());
    }

    @ParameterizedTest
    @CsvSource({
            "?!, ''",
            "ЕТ, ''",
            "111, 11"
    })
    void shouldIgnoreInvalidMonthInput(String value, String expected) {
        var info = DataHelper.Payment.getValidCardInfo().withMonth(value);
        travelPage.completeCardForm(info);
        assertEquals(expected, travelPage.getValueOfMonthField());
    }

    @ParameterizedTest
    @CsvSource({
            "?!, ''",
            "ЕТ, ''",
            "266, 26"
    })
    void shouldIgnoreInvalidYearInput(String value, String expected) {
        var info = DataHelper.Payment.getValidCardInfo().withYear(value);
        travelPage.completeCardForm(info);
        assertEquals(expected, travelPage.getValueOfYearField());
    }

    @ParameterizedTest
    @CsvSource({
            "@$%, ''",
            "GHB, ''",
            "3456, 345"
    })
    void shouldIgnoreInvalidCVCInput(String value, String expected) {
        var info = DataHelper.Payment.getValidCardInfo().withCvc(value);
        travelPage.completeCardForm(info);
        assertEquals(expected, travelPage.getValueOfCVCField());
    }
}