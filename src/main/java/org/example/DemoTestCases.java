package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.example.conf.Settings;
import org.example.conf.UserSettings;
import org.example.connection.ApisConnection;
import org.example.connection.ConnectionProperty;

public class DemoTestCases {
    public void DemoTestCases() {}

    public Object getDemoTestCases(String settingsFile, ConnectionProperty cp) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Settings settings = new Settings(settingsFile);
        UserSettings userSettings;
        userSettings = settings.getUserSettings();
        ApisConnection apisConnection = new ApisConnection(cp);
        apisConnection.properties.setUsername(userSettings.getUserName());
        apisConnection.properties.setPassword(userSettings.getPassWord());
        apisConnection.setRequestAuthenticate("BasicAuthentication");
        apisConnection.setRequestProperty("Content-Type", "application/json");
        String json = apisConnection.GetApisResult();

        Map<String, Object> response = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        return response.get("value");
    }
}