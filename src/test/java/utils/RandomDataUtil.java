package utils;

import java.util.UUID;

public final class RandomDataUtil {

    private RandomDataUtil() {
    }

    public static String randomLogin() {
        return "user_" + token();
    }

    public static String randomScreenName() {
        return "screen_" + token();
    }

    public static String randomPassword() {
        return randomPassword(10);
    }

    public static String randomPassword(int length) {
        if (length < 7 || length > 15) {
            throw new IllegalArgumentException("Password length must be from 7 to 15");
        }
        String value = token() + token();
        return value.substring(0, length);
    }

    private static String token() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
