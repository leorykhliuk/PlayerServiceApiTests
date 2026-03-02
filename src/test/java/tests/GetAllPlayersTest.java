package tests;

import io.restassured.response.Response;
import model.CreatePlayerParams;
import model.Editor;
import model.PlayerIdRequest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
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
                "MALE",
                RandomDataUtil.randomLogin(),
                RandomDataUtil.randomPassword(),
                "user",
                SHARED_SCREEN_NAME
        );

        CreatePlayerParams second = new CreatePlayerParams(
                "30",
                "non-binary",
                RandomDataUtil.randomLogin(),
                RandomDataUtil.randomPassword(),
                "user",
                SHARED_SCREEN_NAME
        );

        for (CreatePlayerParams params : List.of(first, second)) {
            Response createResponse = client.createPlayer(Editor.SUPERVISOR.getValue(), params);
            createdPlayerIds.add(createResponse.jsonPath().getObject("id", Long.class));
        }
    }

    @AfterClass
    public void cleanUpCreatedPlayers() {
        for (Long id : createdPlayerIds) {
            client.deletePlayer(Editor.SUPERVISOR.getValue(), new PlayerIdRequest(id));
        }
    }

    @Test(description = "Get all players returns non-empty list and response time is under 3s")
    public void getAllPlayersReturnsNonEmptyListAndResponseTimeUnder3Seconds() {
        Response response = client.getAllPlayers();

        assertEquals(response.getStatusCode(), 200, "Expected get all players status 200");

        List<?> players = response.jsonPath().getList("players");

        assertFalse(players.isEmpty(), "Players list should not be empty");

        long responseTimeMs = response.getTime();

        assertTrue(responseTimeMs < 3000,
                "Response time should be less than 3s, actual: " + responseTimeMs + " ms");
    }

    @Test(description = "All screenNames are unique: Set of screenNames has same size as players list")
    public void allScreenNamesAreUniqueSetSizeEqualsPlayerCount() {
        Response response = client.getAllPlayers();

        List<?> players = response.jsonPath().getList("players");
        assertFalse(players.isEmpty(), "Players list should not be empty");

        List<String> screenNames = response.jsonPath().getList("players.screenName", String.class);
        Set<String> uniqueScreenNames = new HashSet<>(screenNames);

        assertEquals(uniqueScreenNames.size(), players.size(),
                "Same size means all screenNames are unique");
    }

    @Test(description = "Every player has gender only 'male' or 'female'; any other value fails. Case sensitive")
    public void allPlayersHaveGenderMaleOrFemaleOnly() {
        Response response = client.getAllPlayers();

        List<String> genders = response.jsonPath().getList("players.gender", String.class);
        assertNotNull(genders, "Genders list should not be null");

        List<String> invalid = genders.stream()
                .filter(gender -> gender == null || (!"male".equals(gender) && !"female".equals(gender)))
                .distinct()
                .collect(Collectors.toList());

        assertTrue(invalid.isEmpty(),
                "Gender must be only 'male' or 'female'. Invalid values: " + invalid);
    }
}
