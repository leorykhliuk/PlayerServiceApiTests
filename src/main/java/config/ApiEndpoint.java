package config;

/**
 * API path templates for the Player service.
 * Path parameters (e.g. {editor}, {userId}) are filled by RestAssured pathParam().
 */
public enum ApiEndpoint {

    CREATE_PLAYER("/player/create/{editor}"),
    DELETE_PLAYER("/player/delete/{editor}"),
    GET_PLAYER_BY_ID("/player/get"),
    GET_ALL_PLAYERS("/player/get/all"),
    UPDATE_PLAYER("/player/update/{editor}/{userId}");

    private final String pathTemplate;

    ApiEndpoint(String pathTemplate) {
        this.pathTemplate = pathTemplate;
    }

    public String getPath() {
        return pathTemplate;
    }
}
