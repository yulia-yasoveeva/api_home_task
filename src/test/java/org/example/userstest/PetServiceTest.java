package org.example.userstest;


import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.entities.Category;
import org.example.entities.Pet;
import org.example.entities.Tag;
import org.example.log.Log;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

public class PetServiceTest {


    private static final String BASE_URI = "https://petstore.swagger.io/v2/pet";
    private static final String URI_STATUS_SOLD = "https://petstore.swagger.io/v2/pet/findByStatus?status=sold";
    private static final int OK = 200;
    private static final Random RANDOM = new Random();

    @BeforeClass
    public void setup() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    public void createPetTest() {
        Log.info("Create pet:");
        int id = RANDOM.nextInt(Integer.MAX_VALUE);
        Pet expectedPet = createPet(id);
        Response response = postPet(expectedPet);
        Assert.assertEquals(response.jsonPath().getObject("", Pet.class), expectedPet);

    }

    @Test
    public void findPetById() {
        Log.info("Find pet:");
        int id = RANDOM.nextInt(Integer.MAX_VALUE);
        postPet(createPet(id));
        String url = BASE_URI + "/" + id;

        Response response = RestAssured.expect()
                .statusCode(OK)
                .log().ifError()
                .when().get(url);
        Assert.assertEquals(response.jsonPath().getObject("", Pet.class), createPet(id));
    }

    @Test
    public void deletePetById() {
        Log.info("Delete pet:");
        int id = RANDOM.nextInt(Integer.MAX_VALUE);
        postPet(createPet(id));
        String url = BASE_URI + "/" + id;
        RestAssured.expect()
                .statusCode(OK)
                .when().delete(url);
        RestAssured.expect()
                .statusCode(404)
                .when().get(url);
    }

    @Test
    public void updatePetById() {
        Log.info("Update pet:");
        int id = RANDOM.nextInt(Integer.MAX_VALUE);
        postPet(createPet(id));

        Response response = RestAssured.given()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .formParam("status", "sold")
                .when().post(BASE_URI + "/" + id);

        Response responseStatusSold = RestAssured
                .when().get(URI_STATUS_SOLD);
        List<Pet> pets = responseStatusSold.jsonPath().getList("", Pet.class);
        Assert.assertTrue(pets.stream().anyMatch(pet -> pet.getId() == id));


    }

    private Response postPet(Pet pet) {
        RequestSpecification requestSpecification = RestAssured.given().body(pet).headers("Content-Type", "application/json");
        return requestSpecification.when().post(BASE_URI);
    }

    private Pet createPet(int id) {
        Category category = new Category().setId(0).setName("string");
        Tag tag = new Tag().setId(0).setName("string");
        Tag[] tags = new Tag[]{tag};

        return new Pet()
                .setId(id)
                .setName("Bobo")
                .setCategory(category)
                .setPhotoUrls(new String[]{"string"})
                .setTags(tags)
                .setStatus("available");

    }
}
