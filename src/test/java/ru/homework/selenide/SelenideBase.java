package ru.homework.selenide;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.homework.support.DataTestBase;
import ru.homework.support.UiConfig;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public abstract class SelenideBase extends DataTestBase {
    @BeforeEach
    protected void startBrowser() {
        Configuration.baseUrl = UiConfig.BASE_URL;
        Configuration.browser = "chrome";
        Configuration.headless = UiConfig.HEADLESS;
        Configuration.browserSize = "1280x900";
        Configuration.timeout = 5000;
        Configuration.reportsFolder = "build/reports/selenide";
        String binary = System.getProperty("ui.browserBinary");
        if (binary != null) {
            Configuration.browserBinary = binary;
        }
        open("/");
    }

    @AfterEach
    protected void closeBrowser() {
        closeWebDriver();
    }

    protected void login() {
        open("/admin");
        $("#username").setValue(UiConfig.USERNAME);
        $("#password").setValue(UiConfig.PASSWORD);
        $("button[type='submit']").click();
        $("#add-btn").shouldBe(visible);
    }

    protected void createInAdmin(String name, String price) {
        $("#n-name").setValue(name);
        $("#n-price").setValue(price);
        $("#add-btn").click();
        $("#tbody input[value='" + name + "']").shouldBe(visible);
    }

    protected SelenideElement card(long id) {
        return $("#card-" + id);
    }

    protected void addToCart(long id) {
        card(id).shouldBe(visible).$("[data-action='add-to-cart']").click();
    }

    protected void openCart() {
        $("#open-cart-btn").click();
        $("#cartModal").shouldBe(visible);
    }
}
