package ru.homework.selenide;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

@Tag("selenide")
class SelenideLectureTest extends SelenideBase {
    @Test
    @DisplayName("2.1 Товар из админки на витрине")
    void productCreatedInAdminAppearsInStorefront() {
        String name = data.newName();
        login();
        createInAdmin(name, "45.5");
        long id = data.idOf(name);
        $("a[href='/']").click();
        card(id).$("h4").shouldBe(visible, exactText(name));
        card(id).$("h4 + div").shouldBe(exactText("45.5 ₽"));
    }

    @Test
    @DisplayName("2.2 Товар отображается в корзине")
    void productAppearsInCart() {
        String name = data.newName();
        long id = data.create(name, 80.5);
        open("/");
        addToCart(id);
        openCart();
        $("#cart-item-" + id + " b").shouldBe(visible, exactText(name));
        $("#cart-item-" + id + " .qty-controls span").shouldBe(exactText("1"));
    }

    @Test
    @DisplayName("2.3 Неверные логин и пароль")
    void invalidCredentialsAreRejected() {
        open("/admin");
        $("#username").setValue("incorrect-user");
        $("#password").setValue("incorrect-password");
        $("button[type='submit']").click();
        $(".alert-danger").shouldBe(visible, matchText(".*\\S.*"));
        $("#add-btn").shouldBe(hidden);
    }

    @Test
    @DisplayName("2.4 Корзина сохраняется после обновления")
    void cartSurvivesPageRefresh() {
        String name = data.newName();
        long id = data.create(name, 70.0);
        open("/");
        addToCart(id);
        openCart();
        $("#cart-item-" + id + " b").shouldBe(visible, exactText(name));
        refresh();
        card(id).shouldBe(visible);
        $("#cart-count").shouldBe(exactText("1"));
        openCart();
        $("#cart-item-" + id + " b").shouldBe(visible, exactText(name));
    }

    @Test
    @DisplayName("2.5 Alert при сумме больше 300")
    void orderAboveLimitShowsAlert() {
        long id = data.create(data.newName(), 350.0);
        open("/");
        addToCart(id);
        openCart();
        $("#total-price").shouldBe(exactText("350"));
        $("#makeOrder").click();
        confirm("[SmartShop]: Денег не хватает! Сумма 350 ₽ превышает лимит 300 ₽.");
        $("#cartModal").shouldBe(visible);
        $("#cart-count").shouldBe(exactText("1"));
    }
}
