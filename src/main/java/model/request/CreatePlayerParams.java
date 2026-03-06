package model.request;

import java.util.LinkedHashMap;
import java.util.Map;

public class CreatePlayerParams {

    private String age;
    private String gender;
    private String login;
    private String password;
    private String role;
    private String screenName;

    public CreatePlayerParams() {
    }

    public CreatePlayerParams(String age, String gender, String login, String password, String role, String screenName) {
        this.age = age;
        this.gender = gender;
        this.login = login;
        this.password = password;
        this.role = role;
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
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getScreenName() { return screenName; }
    public void setScreenName(String screenName) { this.screenName = screenName; }

    public Map<String, String> toQueryMap() {
        Map<String, String> map = new LinkedHashMap<>();
        String[] keys = {"age", "gender", "login", "password", "role", "screenName"};
        String[] values = {age, gender, login, password, role, screenName};
        for (int i = 0; i < keys.length; i++) {
            if (values[i] != null) map.put(keys[i], values[i]);
        }
        return map;
    }
}
