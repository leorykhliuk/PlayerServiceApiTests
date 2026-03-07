package tests.deleteplayer;

import io.restassured.response.Response;
import model.enums.Editor;
import model.request.CreatePlayerParams;
import model.request.PlayerIdRequest;
import model.response.PlayerResponse;
import org.testng.annotations.Test;
import providers.DeletePlayerDataProviders;
import tests.base.BaseApiTest;
import utils.RandomDataUtil;

import static org.testng.Assert.*;

@Test(groups = "player-api")
public class DeletePlayerTest extends BaseApiTest {

    @Test(
            description = "Attempt to delete supervisor by any editor returns 403 and supervisor still exists",
            dataProvider = "editorAndExpectedDeleteStatusForSupervisor",
            dataProviderClass = DeletePlayerDataProviders.class
    )
    public void deleteProtectedSupervisorByAnyEditorReturnsExpectedStatusAndSupervisorRemains(String editor, int expectedStatus) {
        PlayerIdRequest body = new PlayerIdRequest(1L);
        Response deleteResponse = client.deletePlayer(editor, body);
        assertNotNull(deleteResponse);
        int deleteStatus = deleteResponse.getStatusCode();
        assertEquals(deleteStatus, expectedStatus, "Editor=" + editor + " expected " + expectedStatus + " got " + deleteStatus);

        Response getResponse = client.getPlayerByPlayerId(new PlayerIdRequest(1L));
        assertNotNull(getResponse);
        assertEquals(getResponse.getStatusCode(), 200, "Supervisor with id=1 should remain in system");
    }

    @Test(
            description = "Delete created admin by each editor returns expected status; only user role can't perform delete to admin",
            dataProvider = "editorAndExpectedDeleteStatusForCreatedAdmin",
            dataProviderClass = DeletePlayerDataProviders.class
    )
    public void deleteCreatedAdminByEachEditorReturnsExpectedStatus(String editor, int expectedStatus) {
        CreatePlayerParams createParams = new CreatePlayerParams()
                .setAge("30")
                .setGender("male")
                .setLogin(RandomDataUtil.randomLogin())
                .setPassword(RandomDataUtil.randomPassword())
                .setRole("admin")
                .setScreenName(RandomDataUtil.randomScreenName());
        Response createResponse = client.createPlayer(Editor.SUPERVISOR.getValue(), createParams);
        assertNotNull(createResponse);

        Long createdAdminId = createResponse.as(PlayerResponse.class).getId();
        assertNotNull(createdAdminId, "Expected created admin id");

        Response deleteResponse = client.deletePlayer(editor, new PlayerIdRequest(createdAdminId));
        assertEquals(deleteResponse.getStatusCode(), expectedStatus,
                "Editor=" + editor + " expected " + expectedStatus + " got " + deleteResponse.getStatusCode());

        if (expectedStatus == 204) {
            Response getResponse = client.getPlayerByPlayerId(new PlayerIdRequest(createdAdminId));
            assertTrue(getResponse.getStatusCode() != 200,
                    "GET after successful delete should not return 200 (player should be gone)");
        } else if (expectedStatus == 403) {
            Response getResponse = client.getPlayerByPlayerId(new PlayerIdRequest(createdAdminId));
            assertEquals(getResponse.getStatusCode(), 200, "GET should return 200 (admin still exists, delete was forbidden)");
            Response cleanupDelete = client.deletePlayer(Editor.SUPERVISOR.getValue(), new PlayerIdRequest(createdAdminId));
            assertNotNull(cleanupDelete);
            assertEquals(cleanupDelete.getStatusCode(), 204, "Supervisor cleanup should delete created admin");
        }
    }

    @Test(
            description = "Delete created regular user by each editor returns 204",
            dataProvider = "editorAndExpectedDeleteStatusForCreatedUser",
            dataProviderClass = DeletePlayerDataProviders.class
    )
    public void deleteCreatedRegularUserByEachEditorReturns204(String editor, int expectedStatus) {
        CreatePlayerParams createParams = new CreatePlayerParams()
                .setAge("28")
                .setGender("female")
                .setLogin(RandomDataUtil.randomLogin())
                .setPassword(RandomDataUtil.randomPassword())
                .setRole("user")
                .setScreenName(RandomDataUtil.randomScreenName());
        Response createResponse = client.createPlayer(Editor.SUPERVISOR.getValue(), createParams);
        assertNotNull(createResponse);
        assertEquals(createResponse.getStatusCode(), 200, "Expected create user status 200");

        Long createdUserId = createResponse.as(PlayerResponse.class).getId();
        assertNotNull(createdUserId, "Expected created user id");

        Response deleteResponse = client.deletePlayer(editor, new PlayerIdRequest(createdUserId));
        assertNotNull(deleteResponse);
        assertEquals(deleteResponse.getStatusCode(), expectedStatus,
                "Editor=" + editor + " expected " + expectedStatus + " got " + deleteResponse.getStatusCode());

        if (expectedStatus == 204) {
            Response getResponse = client.getPlayerByPlayerId(new PlayerIdRequest(createdUserId));
            assertTrue(getResponse.getStatusCode() != 200,
                    "GET after successful delete should not return 200 (player should be gone)");
        } else {
            client.deletePlayer(Editor.SUPERVISOR.getValue(), new PlayerIdRequest(createdUserId));
        }
    }

    @Test(description = "attempt to delete not existing user")
    public void deleteWithNotExistingId() {
        PlayerIdRequest body = new PlayerIdRequest(-999L);
        Response response = client.deletePlayer(Editor.SUPERVISOR.getValue(), body);
        assertNotNull(response);
        int code = response.getStatusCode();
        assertEquals(code, 403);
    }
}
