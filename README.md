# Player Service API Tests

## Tech Stack: Java 21, Maven, TestNG, RestAssured, Jackson

**Config:** `src/main/resources/config.properties` — baseUrl, threadCount with default 3.

**Run:** `mvn test`

**Allure report:** `mvn test allure:serve` — to run tests and open the report in the browser.

**API endpoints**
- `createPlayer(editor, params)` — GET /player/create/{editor} query: age, gender, login, password, role, screenName
- `deletePlayer(editor, body)` — DELETE /player/delete/{editor} body {playerId}
- `getPlayerByPlayerId(body)` — POST /player/get body {playerId}
- `getAllPlayers()` — GET /player/get/all
- `updatePlayer(editor, userId, body)` — PATCH /player/update/{editor}/{userId} body: age, gender, login, password, screenName

**Editor** path param `{editor}`: use one of `admin`, `editor`, `user`.

**Gender** allowed: `male`, `female`.

**Structure:**
- `src/main/java/config` — ApiConfig (baseUrl, threadCount)
- `src/main/java/model` — POJO: CreatePlayerParams, PlayerIdRequest, UpdatePlayerRequest
- `src/main/resources` — config.properties
- `src/test/java/helpers` — API helpers (IPlayerApiClient, RestAssuredPlayerApiClient)
- `src/test/java/tests` — TestNG tests by endpoint: CreatePlayerTest, DeletePlayerTest, GetPlayerByPlayerIdTest, GetAllPlayersTest, UpdatePlayerTest
