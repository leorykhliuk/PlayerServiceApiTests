package model.response;

import java.util.List;

public class GetAllPlayersResponse {

    private List<PlayerResponse> players;

    public GetAllPlayersResponse() {
    }

    public List<PlayerResponse> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerResponse> players) {
        this.players = players;
    }
}
