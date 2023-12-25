package Tests;

import PageObjects.Booking;
import PageObjects.BookingDates;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class PatchAPI extends BaseTest {
    @Test
    public void updateBooking() throws IOException {
        String patchBody = FileUtils.readFileToString(new File(Patch_File),"UTF-8");
        //Booking objects
        BookingDates bookingDates = new BookingDates("2023-03-25", "2023-03-30");
        Booking booking = new Booking("Mukhtar", "Doe", "breakfast", 1000, true, bookingDates);

        int bookingID = getBookingID(booking);
        //generate Token
        String token = getToken();
        //patch api call
        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body(patchBody)
                    .header("Cookie", "token="+token)
                .when()
                    .patch("booking/{bookingID}",bookingID)
                .then()
                    .assertThat()
                    .statusCode(200)
                    .body("lastname", Matchers.equalTo("Ali"));
    }
}
