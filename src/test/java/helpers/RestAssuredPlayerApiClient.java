package helpers;

import config.ApiConfig;
import config.ApiEndpoint;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.request.CreatePlayerParams;
import model.request.PlayerIdRequest;
import model.request.UpdatePlayerRequest;

import static org.testng.Assert.assertTrue;

public class RestAssuredPlayerApiClient implements IPlayerApiClient {

    private static final long MAX_RESPONSE_TIME_MS = 3000;

    private final RequestSpecification defaultSpec;

    public RestAssuredPlayerApiClient(ApiConfig config) {
        this.defaultSpec = RestAssured.given()
                .baseUri(config.getBaseUrl())
                .contentType(ContentType.JSON);
    }

    private static Response verifyResponseTime(Response response) {
        long timeMs = response.getTime();
        assertTrue(timeMs < MAX_RESPONSE_TIME_MS,
                "Response time should be under " + MAX_RESPONSE_TIME_MS + " ms, actual: " + timeMs + " ms");
        return response;
    }

    @Override
    public Response createPlayer(String editor, CreatePlayerParams params) {
        return verifyResponseTime(RestAssured.given(defaultSpec)
                .pathParam("editor", editor)
                .queryParams(params.toQueryMap())
                .get(ApiEndpoint.CREATE_PLAYER.getPath()));
    }

    @Override
    public Response deletePlayer(String editor, PlayerIdRequest body) {
        return verifyResponseTime(RestAssured.given(defaultSpec)
                .pathParam("editor", editor)
                .body(body)
                .delete(ApiEndpoint.DELETE_PLAYER.getPath()));
    }

    @Override
    public Response getPlayerByPlayerId(PlayerIdRequest body) {
        return verifyResponseTime(RestAssured.given(defaultSpec)
                .body(body)
                .post(ApiEndpoint.GET_PLAYER_BY_ID.getPath()));
    }

    @Override
    public Response getAllPlayers() {
        return verifyResponseTime(RestAssured.given(defaultSpec)
                .get(ApiEndpoint.GET_ALL_PLAYERS.getPath()));
    }

    @Override
    public Response updatePlayer(String editor, long userId, UpdatePlayerRequest body) {
        return verifyResponseTime(RestAssured.given(defaultSpec)
                .pathParam("editor", editor)
                .pathParam("userId", userId)
                .body(body)
                .patch(ApiEndpoint.UPDATE_PLAYER.getPath()));
    }
}
