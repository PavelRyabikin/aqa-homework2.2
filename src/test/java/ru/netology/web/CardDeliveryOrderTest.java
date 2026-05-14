package ru.netology.web;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class CardDeliveryOrderTest {

    public String generateDate(int days, String pattern) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern(pattern));
    }

    @BeforeEach
    public void setup() {
        Selenide.open("http://localhost:9999");
    }

    @Test
    public void shouldSubmitForm() {
        String planningDate = generateDate(3, "dd.MM.yyyy");

        $("[data-test-id=city] input").setValue("Москва");
        $("[data-test-id=date] input").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE).setValue(planningDate);
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79271234567");
        $("[data-test-id=agreement]").click();
        $(".button_view_extra").click();
        $("[data-test-id=notification]")
                .should(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Успешно! Встреча успешно забронирована на " + planningDate));
    }

    @Test
    public void shouldSubmitFormWithDropListAndCalendar() {
        LocalDate targetDate = LocalDate.now().plusDays(7);
        String planningDate = targetDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String targetDay = String.valueOf(targetDate.getDayOfMonth());

        $("[data-test-id=city] input").setValue("Мо");
        $$(".menu-item__control").findBy(Condition.text("Москва")).click();

        $("[data-test-id=date] .icon-button").click();

        while ($$(".calendar__layout tbody td").filterBy(Condition.text(targetDay)).isEmpty()) {
            $("[data-step='1']").click();
            Selenide.sleep(300);
        }

        $$(".calendar__layout tbody td").findBy(Condition.text(targetDay)).click();

        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79271234567");
        $("[data-test-id=agreement]").click();
        $(".button_view_extra").click();
        $("[data-test-id=notification]")
                .should(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Успешно! Встреча успешно забронирована на " + planningDate));
    }
}