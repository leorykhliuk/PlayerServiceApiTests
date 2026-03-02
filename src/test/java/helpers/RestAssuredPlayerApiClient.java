package helpers;

import config.ApiConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.CreatePlayerParams;
import model.PlayerIdRequest;
import model.UpdatePlayerRequest;

public class RestAssuredPlayerApiClient implements IPlayerApiClient {

    public RestAssuredPlayerApiClient(ApiConfig config) {
        RestAssured.baseURI = config.getBaseUrl();
    }

    @Override
    public Response createPlayer(String editor, CreatePlayerParams params) {
        return RestAssured.given()
                .pathParam("editor", editor)
                .queryParams(params.toQueryMap())
                .get("/player/create/{editor}");
    }

    @Override
    public Response deletePlayer(String editor, PlayerIdRequest body) {
        return RestAssured.given()
                .pathParam("editor", editor)
                .contentType(ContentType.JSON)
                .body(body)
                .delete("/player/delete/{editor}");
    }

    @Override
    public Response getPlayerByPlayerId(PlayerIdRequest body) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("/player/get");
    }

    @Override
    public Response getAllPlayers() {
        return RestAssured.given()
                .get("/player/get/all");
    }

    @Override
    public Response updatePlayer(String editor, long userId, UpdatePlayerRequest body) {
        return RestAssured.given()
                .pathParam("editor", editor)
                .pathParam("userId", userId)
                .contentType(ContentType.JSON)
                .body(body)
                .patch("/player/update/{editor}/{userId}");
    }
}
