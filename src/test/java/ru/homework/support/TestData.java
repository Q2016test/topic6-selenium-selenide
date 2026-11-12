package ru.homework.support;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.preemptive;
import static org.hamcrest.Matchers.*;

public final class TestData {
    private final Set<String> names = new HashSet<>();
    private final Set<Long> ids = new LinkedHashSet<>();

    public String newName() {
        String name = "ui-" + UUID.randomUUID();
        names.add(name);
        return name;
    }

    private RequestSpecification api() {
        return new RequestSpecBuilder().setBaseUri(UiConfig.BASE_URL)
                .setContentType(ContentType.JSON)
                .setAuth(preemptive().basic(UiConfig.USERNAME, UiConfig.PASSWORD)).build();
    }

    public long create(String name, double price) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", name);
        body.put("price", price);
        names.add(name);
        long id = given().spec(api()).body(body).when().post("/goods/add")
                .then().statusCode(200).body("data.id", notNullValue())
                .extract().jsonPath().getLong("data.id");
        ids.add(id);
        return id;
    }

    private List<Map<String, Object>> products() {
        return given().spec(api()).queryParam("page", 0).queryParam("size", 1000)
                .when().get("/goods/list").then().statusCode(200)
                .extract().jsonPath().getList("goods");
    }

    public long idOf(String name) {
        return products().stream().filter(product -> name.equals(product.get("name")))
                .mapToLong(product -> ((Number) product.get("id")).longValue()).findFirst()
                .orElseThrow(() -> new IllegalStateException("Товар не найден: " + name));
    }

    public void cleanup() {
        for (Map<String, Object> product : products()) {
            if (names.contains(product.get("name"))) {
                ids.add(((Number) product.get("id")).longValue());
            }
        }
        for (long id : ids) {
            given().spec(api()).when().delete("/goods/{id}", id)
                    .then().statusCode(anyOf(is(200), is(404)));
        }
        names.clear();
        ids.clear();
    }
}
