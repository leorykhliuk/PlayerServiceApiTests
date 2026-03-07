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

    public String getAge() { return age; }
    public CreatePlayerParams setAge(String age) { this.age = age; return this; }
    public String getGender() { return gender; }
    public CreatePlayerParams setGender(String gender) { this.gender = gender; return this; }
    public String getLogin() { return login; }
    public CreatePlayerParams setLogin(String login) { this.login = login; return this; }
    public String getPassword() { return password; }
    public CreatePlayerParams setPassword(String password) { this.password = password; return this; }
    public String getRole() { return role; }
    public CreatePlayerParams setRole(String role) { this.role = role; return this; }
    public String getScreenName() { return screenName; }
    public CreatePlayerParams setScreenName(String screenName) { this.screenName = screenName; return this; }

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
