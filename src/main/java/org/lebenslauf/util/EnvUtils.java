package org.lebenslauf.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EnvUtils {
    public static Map<String, String> envVariables = null;

    public static synchronized void loadEnvVariables(String path) {
        if (envVariables != null) {
            return;
        }

        envVariables = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) { // comment
                    continue;
                }

                if (!line.contains("=")) {
                    continue;
                }

                int indexEquals = line.indexOf("=");
                String key = line.substring(0, indexEquals).trim();
                String value = line.substring(indexEquals + 1).trim();
                envVariables.put(key, value);
            }
        } catch (IOException e) {
            LogUtils.logError(e, "Error reading environment variables file: " + e.getMessage());
        }
    }

    public static String getEnv(String key) {
        if (envVariables == null) {
            loadEnvVariables(".env");
        }

        return envVariables.get(key);
    }
}
