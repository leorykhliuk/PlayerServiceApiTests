package tests.createplayer;

import io.restassured.response.Response;
import model.enums.Editor;
import model.request.CreatePlayerParams;
import model.request.PlayerIdRequest;
import model.response.PlayerResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import providers.CreatePlayerDataProviders;
import tests.base.BaseApiTest;

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
            description = "Create player: assert status and, when 200, full response body matches request (any editor and player role)",
            dataProvider = "editorPlayerRoleAndExpectedStatus",
            dataProviderClass = CreatePlayerDataProviders.class
    )
    public void createPlayerWithEditorAndRoleReturnsExpectedStatusAndBodyWhenSuccess(String editor, String playerRole, int expectedStatus) {
        CreatePlayerParams params = new CreatePlayerParams(
                "25",
                "male",
                RandomDataUtil.randomLogin(),
                RandomDataUtil.randomPassword(),
                playerRole,
                RandomDataUtil.randomScreenName()
        );
        Response response = client.createPlayer(editor, params);

        assertNotNull(response, "Response should not be null");
        assertEquals(response.getStatusCode(), expectedStatus,
                "Editor=" + editor + ", role=" + playerRole + ": expected status " + expectedStatus);

        if (expectedStatus == 200) {
            PlayerResponse actual = response.as(PlayerResponse.class);
            assertNotNull(actual.getId(), "Response should contain created player id");
            createdPlayerIds.add(actual.getId());

            SoftAssert soft = new SoftAssert();
            soft.assertEquals(actual.getAge(), params.getAge(), "age");
            soft.assertEquals(actual.getGender(), params.getGender(), "gender");
            soft.assertEquals(actual.getLogin(), params.getLogin(), "login");
            soft.assertEquals(actual.getPassword(), params.getPassword(), "password");
            soft.assertEquals(actual.getRole(), params.getRole(), "role");
            soft.assertEquals(actual.getScreenName(), params.getScreenName(), "screenName");
            soft.assertAll();

            Response getResponse = client.getPlayerByPlayerId(new PlayerIdRequest(actual.getId()));
            assertEquals(getResponse.getStatusCode(), 200, "GET created player should return 200");
            PlayerResponse fromGet = getResponse.as(PlayerResponse.class);
            assertEquals(fromGet, actual, "GET response should match create response (player persisted)");
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
            PlayerResponse created = response.as(PlayerResponse.class);
            Long id = created.getId();
            if (id != null) {
                createdPlayerIds.add(id);
                Response getResponse = client.getPlayerByPlayerId(new PlayerIdRequest(id));
                assertEquals(getResponse.getStatusCode(), 200, "GET created player should return 200");
                assertEquals(getResponse.as(PlayerResponse.class).getAge(), age, "GET response age should match created");
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
            PlayerResponse created = response.as(PlayerResponse.class);
            Long id = created.getId();
            if (id != null) {
                createdPlayerIds.add(id);
                Response getResponse = client.getPlayerByPlayerId(new PlayerIdRequest(id));
                assertEquals(getResponse.getStatusCode(), 200, "GET created player should return 200");
                assertEquals(getResponse.as(PlayerResponse.class).getGender(), gender, "GET response gender should match created");
            }
        }
    }
}
