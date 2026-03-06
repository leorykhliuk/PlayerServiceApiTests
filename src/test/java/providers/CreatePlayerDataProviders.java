package providers;

import model.enums.Editor;
import org.testng.annotations.DataProvider;

public final class CreatePlayerDataProviders {

    private CreatePlayerDataProviders() {
    }

    /**
     * Editor (who creates), player role (role of created player), expected HTTP status.
     * When status is 200, test also verifies full response body matches request.
     */
    @DataProvider(name = "editorPlayerRoleAndExpectedStatus")
    public static Object[][] editorPlayerRoleAndExpectedStatus() {
        return new Object[][]{
                {Editor.SUPERVISOR.getValue(), "user", 200},
                {Editor.SUPERVISOR.getValue(), "admin", 200},
                {Editor.ADMIN.getValue(), "user", 200},
                {Editor.ADMIN.getValue(), "admin", 200},
                {Editor.USER.getValue(), "user", 403}
        };
    }

    @DataProvider(name = "ageBoundaryAndExpectedStatus")
    public static Object[][] ageBoundaryAndExpectedStatus() {
        return new Object[][]{
                {"30", 200},
                {"15", 400},
                {"16", 400},
                {"60", 400},
                {"61", 400}
        };
    }

    @DataProvider(name = "genderAndExpectedStatus")
    public static Object[][] genderAndExpectedStatus() {
        return new Object[][]{
                {"male", 200},
                {"female", 200},
                {"non-binary", 400}
        };
    }
}
