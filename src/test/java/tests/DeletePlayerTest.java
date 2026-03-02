package tests;

import io.restassured.response.Response;
import model.CreatePlayerParams;
import model.Editor;
import model.PlayerIdRequest;
import org.testng.annotations.Test;
import providers.DeletePlayerDataProviders;
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
        CreatePlayerParams createParams = new CreatePlayerParams(
                "30",
                "male",
                RandomDataUtil.randomLogin(),
                RandomDataUtil.randomPassword(),
                "admin",
                RandomDataUtil.randomScreenName()
        );
        Response createResponse = client.createPlayer(Editor.SUPERVISOR.getValue(), createParams);
        assertNotNull(createResponse);

        Long createdAdminId = createResponse.jsonPath().getObject("id", Long.class);
        assertNotNull(createdAdminId, "Expected created admin id");

        Response deleteResponse = client.deletePlayer(editor, new PlayerIdRequest(createdAdminId));
        assertEquals(deleteResponse.getStatusCode(), expectedStatus,
                "Editor=" + editor + " expected " + expectedStatus + " got " + deleteResponse.getStatusCode());

        if (expectedStatus == 403) {
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
        CreatePlayerParams createParams = new CreatePlayerParams(
                "28",
                "female",
                RandomDataUtil.randomLogin(),
                RandomDataUtil.randomPassword(),
                "user",
                RandomDataUtil.randomScreenName()
        );
        Response createResponse = client.createPlayer(Editor.SUPERVISOR.getValue(), createParams);
        assertNotNull(createResponse);
        assertEquals(createResponse.getStatusCode(), 200, "Expected create user status 200");

        Long createdUserId = createResponse.jsonPath().getObject("id", Long.class);
        assertNotNull(createdUserId, "Expected created user id");

        Response deleteResponse = client.deletePlayer(editor, new PlayerIdRequest(createdUserId));
        assertNotNull(deleteResponse);
        assertEquals(deleteResponse.getStatusCode(), expectedStatus,
                "Editor=" + editor + " expected " + expectedStatus + " got " + deleteResponse.getStatusCode());

        if (deleteResponse.getStatusCode() != 204) {
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
