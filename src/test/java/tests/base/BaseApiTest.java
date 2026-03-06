package tests.base;

import config.ApiConfig;
import helpers.IPlayerApiClient;
import helpers.RestAssuredPlayerApiClient;
import org.testng.annotations.BeforeSuite;

public abstract class BaseApiTest {

    protected static IPlayerApiClient client;

    @BeforeSuite
    public void setUpSuite() {
        client = new RestAssuredPlayerApiClient(new ApiConfig());
    }
}
