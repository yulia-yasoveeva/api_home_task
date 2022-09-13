package org.example.userstest;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.entities.Order;
import org.example.log.Log;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Random;

public class OrderServiceTest {
    private static final String BASE_URI = "https://petstore.swagger.io/v2/store/order";
    private static final String INVENTORY_URI = "https://petstore.swagger.io/v2/store/inventory";
    private static final Random RANDOM = new Random();
    private static final int OK = 200;


    @BeforeClass
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    public void createOrderTest() {
        Log.info("Create order");
        int id = RANDOM.nextInt(Integer.MAX_VALUE);

        Order expectedOrder = createOrder(id);
        Response response = postOrder(expectedOrder);
        Assert.assertEquals(response.jsonPath().getObject("", Order.class), createOrder(id));
    }

    @Test
    public void findOrderById() {
        Log.info("Find order by id");
        int id = 5;
        String url = BASE_URI + "/" + id;

        Response responseFindOrderById = RestAssured.expect()
                .statusCode(OK)
                .log().ifError()
                .when().get(url);
        Order order = responseFindOrderById.jsonPath().getObject("", Order.class);
        Assert.assertTrue(order.getId() == id);

    }

    @Test
    public void petInventoriesByStatus() {
        Log.info("Pet inventories by status");
        Map<String, Integer> responseInventory = RestAssured.expect()
                .statusCode(OK)
                .log().ifError()
                .when().get(INVENTORY_URI)
                .then().extract().body().as(new TypeRef<Map<String, Integer>>() {
                });
        Assert.assertFalse(responseInventory.isEmpty());

    }

    @Test
    public void deleteOrderById() {
        Log.info("Delete order");
        int id = 15;
        String url = BASE_URI + "/" + id;
        Order expectedOrder = createOrder(id);

        postOrder(expectedOrder);

        RestAssured.expect()
                .statusCode(OK)
                .log().ifError()
                .when().delete(url);
        RestAssured.expect()
                .statusCode(404)
                .when().get(url);

    }

    private Response postOrder(Order order) {
        RequestSpecification requestSpecification = RestAssured.given().body(order).headers("Content-Type", "application/json");
        return requestSpecification.when().post(BASE_URI);
    }

    private Order createOrder(int id) {
        return new Order()
                .setId(id)
                .setPetId(22)
                .setQuantity(2)
                .setShipDate("2022-09-13T07:33:01.921+0000")
                .setStatus("placed")
                .setComplete(true);
    }
}
