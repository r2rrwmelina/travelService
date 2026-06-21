package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import ru.netology.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class TravelPage {
    private final SelenideElement buttonBuy = $$(".button").findBy(Condition.text("Купить"));
    private final SelenideElement formName = $$("h3").findBy(Condition.text("Оплата по карте"));
    private final SelenideElement cardNumberField = $("input[placeholder='0000 0000 0000 0000']");
    private final SelenideElement monthField = $("input[placeholder='08']");
    private final SelenideElement yearField = $("input[placeholder='22']");
    private final SelenideElement nameField = $$(".input").findBy(Condition.text("Владелец")).$("input");
    private final SelenideElement cvcField = $("input[placeholder='999']");
    private final SelenideElement buttonSend = $$(".button").findBy(Condition.text("Продолжить"));
    private final SelenideElement successNotification = $(".notification_status_ok");
    private final SelenideElement errorNotification = $(".notification_status_error");
    private final SelenideElement cardNumberError = $$(".input_invalid").findBy(Condition.text("Номер карты")).$(".input__sub");
    private final SelenideElement monthError = $$(".input_invalid").findBy(Condition.text("Месяц")).$(".input__sub");
    private final SelenideElement yearError = $$(".input_invalid").findBy(Condition.text("Год")).$(".input__sub");
    private final SelenideElement ownerError = $$(".input_invalid").findBy(Condition.text("Владелец")).$(".input__sub");
    private final SelenideElement cvcError = $$(".input_invalid").findBy(Condition.text("CVC/CVV")).$(".input__sub");
    private final SelenideElement valueCardNumberField = $$(".input").findBy(Condition.text("Номер карты")).$(".input__control[value]");
    private final SelenideElement valueMonthField = $$(".input").findBy(Condition.text("Месяц")).$(".input__control[value]");
    private final SelenideElement valueYearField = $$(".input").findBy(Condition.text("Год")).$(".input__control[value]");
    private final SelenideElement valueCVCField = $$(".input").findBy(Condition.text("CVC/CVV")).$(".input__control[value]");

    public TravelPage() {
    }

    public void completeCardForm(DataHelper.CardInfo info) {
        buttonBuy.click();
        formName.should(Condition.visible);
        cardNumberField.setValue(info.getCardNumber());
        monthField.setValue(info.getMonth());
        yearField.setValue(info.getYear());
        nameField.setValue(info.getOwner());
        cvcField.setValue(info.getCvc());
    }

    public void sendForm() {
        buttonSend.click();
    }

    public void sendEmptyForm() {
        buttonBuy.click();
        buttonSend.click();
    }

    public void successTransaction(DataHelper.CardInfo info, String message) {
        completeCardForm(info);
        sendForm();
        successNotification.should(Condition.visible, Duration.ofSeconds(15)).should(Condition.text(message));
    }

    public void errorTransaction(DataHelper.CardInfo info, String message) {
        completeCardForm(info);
        sendForm();
        errorNotification.should(Condition.visible, Duration.ofSeconds(15)).should(Condition.text(message));
    }

    public void monthError(String message) {
        monthError.should(Condition.visible).should(Condition.text(message));
    }

    public void cardNumberError(String message) {
        cardNumberError.should(Condition.visible).should(Condition.text(message));
    }

    public void yearError(String message) {
        yearError.should(Condition.visible).should(Condition.text(message));
    }

    public void ownerError(String message) {
        ownerError.should(Condition.visible).should(Condition.text(message));
    }

    public void cvcError(String message) {
        cvcError.should(Condition.visible).should(Condition.text(message));
    }

    public String getValueOfCardNumberField() {
        return valueCardNumberField.getValue();
    }

    public String getValueOfMonthField() {
        return valueMonthField.getValue();
    }

    public String getValueOfYearField() {
        return valueYearField.getValue();
    }

    public String getValueOfCVCField() {
        return valueCVCField.getValue();
    }
}