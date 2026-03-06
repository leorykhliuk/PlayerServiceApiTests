package tests.updateplayer;

import io.restassured.response.Response;
import model.enums.Editor;
import model.request.CreatePlayerParams;
import model.request.PlayerIdRequest;
import model.request.UpdatePlayerRequest;
import model.response.PlayerResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import providers.UpdatePlayerDaraProvider;
import tests.base.BaseApiTest;
import utils.RandomDataUtil;

import java.util.EnumMap;
import java.util.Map;

import static org.testng.Assert.*;

@Test(groups = "player-api")
public class UpdatePlayerTest extends BaseApiTest {

    private static final long SUPERVISOR_ID = 1L;

    private Map<Editor, Long> playerIdByRole;

    @BeforeClass
    public void createPlayersPerRole() {
        //Note: Supervisor already exists in the DB
        playerIdByRole = new EnumMap<>(Editor.class);
        playerIdByRole.put(Editor.SUPERVISOR, SUPERVISOR_ID);
        for (Editor role : new Editor[]{Editor.ADMIN, Editor.USER}) {
            CreatePlayerParams params = new CreatePlayerParams(
                    "30",
                    "male",
                    RandomDataUtil.randomLogin(),
                    RandomDataUtil.randomPassword(),
                    role.getValue(),
                    RandomDataUtil.randomScreenName()
            );
            Response createResponse = client.createPlayer(Editor.SUPERVISOR.getValue(), params);
            Long id = createResponse.as(PlayerResponse.class).getId();
            if (id != null) {
                playerIdByRole.put(role, id);
            }
        }
    }

    @AfterClass
    public void cleanUpCreatedPlayers() {
        for (Editor role : new Editor[]{Editor.ADMIN, Editor.USER}) {
            Long id = playerIdByRole.get(role);
            if (id != null) {
                client.deletePlayer(Editor.SUPERVISOR.getValue(), new PlayerIdRequest(id));
            }
        }
    }

    @Test(description = "Each editor updates created user: assert response code then all fields (age, gender, login, password, screenName) match",
            dataProvider = "editors",
            dataProviderClass = UpdatePlayerDaraProvider.class)
    public void updateUserByEachEditorThenValidateResponseCodeAndAllFields(Editor editor) {
        Long userId = playerIdByRole.get(Editor.USER);
        assertNotNull(userId, "Created user should exist");

        UpdatePlayerRequest body = new UpdatePlayerRequest()
                .setAge("25")
                .setGender("female")
                .setLogin(RandomDataUtil.randomLogin())
                .setPassword(RandomDataUtil.randomPassword())
                .setScreenName(RandomDataUtil.randomScreenName());

        Response updateResponse = client.updatePlayer(editor.getValue(), userId, body);
        assertEquals(updateResponse.getStatusCode(), 200,
                "Update user as " + editor + " should return 200");

        PlayerResponse actualFromUpdate = updateResponse.as(PlayerResponse.class);
        PlayerResponse expectedFromUpdate = new PlayerResponse(
                userId,
                body.getAge(),
                body.getGender(),
                body.getLogin(),
                null,
                Editor.USER.getValue(),
                body.getScreenName()
        );
        assertEquals(actualFromUpdate, expectedFromUpdate, "Update response (no password) should match updated data");

        Response getResponse = client.getPlayerByPlayerId(new PlayerIdRequest(userId));
        assertEquals(getResponse.getStatusCode(), 200, "GET updated player should return 200");
        PlayerResponse fromGet = getResponse.as(PlayerResponse.class);
        assertEquals(fromGet.getAge(), body.getAge(), "GET age should match updated");
        assertEquals(fromGet.getGender(), body.getGender(), "GET gender should match updated");
        assertEquals(fromGet.getLogin(), body.getLogin(), "GET login should match updated");
        assertEquals(fromGet.getPassword(), body.getPassword(), "GET password should match updated");
        assertEquals(fromGet.getRole(), Editor.USER.getValue(), "GET role should be unchanged");
        assertEquals(fromGet.getScreenName(), body.getScreenName(), "GET screenName should match updated");
    }

    @Test(description = "For each (editor, target): editor tries to change target's role to another; target role must stay unchanged",
            dataProvider = "editorAndTargetRole",
            dataProviderClass = UpdatePlayerDaraProvider.class)
    public void roleRemainsUnchangedWhenAnyEditorTriesToChangeIt(Editor editor, Editor targetRole) {
        Long targetId = playerIdByRole.get(targetRole);
        assertNotNull(targetId, "Player with role " + targetRole + " should exist");
        String expectedRole = targetRole.getValue();

        UpdatePlayerRequest body = new UpdatePlayerRequest()
                .setRole(targetRole.getRoleToTryForUpdateAttempt());
        Response updateResponse = client.updatePlayer(editor.getValue(), targetId, body);

        assertEquals(updateResponse.getStatusCode(), 200);

        String actualRole = updateResponse.as(PlayerResponse.class).getRole();
        assertEquals(actualRole, expectedRole,
                "Update response role must be unchanged (target " + targetRole + ", editor " + editor + ")");

        Response getResponse = client.getPlayerByPlayerId(new PlayerIdRequest(targetId));
        assertEquals(getResponse.getStatusCode(), 200, "GET player after update should return 200");
        assertEquals(getResponse.as(PlayerResponse.class).getRole(), expectedRole,
                "GET response role should still be unchanged (persisted)");
    }

    //BUG - update shouldn't have been happened, but it gives to update age to be out of rules.
    @Test(description = "Update created user's age to 15 as user editor returns 403")
    public void updateUserAgeAsUserEditorReturns403() {
        Long userId = playerIdByRole.get(Editor.USER);

        UpdatePlayerRequest body = new UpdatePlayerRequest().setAge("15");
        Response response = client.updatePlayer(Editor.USER.getValue(), userId, body);

        assertEquals(response.getStatusCode(), 403, "User updating user's age should return 403");

        Response getResponse = client.getPlayerByPlayerId(new PlayerIdRequest(userId));
        assertEquals(getResponse.getStatusCode(), 200, "GET player should still return 200 after rejected update");
        assertEquals(getResponse.as(PlayerResponse.class).getAge(), "30", "GET age should be unchanged (update rejected)");
    }
}
