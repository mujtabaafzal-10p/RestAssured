package Tests;

import PageObjects.Booking;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;

public class BaseTest {
    public static final String File_Base_Path = "./src/main/resources/";
    public static final String Post_File = File_Base_Path + "post.txt";
    public static final String Json_Schema = File_Base_Path + "expectedJsonSchema.txt";
    public static final String Put_File = File_Base_Path + "put.txt";
    public static final String Auth_File = File_Base_Path + "auth.txt";
    public static final String Patch_File = File_Base_Path + "patch.txt";

    @BeforeMethod
    public void setup(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";


    }
    public String getToken() throws IOException {
        String authBody = FileUtils.readFileToString(new File(Auth_File),"UTF-8");
        Response response =
                RestAssured
                        .given()
                            .contentType(ContentType.JSON)
                            .body(authBody)
                        .when()
                            .post("/auth")
                        .then()
                            .assertThat()
                            .statusCode(200)
                        .extract().response();
        return response.path("token");
    }
    public int getBookingID(Booking booking) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);
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
        return response.path("bookingid");
    }
    public Response getBookingDetails(int bookingID){
        Response response = RestAssured
                .given()
                    .contentType(ContentType.JSON)
                .when()
                    .get("/booking{bookingID}", bookingID)
                .then()
                .extract().response();
        return response;
    }
}
