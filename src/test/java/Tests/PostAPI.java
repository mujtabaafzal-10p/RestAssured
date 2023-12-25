package Tests;

import PageObjects.Booking;
import PageObjects.BookingDates;
import Tests.BaseTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class PostAPI extends BaseTest {
    @Test
    public void createBooking(){
        //prepare request body
        JSONObject booking = new JSONObject();
        JSONObject bookingDates = new JSONObject();
        booking.put("firstname", "Umar");
        booking.put("lastname", "Akmal");
        booking.put("totalprice", 1500);
        booking.put("depositpaid", false);
        booking.put("additionalneeds", "Lunch");
        booking.put("bookingdates", bookingDates);
        bookingDates.put("checkin", "2023-01-01");
        bookingDates.put("checkout", "2023-06-01");

        Response response = RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body(booking.toJSONString())
                .when()
                    .post("/booking")
                .then()
                .assertThat()
                    .statusCode(200)
                    .body("booking.firstname", Matchers.equalTo(booking.get("firstname")))
                    .body("booking.totalprice", Matchers.equalTo(booking.get("totalprice")))
                    .body("booking.depositpaid", Matchers.equalTo(booking.get("depositpaid")))
                    .body("booking.bookingdates.checkin", Matchers.equalTo(bookingDates.get("checkin")))
                .extract()
                    .response();
        Assert.assertTrue(response.getBody().asString().contains("bookingid"));
        int bookingID = response.path("bookingid");
        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .pathParam("bookingid", bookingID)
                .when()
                    .get("/booking/{bookingid}")
                .then()
                    .assertThat()
                        .statusCode(200)
                        .body("firstname", Matchers.equalTo(booking.get("firstname")))
                        .body("totalprice", Matchers.equalTo(booking.get("totalprice")))
                        .body("depositpaid", Matchers.equalTo(booking.get("depositpaid")))
                        .body("bookingdates.checkin", Matchers.equalTo(bookingDates.get("checkin")));
    }

    @Test
    public void postWithFile(){
        try {
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void postWithPom(){
        BookingDates bookingDates = new BookingDates("2023-03-25", "2023-03-30");
        Booking booking = new Booking("John", "Doe", "breakfast", 1000, true, bookingDates);
        //serialization
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = null;
        try {
            String jsonSchema = FileUtils.readFileToString(new File(Json_Schema),"UTF-8");
            requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);
            //System.out.println(requestBody);

            //De-Serialization
            Booking bookingDetails = objectMapper.readValue(requestBody, Booking.class);
            System.out.println(bookingDetails.getTotalprice());
            System.out.println(bookingDetails.getBookingdates().getCheckin());

            Response response = RestAssured
                    .given()
                        .contentType(ContentType.JSON)
                        .body(requestBody)
                    .when()
                        .post("/booking")
                    .then()
                        .assertThat()
                        .statusCode(200)
                    .extract().response();
            int bookingId = response.path("bookingid");

            //System.out.println(jsonSchema);

            RestAssured
                    .given()
                        .contentType(ContentType.JSON)
                    .when()
                        .get("booking/{bookingId}",bookingId)
                    .then()
                        .assertThat()
                        .statusCode(200)
                        .body(JsonSchemaValidator.matchesJsonSchema(jsonSchema));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
