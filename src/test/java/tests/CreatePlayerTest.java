package tests;

import io.restassured.response.Response;
import model.CreatePlayerParams;
import model.Editor;
import model.PlayerIdRequest;
import model.PlayerResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import providers.CreatePlayerDataProviders;

import java.util.ArrayList;
import java.util.List;
import utils.RandomDataUtil;

import static org.testng.Assert.*;

@Test(groups = "player-api")
public class CreatePlayerTest extends BaseApiTest {

    private final List<Long> createdPlayerIds = new ArrayList<>();

    @AfterClass
    public void cleanUpCreatedPlayers() {
        for (Long id : createdPlayerIds) {
            client.deletePlayer(Editor.SUPERVISOR.getValue(), new PlayerIdRequest(id));
        }
        createdPlayerIds.clear();
    }

    @Test(
            description = "Create player with valid data: SUPERVISOR and ADMIN should return 200, USER should return 403",
            dataProvider = "editorAndExpectedStatus",
            dataProviderClass = CreatePlayerDataProviders.class
    )
    public void createPlayerWithValidDataPerEditorReturnsExpectedStatus(String editor, int expectedStatus) {
        CreatePlayerParams params = new CreatePlayerParams(
                "25",
                "male",
                RandomDataUtil.randomLogin(),
                RandomDataUtil.randomPassword(),
                "user",
                RandomDataUtil.randomScreenName()
        );
        Response response = client.createPlayer(editor, params);
        assertNotNull(response);
        int actualStatus = response.getStatusCode();
        assertEquals(actualStatus, expectedStatus, "Editor=" + editor + " expected " + expectedStatus + " got " + actualStatus);
        if (actualStatus == 200) {
            Long id = response.jsonPath().getObject("id", Long.class);
            if (id != null) {
                createdPlayerIds.add(id);
            }
        }
    }

    @Test(
            description = "Create player with boundary ages, ages 17-59 should be passed",
            dataProvider = "ageBoundaryAndExpectedStatus",
            dataProviderClass = CreatePlayerDataProviders.class
    )
    public void createPlayerWithBoundaryAgeReturnsExpectedStatus(String age, int expectedStatus) {
        CreatePlayerParams params = new CreatePlayerParams(
                age,
                "male",
                RandomDataUtil.randomLogin(),
                RandomDataUtil.randomPassword(),
                "user",
                RandomDataUtil.randomScreenName()
        );
        Response response = client.createPlayer(Editor.SUPERVISOR.getValue(), params);
        assertNotNull(response);
        int actualStatus = response.getStatusCode();
        assertEquals(actualStatus, expectedStatus, "Age=" + age + " expected " + expectedStatus + " got " + actualStatus);

        if (actualStatus == 200) {
            Long id = response.jsonPath().getObject("id", Long.class);
            if (id != null) {
                createdPlayerIds.add(id);
            }
        }
    }

    @Test(
            description = "Create player with gender values returns expected status",
            dataProvider = "genderAndExpectedStatus",
            dataProviderClass = CreatePlayerDataProviders.class
    )
    public void createPlayerWithGenderReturnsExpectedStatus(String gender, int expectedStatus) {
        CreatePlayerParams params = new CreatePlayerParams(
                "30",
                gender,
                RandomDataUtil.randomLogin(),
                RandomDataUtil.randomPassword(),
                "user",
                RandomDataUtil.randomScreenName()
        );
        Response response = client.createPlayer(Editor.SUPERVISOR.getValue(), params);
        assertNotNull(response);
        int actualStatus = response.getStatusCode();
        assertEquals(actualStatus, expectedStatus, "Gender=" + gender + " expected " + expectedStatus + " got " + actualStatus);
        if (actualStatus == 200) {
            Long id = response.jsonPath().getObject("id", Long.class);
            if (id != null) {
                createdPlayerIds.add(id);
            }
        }
    }
    @Test(description = "Create admin response contains same input data + id, status 200, all mismatches reported")
    public void createAdminAndVerifyRespondBody() {
        CreatePlayerParams params = new CreatePlayerParams(
                "25",
                "male",
                RandomDataUtil.randomLogin(),
                RandomDataUtil.randomPassword(),
                "admin",
                RandomDataUtil.randomScreenName()
        );
        Response response = client.createPlayer(Editor.SUPERVISOR.getValue(), params);

        assertEquals(response.getStatusCode(), 200, "Expected 200");

        PlayerResponse actual = response.as(PlayerResponse.class);
        assertNotNull(actual.getId(), "id should be present");
        createdPlayerIds.add(actual.getId());

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(actual.getAge(), params.getAge(), "age");
        soft.assertEquals(actual.getGender(), params.getGender(), "gender");
        soft.assertEquals(actual.getLogin(), params.getLogin(), "login");
        soft.assertEquals(actual.getPassword(), params.getPassword(), "password");
        soft.assertEquals(actual.getRole(), params.getRole(), "role");
        soft.assertEquals(actual.getScreenName(), params.getScreenName(), "screenName");
        soft.assertAll();
    }

}
