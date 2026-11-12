package ru.homework.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.homework.support.DataTestBase;
import ru.homework.support.UiConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public abstract class SeleniumBase extends DataTestBase {
    protected WebDriver browser;
    protected WebDriverWait wait;

    @RegisterExtension
    final AfterTestExecutionCallback failureScreenshot = context -> {
        if (context.getExecutionException().isPresent() && browser != null) {
            Path folder = Path.of("build", "reports", "screenshots");
            Files.createDirectories(folder);
            Files.write(folder.resolve(context.getRequiredTestMethod().getName() + ".png"),
                    ((TakesScreenshot) browser).getScreenshotAs(OutputType.BYTES));
        }
    };

    @BeforeEach
    protected void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        if (UiConfig.HEADLESS) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1280,900");
        String binary = System.getProperty("ui.browserBinary");
        if (binary != null) {
            options.setBinary(binary);
        }
        browser = new ChromeDriver(options);
        wait = new WebDriverWait(browser, Duration.ofSeconds(5));
    }

    @AfterEach
    protected void closeBrowser() {
        if (browser != null) {
            browser.quit();
        }
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void fill(By locator, String value) {
        WebElement input = visible(locator);
        input.clear();
        input.sendKeys(value);
    }

    protected void login() {
        browser.get(UiConfig.BASE_URL + "/admin");
        fill(By.id("username"), UiConfig.USERNAME);
        fill(By.id("password"), UiConfig.PASSWORD);
        click(By.cssSelector("button[type='submit']"));
        visible(By.id("add-btn"));
    }

    protected void createInAdmin(String name, String price) {
        fill(By.id("n-name"), name);
        fill(By.id("n-price"), price);
        click(By.id("add-btn"));
        visible(By.cssSelector("#tbody input[value='" + name + "']"));
    }

    protected void addToCart(long id) {
        browser.get(UiConfig.BASE_URL + "/");
        click(By.cssSelector("#card-" + id + " [data-action='add-to-cart']"));
        wait.until(ExpectedConditions.textToBe(By.id("cart-count"), "1"));
    }
}
