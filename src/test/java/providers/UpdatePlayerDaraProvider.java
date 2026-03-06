package providers;

import model.enums.Editor;
import org.testng.annotations.DataProvider;

public class UpdatePlayerDaraProvider {
    private UpdatePlayerDaraProvider() {
    }

    @DataProvider(name = "editors")
    public static Object[][] editors() {
        return new Object[][]{
                {Editor.SUPERVISOR},
                {Editor.ADMIN},
                {Editor.USER}
        };
    }

    @DataProvider(name = "editorAndTargetRole")
    public static Object[][] editorAndTargetRole() {
        return new Object[][]{
                {Editor.SUPERVISOR, Editor.SUPERVISOR},
                {Editor.SUPERVISOR, Editor.ADMIN},
                {Editor.SUPERVISOR, Editor.USER},
                {Editor.ADMIN, Editor.SUPERVISOR},
                {Editor.ADMIN, Editor.ADMIN},
                {Editor.ADMIN, Editor.USER},
                {Editor.USER, Editor.SUPERVISOR},
                {Editor.USER, Editor.ADMIN},
                {Editor.USER, Editor.USER}
        };
    }
}
