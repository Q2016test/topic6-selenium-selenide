package ru.homework.support;

public final class UiConfig {
    public static final String BASE_URL = System.getProperty("ui.baseUrl", "http://localhost:8080");
    public static final String USERNAME = System.getProperty("ui.username", "admin");
    public static final String PASSWORD = System.getProperty("ui.password", "secret123");
    public static final boolean HEADLESS = Boolean.parseBoolean(System.getProperty("ui.headless", "true"));

    private UiConfig() {
    }
}
