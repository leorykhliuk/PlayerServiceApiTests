package helpers;

import model.CreatePlayerParams;
import model.PlayerIdRequest;
import model.UpdatePlayerRequest;
import io.restassured.response.Response;

public interface IPlayerApiClient {
    Response createPlayer(String editor, CreatePlayerParams params);
    Response deletePlayer(String editor, PlayerIdRequest body);
    Response getPlayerByPlayerId(PlayerIdRequest body);
    Response getAllPlayers();
    Response updatePlayer(String editor, long userId, UpdatePlayerRequest body);
}
