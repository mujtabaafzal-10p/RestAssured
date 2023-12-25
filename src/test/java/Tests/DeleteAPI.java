package Tests;

import PageObjects.Booking;
import PageObjects.BookingDates;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class DeleteAPI extends BaseTest {
    @Test
    public void deleteBooking() throws IOException {
        //Booking objects
        BookingDates bookingDates = new BookingDates("2023-03-25", "2023-03-30");
        Booking booking = new Booking("Mukhtar", "Doe", "breakfast", 1000, true, bookingDates);

        int bookingID = getBookingID(booking);
        //generate Token
        String token = getToken();
        RestAssured
                .given()
                //.filter(new RestAssuredListener())
                    .contentType(ContentType.JSON)
                    .header("Cookie", "token=" + token)
                .when()
                    .delete("booking/{bookingID}", bookingID)
                .then()
                    .assertThat()
                    .statusCode(201);
        Assert.assertEquals(getBookingDetails(bookingID).statusCode(), 404);
    }
}
