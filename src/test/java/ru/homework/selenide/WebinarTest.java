package ru.homework.selenide;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

@Tag("webinar")
class WebinarTest extends SelenideBase {
    @Test
    @DisplayName("3.1 Оплата трёх единиц товара")
    void purchaseThreeUnits() {
        long id = data.create(data.newName(), 80.0);
        open("/");
        $("#q-" + id).setValue("3");
        addToCart(id);
        openCart();
        $("#cart-item-" + id + " .qty-controls span").shouldBe(exactText("3"));
        $("#total-price").shouldBe(exactText("240"));
        $("#makeOrder").click();
        $("#toast-container").shouldBe(text("Заказ принят в обработку!"));
        $("#main-title").shouldBe(exactText("Заказ успешно оформлен!"));
        $("#cart-count").shouldBe(exactText("0"));
        $("#cartModal").shouldBe(hidden);
    }

    @Test
    @DisplayName("3.2 Сумма разных товаров в корзине")
    void totalOfDifferentProductsIsCorrect() {
        double[] prices = {45.5, 70.25, 20.0};
        int[] quantities = {2, 1, 3};
        long[] ids = new long[prices.length];
        for (int i = 0; i < prices.length; i++) {
            ids[i] = data.create(data.newName(), prices[i]);
        }
        open("/");
        for (int i = 0; i < ids.length; i++) {
            $("#q-" + ids[i]).setValue(String.valueOf(quantities[i]));
            addToCart(ids[i]);
        }
        openCart();
        $("#total-price").shouldBe(exactText("221.25"));
        $("#cart-count").shouldBe(exactText("6"));
        for (int i = 0; i < ids.length; i++) {
            $("#cart-item-" + ids[i] + " .qty-controls span")
                    .shouldBe(exactText(String.valueOf(quantities[i])));
        }
    }

    @Test
    @DisplayName("3.3 Уведомление о добавлении товара")
    void adminCreationShowsNotification() {
        String name = data.newName();
        login();
        createInAdmin(name, "50");
        $("#toast-container").shouldBe(text("Товар успешно добавлен!"));
        $("#tbody input[value='" + name + "']").shouldBe(visible);
    }

    @Test
    @DisplayName("3.4 Редактирование товара в админке")
    void editedProductAppearsInStorefront() {
        long id = data.create(data.newName(), 50.0);
        String newName = data.newName();
        login();
        $("#nm-" + id).setValue(newName);
        $("#pr-" + id).setValue("90.75");
        $("button[data-action='update'][data-id='" + id + "']").click();
        $("#toast-container").shouldBe(text("Товар #" + id + " обновлен"));
        $("a[href='/']").click();
        card(id).$("h4").shouldBe(visible, exactText(newName));
        card(id).$("h4 + div").shouldBe(exactText("90.75 ₽"));
    }
}
