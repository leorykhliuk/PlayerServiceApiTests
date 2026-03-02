package config;

import java.io.InputStream;
import java.util.Properties;

public class ApiConfig {

    private final String baseUrl;
    private final int threadCount;

    public ApiConfig() {
        Properties p = new Properties();
        try (InputStream in = ApiConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) p.load(in);
        } catch (Exception ignored) {
        }
        baseUrl = get("baseUrl", p);
        threadCount = getInt("threadCount", p);
    }

    private static String get(String key, Properties p) {
        String v = System.getProperty(key);
        if (v != null && !v.isEmpty()) return v.trim();
        v = p.getProperty(key);
        if (v != null && !v.isEmpty()) return v.trim();
        throw new IllegalStateException("Missing " + key + ". Set in config.properties or -D" + key + "=...");
    }

    private static int getInt(String key, Properties p) {
        String v = get(key, p);
        try { return Integer.parseInt(v.trim()); } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid " + key + "=" + v + ". Set in config.properties or -D" + key + "=...");
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }
    public int getThreadCount() {
        return threadCount;
    }
}
