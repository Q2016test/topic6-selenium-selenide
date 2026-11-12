package ru.homework.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import ru.homework.support.UiConfig;

import static org.junit.jupiter.api.Assertions.*;

@Tag("selenium")
class SeleniumLectureTest extends SeleniumBase {
    @Test
    @DisplayName("1.1 Товар из админки на витрине")
    void productCreatedInAdminAppearsInStorefront() {
        String name = data.newName();
        login();
        createInAdmin(name, "45.5");
        long id = data.idOf(name);
        click(By.cssSelector("a[href='/']"));
        assertEquals(name, visible(By.cssSelector("#card-" + id + " h4")).getText());
        assertEquals("45.5 ₽", visible(By.cssSelector("#card-" + id + " h4 + div")).getText());
    }

    @Test
    @DisplayName("1.2 Товар отображается в корзине")
    void productAppearsInCart() {
        String name = data.newName();
        long id = data.create(name, 80.5);
        addToCart(id);
        click(By.id("open-cart-btn"));
        assertEquals(name, visible(By.cssSelector("#cart-item-" + id + " b")).getText());
        assertEquals("1", visible(By.cssSelector("#cart-item-" + id + " .qty-controls span")).getText());
    }

    @Test
    @DisplayName("1.3 Неверные логин и пароль")
    void invalidCredentialsAreRejected() {
        browser.get(UiConfig.BASE_URL + "/admin");
        fill(By.id("username"), "incorrect-user");
        fill(By.id("password"), "incorrect-password");
        click(By.cssSelector("button[type='submit']"));
        assertFalse(visible(By.cssSelector(".alert-danger")).getText().isBlank(), "Ожидалось сообщение об ошибке входа");
        assertTrue(browser.getCurrentUrl().contains("/login?error"), "Ожидалась страница ошибки входа");
        assertTrue(browser.findElements(By.id("add-btn")).isEmpty(), "Админка должна быть недоступна");
    }

    @Test
    @DisplayName("1.4 Корзина сохраняется после обновления")
    void cartSurvivesPageRefresh() {
        String name = data.newName();
        long id = data.create(name, 70.0);
        addToCart(id);
        click(By.id("open-cart-btn"));
        assertEquals(name, visible(By.cssSelector("#cart-item-" + id + " b")).getText());
        browser.navigate().refresh();
        visible(By.cssSelector("#card-" + id));
        assertEquals("1", visible(By.id("cart-count")).getText(), "После обновления товар должен остаться в корзине");
        click(By.id("open-cart-btn"));
        assertEquals(name, visible(By.cssSelector("#cart-item-" + id + " b")).getText());
    }
}
