package model.request;

public class UpdatePlayerRequest {

    private String age;
    private String gender;
    private String login;
    private String password;
    private String screenName;
    private String role;

    public UpdatePlayerRequest() {
    }

    public UpdatePlayerRequest(String age, String gender, String login, String password, String screenName) {
        this.age = age;
        this.gender = gender;
        this.login = login;
        this.password = password;
        this.screenName = screenName;
    }

    public String getAge() { return age; }
    public UpdatePlayerRequest setAge(String age) { this.age = age; return this; }
    public String getGender() { return gender; }
    public UpdatePlayerRequest setGender(String gender) { this.gender = gender; return this; }
    public String getLogin() { return login; }
    public UpdatePlayerRequest setLogin(String login) { this.login = login; return this; }
    public String getPassword() { return password; }
    public UpdatePlayerRequest setPassword(String password) { this.password = password; return this; }
    public String getScreenName() { return screenName; }
    public UpdatePlayerRequest setScreenName(String screenName) { this.screenName = screenName; return this; }
    public String getRole() { return role; }
    public UpdatePlayerRequest setRole(String role) { this.role = role; return this; }
}
