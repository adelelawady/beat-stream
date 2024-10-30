package com.konsol.beatstream.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration // Use in the production profile or any specific profile
@Profile("prod")
public class EmbeddedMongoConfig {

    private Process mongoProcess;

    @PostConstruct
    public void startMongoDB() {
        try {
            String startupPath = Paths.get("").toAbsolutePath().toString();
            String mongoDbPath = startupPath + "\\mongodb\\mongod"; // Path to your MongoDB installation
            String dbPath = startupPath + "\\mongodb\\data"; // Data directory for MongoDB
            String port = "27017"; // MongoDB port, replace with your provided port
            try {
                boolean enableMongo = Boolean.parseBoolean(
                    AppSettingsConfiguration.getSettings().getProperty("beatstream.settings.mongo.embedded.start", "true")
                );
                if (!enableMongo) {
                    return;
                }
                mongoDbPath = AppSettingsConfiguration.getSettings()
                    .getProperty("beatstream.settings.mongo.path", startupPath + "\\mongodb\\mongod");
                dbPath = AppSettingsConfiguration.getSettings()
                    .getProperty("beatstream.settings.mongo.data.path", startupPath + "\\mongodb\\data");
                port = String.valueOf(AppSettingsConfiguration.getSettings().getProperty("beatstream.settings.mongo.port", "27017"));
            } catch (Exception ignored) {}

            ProcessBuilder processBuilder = new ProcessBuilder(mongoDbPath, "--dbpath", dbPath, "--port", port);
            processBuilder.inheritIO(); // This will redirect the output to the console
            mongoProcess = processBuilder.start();

            System.out.println("MongoDB started on port: " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stopMongoDB() {
        if (mongoProcess != null) {
            mongoProcess.destroy();
            System.out.println("MongoDB stopped.");
        }
    }
}
