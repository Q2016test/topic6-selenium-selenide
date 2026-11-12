package ru.homework.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

@Tag("ui")
public abstract class DataTestBase {
    protected TestData data;

    @BeforeEach
    protected void initializeTestData() {
        data = new TestData();
    }

    @AfterEach
    protected void deleteTestData() {
        data.cleanup();
    }
}
