package providers;

import model.enums.Editor;
import org.testng.annotations.DataProvider;

public final class DeletePlayerDataProviders {

    private DeletePlayerDataProviders() {
    }

    @DataProvider(name = "editorAndExpectedDeleteStatusForSupervisor")
    public static Object[][] editorAndExpectedDeleteStatusForSupervisor() {
        return new Object[][]{
                {Editor.ADMIN.getValue(), 403},
                {Editor.SUPERVISOR.getValue(), 403},
                {Editor.USER.getValue(), 403}
        };
    }

    @DataProvider(name = "editorAndExpectedDeleteStatusForCreatedAdmin")
    public static Object[][] editorAndExpectedDeleteStatusForCreatedAdmin() {
        return new Object[][]{
                {Editor.SUPERVISOR.getValue(), 204},
                {Editor.ADMIN.getValue(), 204},
                {Editor.USER.getValue(), 403}
        };
    }

    @DataProvider(name = "editorAndExpectedDeleteStatusForCreatedUser")
    public static Object[][] editorAndExpectedDeleteStatusForCreatedUser() {
        return new Object[][]{
                {Editor.SUPERVISOR.getValue(), 204},
                {Editor.ADMIN.getValue(), 204},
                {Editor.USER.getValue(), 204}
        };
    }
}
