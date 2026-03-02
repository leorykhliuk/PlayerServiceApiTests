package model;

public enum Editor {

    ADMIN("admin"),
    SUPERVISOR("supervisor"),
    USER("user");

    private final String value;

    Editor(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getRoleToTryForUpdateAttempt() {
        switch (this) {
            case SUPERVISOR:
            case ADMIN:
                return "user";
            case USER:
                return "admin";
            default:
                return "user";
        }
    }
}
