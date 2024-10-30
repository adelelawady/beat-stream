package com.konsol.beatstream.config;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Order(0)
public class AppSettingsConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(AppSettingsConfiguration.class);

    @PostConstruct
    public void runAppSettingsConfiguration() {
        Properties properties = new Properties();
        File file = new File("settings.properties"); // Ensure this file path is correct

        if (file.exists()) {
            LOG.debug("Reading AppSettingsConfiguration");
            return;
        } else {
            LOG.debug("Writing AppSettingsConfiguration");
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // Load the existing properties if needed
        try (FileInputStream in = new FileInputStream(file)) {
            properties.load(in);
        } catch (IOException e) {
            System.out.println("Could not load existing properties: " + e.getMessage());
        }
        String startupPath = Paths.get("").toAbsolutePath().toString();

        // Add or update properties
        properties.setProperty("beatstream.settings.socket.task.log", "false"); //
        properties.setProperty("beatstream.settings.socket.task.notify", "true"); //
        properties.setProperty("beatstream.settings.mongo.embedded.start", "true"); //
        properties.setProperty("beatstream.settings.mongo.port", "27017"); //
        properties.setProperty("beatstream.settings.server.port", "8080"); //
        properties.setProperty("beatstream.settings.mongo.path", startupPath + "\\mongodb\\mongod"); //
        properties.setProperty("beatstream.settings.mongo.data.path", startupPath + "\\mongodb\\data"); //
        properties.setProperty("beatstream.settings.plugins.yt-dl_path", startupPath + "\\plugins\\yt-dlp.exe"); //
        properties.setProperty("beatstream.settings.plugins.ffmpeg_path", startupPath + "\\plugins\\ffmpeg.exe"); //
        properties.setProperty("beatstream.settings.plugins.chromedriver_path", startupPath + "\\plugins\\chromedriver.exe"); //
        properties.setProperty("beatstream.settings.download.playlist.enabled", "true");
        properties.setProperty("beatstream.settings.download.failed.retry.count", "3");
        properties.setProperty("beatstream.settings.download.failed.retry.schedule.time", "10");
        properties.setProperty("beatstream.settings.youtube.duplicated.enabled", "false");
        properties.setProperty("beatstream.settings.soundcloud.duplicated.enabled", "false");
        properties.setProperty("beatstream.settings.spotify.duplicated.enabled", "false");

        // Save the properties back to the file
        try (FileOutputStream out = new FileOutputStream(file)) {
            properties.store(out, "Updated application settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties getSettings() {
        Properties properties = new Properties();
        File file = new File("settings.properties"); // Ensure this file path is correct

        // Load the existing properties if needed
        try (FileInputStream in = new FileInputStream(file)) {
            properties.load(in);
        } catch (IOException e) {
            System.out.println("Could not load existing properties: " + e.getMessage());
        }
        return properties;
    }
}
