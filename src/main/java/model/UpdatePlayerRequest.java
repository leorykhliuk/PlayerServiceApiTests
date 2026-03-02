package model;

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
    public void setAge(String age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getScreenName() { return screenName; }
    public void setScreenName(String screenName) { this.screenName = screenName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
