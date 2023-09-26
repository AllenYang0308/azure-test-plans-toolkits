package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.example.conf.Settings;
import org.example.conf.UserSettings;
import org.example.connection.ConnectionProperty;
import org.example.connection.ApisConnection;

public class DemoApis {

    public DemoApis() {}

    public String getDemoApis(String settingsFile, ConnectionProperty cp) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Settings settings = new Settings(settingsFile);
        UserSettings userSettings;
        userSettings = settings.getUserSettings();
        ApisConnection apisConnection = new ApisConnection(cp);
        apisConnection.properties.setUsername(userSettings.getUserName());
        apisConnection.properties.setPassword(userSettings.getPassWord());
        apisConnection.setRequestProperty("Content-type", "application/json; charset=utf-8");
        apisConnection.setRequestAuthenticate("BasicAuthentication");
        return apisConnection.GetApisResult();
    }

    public String getDemoParamApis(String settingsFile, ConnectionProperty cp) throws IOException {
        Settings settings = new Settings(settingsFile);
        UserSettings userSettings;
        userSettings = settings.getUserSettings();
        ApisConnection apisConnection = new ApisConnection(cp);
        apisConnection.properties.setUsername(userSettings.getUserName());
        apisConnection.properties.setPassword(userSettings.getPassWord());
        apisConnection.setRequestProperty("Content-type", "application/json-patch+json; charset=utf-8");
        apisConnection.setRequestAuthenticate("BasicAuthentication");
        return apisConnection.GetApisResult();
    }
}