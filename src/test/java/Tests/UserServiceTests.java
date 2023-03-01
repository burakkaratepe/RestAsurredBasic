package Tests;

import Base.BaseTest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.User;
import model.Users;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.testng.Assert.assertTrue;

public class UserServiceTests extends BaseTest {

    private String[] validAvatarExtensions = new String[]{"jpg", "JPG"};

    @Test(testName = "Multiple Users Response Test")
    @Parameters({"page"})
    public void getUsersResponseTime(String page) {
        System.out.println("page -> " + page);
        Users users = (Users) given()
                .queryParam("page", page)
                .when()
                .get(baseURI + "/users")
                .then()
                .statusCode(200)
                .time(lessThan(2000L))
                .body("page", equalTo(1))
                .extract()
                .body().as(Users.class);


        for (User user : users.getUsers()) {
            assertTrue(user.getEmail().contains("@") && user.getEmail().contains("."),
                    user.getEmail() + " is not a compatible email address");

            String avatarExtension = user.getAvatar().substring(user.getAvatar().lastIndexOf(".") + 1, user.getAvatar().length());
            assertTrue(Arrays.stream(validAvatarExtensions).anyMatch(avatarExtension::equals),
                    "Extension of (" + user.getAvatar() + ") does not match any of " + Arrays.toString(validAvatarExtensions));
        }
    }

    @Test(testName = "Single User Response Test")
    @Parameters({"userID"})
    public void getUser(String userID) {
        User user = when()
                .get(baseURI + "/users/" + userID)
                .then()
                .statusCode(200)
                .extract()
                .body().as(User.class);
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        System.out.println(gson.toJson(user));
    }
}
