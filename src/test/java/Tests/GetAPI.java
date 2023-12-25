package Tests;

import Tests.BaseTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GetAPI extends BaseTest {
    @Test
    public void getAllBooking(){
        Response response = RestAssured
                .given()
                    .contentType(ContentType.JSON)
                .when()
                    .get("/booking")
                .then()
                    .assertThat()
                        .statusCode(200)
                        .header("Server", "Cowboy")
                        .header("Content-Type", "application/json; charset=utf-8")
                .extract()
                    .response();
        Assert.assertTrue(response.getBody().asString().contains("bookingid"));
    }
}
