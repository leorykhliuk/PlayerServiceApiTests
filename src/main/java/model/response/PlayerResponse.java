package model.response;

import java.util.Objects;

public class PlayerResponse {

    private Long id;
    private String age;
    private String gender;
    private String login;
    private String password;
    private String role;
    private String screenName;

    public PlayerResponse() {
    }

    public PlayerResponse(Long id, String age, String gender, String login, String password, String role, String screenName) {
        this.id = id;
        this.age = age;
        this.gender = gender;
        this.login = login;
        this.password = password;
        this.role = role;
        this.screenName = screenName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PlayerResponse that)) return false;
        return Objects.equals(id, that.id)
                && Objects.equals(age, that.age)
                && Objects.equals(gender, that.gender)
                && Objects.equals(login, that.login)
                && Objects.equals(password, that.password)
                && Objects.equals(role, that.role)
                && Objects.equals(screenName, that.screenName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, age, gender, login, password, role, screenName);
    }

    @Override
    public String toString() {
        return "PlayerResponse{" +
                "id=" + id +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", screenName='" + screenName + '\'' +
                '}';
    }
}
