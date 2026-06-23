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
import static org.junit.jupiter.api.Assertions.*;

public class TravelInvalidTest {

    TravelPage travelPage;

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

//    @Test
//    @Description("Проверка кейса TC-2")
//    void shouldToPerformErrorTransactionWithDeclinedCard() {
//        var info = DataHelper.Payment.getDeclinedCardNumberInfo();
//        var message = "Ошибка Ошибка! Банк отказал в проведении операции.";
//        travelPage.errorTransaction(info, message);
//        var dbInfo = SQLHelper.getPaymentStatus();
//        assertEquals("DECLINED", dbInfo);
//    }

    @Test
    @Description("Проверка кейса TC-3")
    void shouldToPerformErrorTransactionWithAlternativeCard() {
        var info = DataHelper.Payment.getAlternativeCardNumberInfo();
        var message = "Ошибка Ошибка! Банк отказал в проведении операции.";
        travelPage.errorTransaction(info, message);
        var dbInfo = SQLHelper.getPaymentStatus();
        assertNull(dbInfo);
    }

    @Test
    @Description("Проверка кейса TC-4")
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
    @Description("Проверка кейсов: TC-5, TC-8")
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

    @ParameterizedTest
    @Description("Проверка кейсов: TC-6, TC-7")
    @CsvSource({
            "**** ++++ //** $$$$, ''",
            "ПРИВ ЕЕЕТ ПРИВ ЕЕЕТ, ''",
            "1111 2222 3333 4444 5, 1111 2222 3333 4444"
    })
    void shouldIgnoreInvalidCardNumberInput(String value, String expectedValue) {
        var info = DataHelper.Payment.getValidCardInfo().withCardNumber(value);
        travelPage.completeCardForm(info);
        travelPage.checkValueOfCardNumberField(expectedValue);
    }

    // Поле месяца
    @ParameterizedTest
    @Description("Проверка кейсов: TC-9, TC-11, TC-12, TC-15")
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
    @Description("Проверка кейса TC-14")
    void shouldNotAllowedPastMonth() {
        var pastMonth = DataHelper.generateMonth(-1);
        var info = DataHelper.Payment.getValidCardInfo().withMonth(pastMonth);
        travelPage.completeCardForm(info);
        travelPage.sendForm();
        travelPage.monthError("Неверно указан срок действия карты");
    }

    @ParameterizedTest
    @Description("Проверка кейсов: TC-10, TC-13")
    @CsvSource({
            "?!, ''",
            "ЕТ, ''",
            "111, 11"
    })
    void shouldIgnoreInvalidMonthInput(String value, String expectedValue) {
        var info = DataHelper.Payment.getValidCardInfo().withMonth(value);
        travelPage.completeCardForm(info);
        travelPage.checkValueOfMonthField(expectedValue);
    }

    // Поле года
    @ParameterizedTest
    @Description("Проверка кейсов: TC-18, TC-20, TC-22")
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
    @Description("Проверка кейса TC-19")
    void shouldNotAllowedPastYears() {
        var pastYear = DataHelper.generateYear(-1);
        var info = DataHelper.Payment.getValidCardInfo().withYear(pastYear);
        travelPage.completeCardForm(info);
        travelPage.sendForm();
        travelPage.yearError("Истёк срок действия карты");
    }

    @Test
    @Description("Проверка кейса TC-21")
    void shouldNotAllowedYearAfterSixYearsOrMore() {
        var futureYear = DataHelper.generateYear(6);
        var info = DataHelper.Payment.getValidCardInfo().withYear(futureYear);
        travelPage.completeCardForm(info);
        travelPage.sendForm();
        travelPage.yearError("Неверно указан срок действия карты");
    }

    @ParameterizedTest
    @Description("Проверка кейсов: TC-16, TC-17")
    @CsvSource({
            "?!, ''",
            "ЕТ, ''",
            "266, 26"
    })
    void shouldIgnoreInvalidYearInput(String value, String expectedValue) {
        var info = DataHelper.Payment.getValidCardInfo().withYear(value);
        travelPage.completeCardForm(info);
        travelPage.checkValueOfYearField(expectedValue);
    }

    // Владелец
//    @ParameterizedTest
//    @Description("Проверка кейсов: TC-23, TC-24, TC-28, TC-29, TC-30 (частично), TC-31")
//    @CsvSource({
//            "I, Слишком короткое имя",
//            "IVANOV IVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAN, Превышено максимальное допустимое количество символов",
//            "ИВАНОВ ИВАН, Неверный формат",
//            "5455 12346897, Неверный формат",
//            "*/++^*)(@ %:?*(), Неверный формат",
//            " , Поле обязательно для заполнения"
//    })
//    void shouldNotAllowedInvalidName(String value, String message) {
//        var info = DataHelper.Payment.getValidCardInfo().withOwner(value);
//        travelPage.completeCardForm(info);
//        travelPage.sendForm();
//        travelPage.ownerError(message);
//    }

    // CVC
    @ParameterizedTest
    @Description("Проверка кейсов: TC-33, TC-36")
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
    @Description("Проверка кейсов: TC-32, TC-34")
    @CsvSource({
            "@$%, ''",
            "GHB, ''",
            "3456, 345"
    })
    void shouldIgnoreInvalidCVCInput(String value, String expectedValue) {
        var info = DataHelper.Payment.getValidCardInfo().withCvc(value);
        travelPage.completeCardForm(info);
        travelPage.checkValueOfCVCField(expectedValue);
    }
}