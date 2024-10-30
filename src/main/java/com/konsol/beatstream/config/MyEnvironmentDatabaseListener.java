package com.konsol.beatstream.config;

import com.konsol.beatstream.service.audioPlugins.Spotify.SpotifyDownloader;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

@Profile("prod")
public class MyEnvironmentDatabaseListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(MyEnvironmentDatabaseListener.class);

    /**
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        LOG.debug("MyEnvironmentDatabaseListener started");
        String mongoPort = String.valueOf(AppSettingsConfiguration.getSettings().getProperty("beatstream.settings.mongo.port", "27017"));
        String serverPort = String.valueOf(AppSettingsConfiguration.getSettings().getProperty("beatstream.settings.server.port", "8080"));

        ConfigurableEnvironment env = event.getEnvironment();
        // Set properties programmatically
        Map<String, Object> myMap = new HashMap<>();
        myMap.put("server.port", serverPort);
        myMap.put("spring.data.mongodb.uri", "mongodb://localhost:" + mongoPort + "/beatStream");
        env.getPropertySources().addFirst(new MapPropertySource("CustomPorts", myMap));
    }
}
