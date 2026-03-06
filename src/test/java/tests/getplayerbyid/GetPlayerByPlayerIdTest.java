package tests.getplayerbyid;

import io.restassured.response.Response;
import model.enums.Editor;
import model.request.CreatePlayerParams;
import model.request.PlayerIdRequest;
import model.response.PlayerResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import tests.base.BaseApiTest;
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
        createdAdminId = createResponse.as(PlayerResponse.class).getId();
        assertNotNull(createdAdminId, "Expected created admin id");
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
}
