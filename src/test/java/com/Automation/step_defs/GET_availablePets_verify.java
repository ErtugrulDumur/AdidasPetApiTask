package com.Automation.step_defs;

import static org.hamcrest.Matchers.*;

import com.Automation.pages.PetBody;
import com.Automation.utils.ConfigurationReader;
import com.google.gson.Gson;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class GET_availablePets_verify {

    Response response;
    Map<String,String> statusMap = new HashMap<>();

    @Given("the user should enter status as the following")
    public void the_user_should_enter_status_as_the_following(String status) {
        statusMap.put("status",status);
    }

    @When("the user get the pets from api with the following endPoints")
    public void the_user_get_the_pets_from_api_with_the_following_end_points(List<String> endPoints) {

        for (String endPoint : endPoints) {
            response = given().accept(ContentType.JSON)
                    .and().queryParams(statusMap)
                    .get(ConfigurationReader.get("baseurl_petstore") + "/" + endPoint);
        }

    }

    @Then("status code should be {int}")
    public void status_code_should_be(int responseStatusCode) {
        assertEquals(responseStatusCode, response.statusCode());
    }

    @And("content type of payload should be JSON")
    public void content_type_of_payload_should_be_json() {
        assertEquals("application/json",response.contentType());
    }

    @And("the user should be able to verify the payload")
    public void the_user_should_be_able_to_verify_the_payload() {
        Boolean available = response.body().asString().contains("available");
        assertTrue(available);

        String pet = response.path("status[0]");
        assertEquals(pet, "available");

        JsonPath jsonPet = response.jsonPath();
        String status = jsonPet.getString("status[0]").replace("[", "").replace("]", "");
        System.out.println(status);
        assertEquals(status, "available");

        response.then().assertThat().body("status[0]", equalTo("available"));

        List<Map<String, Object>> listPets = response.body().as(List.class);   //response.body().as(Map.class); I have list of maps
        System.out.println("All available pets : " + listPets);
        System.out.println("listPets.size() = " + listPets.size());
        response.prettyPrint();

        Map<String, Object> onePet = listPets.get(0); //(I may select randomly) I got the first map from the list
        System.out.println("First sample available pet : " + onePet);
        String petStatus = onePet.get("status").toString();
        assertEquals(petStatus, "available");

        int counter=1;
        for (Map<String, Object> eachPet : listPets) {
            System.out.println(counter + " - Pet " + eachPet);
            counter++;
        }

        response = given().accept(ContentType.JSON)
                .pathParam("petId","9100")
                .when().get(ConfigurationReader.get("baseurl_petstore") + "/pet/{petId}");
        PetBody pet1 = response.body().as(PetBody.class);
        System.out.println("pet1 Object = " + pet1.toString());
        assertEquals("doggie",pet1.getName());
        assertEquals(9100,pet1.getId());

        Gson gson = new Gson();
        PetBody petBodyMeta = gson.fromJson(response.body().asString(),PetBody.class);
        System.out.println(petBodyMeta.toString());

        PetBody pet2 = new PetBody();
        pet2.setId(343434);
        pet2.setName("Mike");

        String jsonBody = gson.toJson(pet2);
        System.out.println(jsonBody);

    }

}
