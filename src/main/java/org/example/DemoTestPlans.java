package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import org.example.conf.Settings;
import org.example.conf.UserSettings;
import org.example.connection.ApisConnection;
import org.example.connection.ConnectionProperty;

public class DemoTestPlans {

    public DemoTestPlans() {}

    public Map<String, Object> getDemoTestPlans(String settingsFile, ConnectionProperty cp) throws IOException {
        Map<String, Object> response = new HashMap<>();
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

        Map<String, Object> map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
        List<Map<String, Object>> items = (List<Map<String, Object>>) map.get("value");
        for (Map<String, Object> item: items) {
            response.put("TestPlanId", item.get("id"));
            response.put("TestPlanName", item.get("name"));
            Map<String, Object> rootSuite = (Map<String, Object>) item.get("rootSuite");
            response.put("TestPlanRootSuiteId", rootSuite.get("id"));
        }
        return response;
    }
}
