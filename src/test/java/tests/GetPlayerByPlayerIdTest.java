package tests;

import io.restassured.response.Response;
import model.CreatePlayerParams;
import model.Editor;
import model.PlayerIdRequest;
import model.PlayerResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.RandomDataUtil;

import static org.testng.Assert.*;

@Test(groups = "player-api")
public class GetPlayerByPlayerIdTest extends BaseApiTest {

    private long createdAdminId;
    private CreatePlayerParams createdAdminParams;
    private PlayerResponse expectedAdminResponse;

    @BeforeClass
    public void createAdminBeforeClass() {
        createdAdminParams = new CreatePlayerParams(
                "31",
                "male",
                RandomDataUtil.randomLogin(),
                RandomDataUtil.randomPassword(),
                "admin",
                RandomDataUtil.randomScreenName()
        );
        Response createResponse = client.createPlayer(Editor.SUPERVISOR.getValue(), createdAdminParams);
        assertNotNull(createResponse);
        assertEquals(createResponse.getStatusCode(), 200, "Expected create admin status 200");
        Long id = createResponse.jsonPath().getObject("id", Long.class);
        assertNotNull(id, "Expected created admin id");
        createdAdminId = id;
        expectedAdminResponse = new PlayerResponse(
                createdAdminId,
                createdAdminParams.getAge(),
                createdAdminParams.getGender(),
                createdAdminParams.getLogin(),
                createdAdminParams.getPassword(),
                createdAdminParams.getRole(),
                createdAdminParams.getScreenName()
        );
    }

    @AfterClass
    public void cleanUpCreatedAdmin() {
        if (createdAdminId > 0) {
            client.deletePlayer(Editor.SUPERVISOR.getValue(), new PlayerIdRequest(createdAdminId));
        }
    }

    @Test(description = "Get player by id returns the created admin data")
    public void getPlayerByIdReturnsCreatedAdminData() {
        Response getByIdResponse = client.getPlayerByPlayerId(new PlayerIdRequest(createdAdminId));
        assertNotNull(getByIdResponse);
        assertEquals(getByIdResponse.getStatusCode(), 200, "Expected get by id status 200");

        PlayerResponse actual = getByIdResponse.as(PlayerResponse.class);
        assertNotNull(actual, "Expected non-null player object");
        assertEquals(actual, expectedAdminResponse, "Player object mismatch");
    }

    // BUG related to the age, when age <= 30 everything is fine
    @Test(description = "Get player by id response time is less than 3 seconds")
    public void getPlayerByIdResponseTimeLessThan3Seconds() {
        Response getByIdResponse = client.getPlayerByPlayerId(new PlayerIdRequest(createdAdminId));

        assertEquals(getByIdResponse.getStatusCode(), 200, "Expected get by id status 200");

        long responseTimeMs = getByIdResponse.getTime();

        assertTrue(responseTimeMs < 3000,
                "Response time should be less than 3s, actual: " + responseTimeMs + " ms");
    }
}
