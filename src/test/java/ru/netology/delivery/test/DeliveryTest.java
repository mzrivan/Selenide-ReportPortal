package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;
import ru.netology.delivery.util.ScreenShooterReportPortalExtension;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static ru.netology.delivery.util.LoggingUtils.logInfo;

class DeliveryTest {
    @ExtendWith({ScreenShooterReportPortalExtension.class})

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan meeting with yo in name ")
    void shouldSuccessfulPlanWithRussianYoInName() {
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var validUser = DataGenerator.Registration.generateUser("ru");
        $("[data-test-id=city] input").setValue(validUser.getCity());
        logInfo("В поле город введено:" + validUser.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        logInfo("В поле дата введено:" + firstMeetingDate);
        $("[data-test-id=name] input").setValue(validUser.getName() + "ё");
        logInfo("В поле имя введено:" + validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        logInfo("В поле телефон введено:" + validUser.getPhone());
        $("[data-test-id=agreement] span").click();
        $("[role=button].button").click();
        $("[data-test-id=success-notification] .notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + firstMeetingDate), Duration.ofSeconds(3))
                .shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement] span").click();
        $("[role=button].button").click();
        $("[data-test-id=success-notification] .notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + firstMeetingDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);
        $("[data-test-id=success-notification] button").click();
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(secondMeetingDate);
        $("[role=button].button").click();
        $("[data-test-id=replan-notification] .notification__content")
                .shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);
        $("[data-test-id=replan-notification] button").click();
        $("[data-test-id=success-notification] .notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + secondMeetingDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);

    }

    @ParameterizedTest
    @CsvSource({"Vasiliy",
            "1111111",
            "@#$%!",
    })
    @DisplayName("Should Give Invalid Notification Name")
    void shouldGiveInvalidNotificationName(String name) {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 7;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue(name);
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement] span").click();
        $("[role=button].button").click();
        $("[data-test-id=name] .input__sub")
                .shouldHave(Condition.text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы. "), Duration.ofSeconds(4))
                .shouldBe(Condition.visible);

    }

    @Test
    @DisplayName("Should Give Invalid Notification Empty Name")
    void shouldGiveInvalidNotificationEmptyName() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 5;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement] span").click();
        $("[role=button].button").click();
        $("[data-test-id=name] .input__sub")
                .shouldHave(Condition.text("Поле обязательно для заполнения"), Duration.ofSeconds(4))
                .shouldBe(Condition.visible);

    }

    @ParameterizedTest
    @CsvSource(
            {"+7123456789",
                    "+712345678910",
                    "-71234567891",
                    "+712345678",
                    "+7123456789112",
                    "+7123456789A",
                    "71234567891",
                    "712345678912",
                    "FGFDfGGDDDD",
                    //"+71234567891" //Верный вариант для проверки теста
            })
    @DisplayName("Should Give Invalid Notification Phone")
    void shouldGiveInvalidNotificationPhone(String phone) {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 7;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(phone);
        $("[data-test-id=agreement] span").click();
        $("[role=button].button").click();
        $("[data-test-id=phone] .input__sub")
                .shouldHave(Condition.text("Телефон указан неверно. "), Duration.ofSeconds(4))
                .shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("Should Give Invalid Notification Empty Phone")
    void shouldGiveInvalidNotificationEmptyPhone() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 7;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=agreement] span").click();
        $("[role=button].button").click();
        $("[data-test-id=phone] .input__sub")
                .shouldHave(Condition.text("Поле обязательно для заполнения"), Duration.ofSeconds(4))
                .shouldBe(Condition.visible);

    }

    @ParameterizedTest
    @CsvSource(
            {"Москва!",
                    "Москва1",
                    "Москв",
                    "Москвa" //Английская "a"

                    //"Москва" //Верный вариант для проверки теста
            })
    @DisplayName("Should Give Invalid Notification City")
    void shouldGiveInvalidNotificationCity(String city) {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        $("[data-test-id=city] input").setValue(city);
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement] span").click();
        $("[role=button].button").click();
        $("[data-test-id=city] .input__sub")
                .shouldHave(Condition.text("Доставка в выбранный город недоступна"), Duration.ofSeconds(4))
                .shouldBe(Condition.visible);

    }

    @Test
    @DisplayName("Should Give Invalid Notification Empty City")
    void shouldGiveInvalidNotificationEmptyCity() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 9;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement] span").click();
        $("[role=button].button").click();
        $("[data-test-id=city] .input__sub")
                .shouldHave(Condition.text("Поле обязательно для заполнения"), Duration.ofSeconds(4))
                .shouldBe(Condition.visible);

    }

    @Test
    @DisplayName("Should not successful meeting without agreement")
    void shouldNotSuccessfulMeetingWithoutAgreement() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[role=button].button").click();
        $("[data-test-id=agreement].input_invalid .checkbox__text")
                .shouldHave(Condition.text("Я соглашаюсь с условиями обработки и использования моих персональных данных"), Duration.ofSeconds(4))
                .shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("Should not successful meeting 2 days")
    void shouldNotSuccessfulMeeting2Days() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 2;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement] span").click();
        $("[role=button].button").click();
        $("[data-test-id=date] .input__sub")
                .shouldHave(Condition.text("Заказ на выбранную дату невозможен"), Duration.ofSeconds(4))
                .shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("Should not successful meeting with 21s month")
    void shouldNotSuccessfulMeetingWith21sMonth() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue("01.21.2023");
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement] span").click();
        $("[role=button].button").click();
        $("[data-test-id=date] .input__sub")
                .shouldHave(Condition.text("Неверно введена дата"), Duration.ofSeconds(4))
                .shouldBe(Condition.visible);
    }
}

