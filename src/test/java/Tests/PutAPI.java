package Tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class PutAPI extends BaseTest {
    @Test
    public void updateBooking() throws IOException {
        String putBody = FileUtils.readFileToString(new File(Put_File),"UTF-8");
        String postBody = FileUtils.readFileToString(new File(Post_File),"UTF-8");

        Response response = RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body(postBody)
                .when()
                    .post("/booking")
                .then()
                    .assertThat()
                    .statusCode(200)
                .extract().response();
        int bookingId = response.path("bookingid");
        String token = getToken();
        //put api call
        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body(putBody)
                    .header("Cookie", "token="+token)
                .when()
                    .put("booking/{bookingId}",bookingId)
                .then()
                    .assertThat()
                    .statusCode(200)
                    .body("firstname", Matchers.equalTo("Kamil"))
                    .body("lastname", Matchers.equalTo("Brown"));
    }
}
