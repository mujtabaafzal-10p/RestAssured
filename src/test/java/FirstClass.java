import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;

public class FirstClass {
    @Test
    public void Task1(){
        // Set base URI for the API
        RestAssured.baseURI = "https://reqres.in/api";
        // Send a GET request to the specified endpoint and log all
        Response response = given()
                .when()
                    .get("/users?page=2")
                .then()
                    .log().all() // Log request and response
                    .statusCode(200)
                    .extract().response();
        Assert.assertEquals(response.jsonPath().getInt("data[2].id"), 9); // Verify 3rd element id
        Assert.assertEquals(response.jsonPath().getString("data[2].first_name"), "Tobias"); // Verify 3rd element first name
        response.then().body("data.email", hasItem("michael.lawson@reqres.in"));
        response.then().body("data.last_name", hasItem("Fields")); // Verify last name "Fields" exists
        response.then().body("data.last_name", hasItem("Howell")); // Verify last name "Howell" exists
    }
    @Test
    public void Task2(){
        Response response1 = RestAssured.delete("https://reqres.in/api/users/2");
        Assert.assertEquals(response1.statusCode(),204);
        // Create payload
        JSONObject payload = new JSONObject();
        payload.put("email", "eve.holt@reqres.in");
        payload.put("password", "pistol");
        // Send request
        Response response2 = given()
                .header("Content-Type", "application/json")
                .body(payload.toJSONString())
                .when()
                    .post("https://reqres.in/api/register")
                .then()
                    .log().all() // Log request and response
                    .statusCode(200)
                    .extract().response();
        // Assert response time is less than 10 seconds
        Assert.assertTrue(response2.time()<10000);
        // Get token value
        String token = response2.jsonPath().getString("token");
        System.out.println("Token: " + token);
        // Check if token value is null or empty
        if (token == null || token.isEmpty()) {
            Assert.assertTrue(false);
        }
    }
}
