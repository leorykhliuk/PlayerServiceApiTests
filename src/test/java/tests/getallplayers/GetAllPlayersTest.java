package tests.getallplayers;

import io.restassured.response.Response;
import model.enums.Editor;
import model.request.CreatePlayerParams;
import model.request.PlayerIdRequest;
import model.response.GetAllPlayersResponse;
import model.response.PlayerResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import tests.base.BaseApiTest;
import utils.RandomDataUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.testng.Assert.*;

@Test(groups = "player-api")
public class GetAllPlayersTest extends BaseApiTest {

    private static final String SHARED_SCREEN_NAME = "dup_screen_" + RandomDataUtil.randomLogin();
    private final List<Long> createdPlayerIds = new ArrayList<>();

    @BeforeClass
    public void createTwoPlayersWithSameScreenName() {
        CreatePlayerParams first = new CreatePlayerParams(
                "30",
                "male",
                RandomDataUtil.randomLogin(),
                RandomDataUtil.randomPassword(),
                "user",
                SHARED_SCREEN_NAME
        );

        CreatePlayerParams second = new CreatePlayerParams(
                "30",
                "female",
                RandomDataUtil.randomLogin(),
                RandomDataUtil.randomPassword(),
                "user",
                SHARED_SCREEN_NAME
        );

        for (CreatePlayerParams params : List.of(first, second)) {
            Response createResponse = client.createPlayer(Editor.SUPERVISOR.getValue(), params);
            createdPlayerIds.add(createResponse.as(PlayerResponse.class).getId());
        }
    }

    @AfterClass
    public void cleanUpCreatedPlayers() {
        for (Long id : createdPlayerIds) {
            client.deletePlayer(Editor.SUPERVISOR.getValue(), new PlayerIdRequest(id));
        }
    }

    @Test(description = "Get all players returns 200 and non-empty list")
    public void getAllPlayersReturnsNonEmptyList() {
        Response response = client.getAllPlayers();

        assertEquals(response.getStatusCode(), 200, "Expected get all players status 200");

        List<PlayerResponse> players = response.as(GetAllPlayersResponse.class).getPlayers();
        assertNotNull(players, "Players list should not be null");
        assertFalse(players.isEmpty(), "Players list should not be empty");
    }

    @Test(description = "All screenNames are unique: Set of screenNames has same size as players list")
    public void allScreenNamesAreUniqueSetSizeEqualsPlayerCount() {
        Response response = client.getAllPlayers();

        List<PlayerResponse> players = response.as(GetAllPlayersResponse.class).getPlayers();
        assertNotNull(players);
        assertFalse(players.isEmpty(), "Players list should not be empty");

        List<String> screenNames = players.stream().map(PlayerResponse::getScreenName).collect(Collectors.toList());
        Set<String> uniqueScreenNames = new HashSet<>(screenNames);

        assertEquals(uniqueScreenNames.size(), players.size(),
                "Same size means all screenNames are unique");
    }

    @Test(description = "Every player has gender only 'male' or 'female'; any other value fails. Case sensitive")
    public void allPlayersHaveGenderMaleOrFemaleOnly() {
        Response response = client.getAllPlayers();

        List<PlayerResponse> players = response.as(GetAllPlayersResponse.class).getPlayers();
        assertNotNull(players, "Players list should not be null");

        List<String> invalid = players.stream()
                .map(PlayerResponse::getGender)
                .filter(gender -> gender == null || (!"male".equals(gender) && !"female".equals(gender)))
                .distinct()
                .collect(Collectors.toList());

        assertTrue(invalid.isEmpty(),
                "Gender must be only 'male' or 'female'. Invalid values: " + invalid);
    }
}
