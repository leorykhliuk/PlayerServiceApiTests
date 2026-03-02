package tests;

import io.restassured.response.Response;
import model.CreatePlayerParams;
import model.Editor;
import model.PlayerIdRequest;
import model.PlayerResponse;
import model.UpdatePlayerRequest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import providers.UpdatePlayerDaraProvider;
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
            Long id = createResponse.jsonPath().getObject("id", Long.class);
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

        UpdatePlayerRequest body = new UpdatePlayerRequest();
        body.setAge("25");
        body.setGender("female");
        body.setLogin(RandomDataUtil.randomLogin());
        body.setPassword(RandomDataUtil.randomPassword());
        body.setScreenName(RandomDataUtil.randomScreenName());

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
        String actualPassword = getResponse.jsonPath().getObject("password", String.class);
        assertEquals(actualPassword, body.getPassword(), "GET response password should match updated value");
    }

    @Test(description = "For each (editor, target): editor tries to change target's role to another; target role must stay unchanged",
            dataProvider = "editorAndTargetRole",
            dataProviderClass = UpdatePlayerDaraProvider.class)
    public void roleRemainsUnchangedWhenAnyEditorTriesToChangeIt(Editor editor, Editor targetRole) {
        Long targetId = playerIdByRole.get(targetRole);
        assertNotNull(targetId, "Player with role " + targetRole + " should exist");
        String expectedRole = targetRole.getValue();

        UpdatePlayerRequest body = new UpdatePlayerRequest();
        body.setRole(targetRole.getRoleToTryForUpdateAttempt());
        Response updateResponse = client.updatePlayer(editor.getValue(), targetId, body);

        assertEquals(updateResponse.getStatusCode(), 200);

        String actualRole = updateResponse.jsonPath().getObject("role", String.class);
        assertEquals(actualRole, expectedRole,
                "Update response role must be unchanged (target " + targetRole + ", editor " + editor + ")");
    }

    //BUG - update shouldn't have been happened, but it gives to update age to be out of rules.
    @Test(description = "Update created user's age to 15 as user editor returns 403")
    public void updateUserAgeAsUserEditorReturns403() {
        Long userId = playerIdByRole.get(Editor.USER);

        UpdatePlayerRequest body = new UpdatePlayerRequest();
        body.setAge("15");
        Response response = client.updatePlayer(Editor.USER.getValue(), userId, body);

        assertEquals(response.getStatusCode(), 403, "User updating user's age should return 403");
    }
}
