package ru.snowyk.sentinelCore;

import org.bukkit.plugin.java.JavaPlugin;
import ru.snowyk.sentinelCore.database.DatabaseConnection;
import ru.snowyk.sentinelCore.database.DatabaseCredentials;
import ru.snowyk.sentinelCore.database.DatabaseInitializer;
import ru.snowyk.sentinelCore.impl.MySQLDatabase;

import java.sql.SQLException;

public final class SentinelCore extends JavaPlugin {

    private DatabaseConnection database;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        DatabaseCredentials credentials = new DatabaseCredentials(
                "localhost",
                3306,
                "sentinel",
                "root",
                "password",
                10
        );

        this.database = new MySQLDatabase(credentials);

        try {
            this.database.connect();
            getLogger().info("Database connected successfully!");

            DatabaseInitializer initializer = new DatabaseInitializer(database);
            initializer.initTables();
            getLogger().info("Database tables initialized.");

        } catch (SQLException e) {
            getLogger().severe("Failed to connect to database! Disabling plugin...");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    @Override
    public void onDisable() {
        if (database != null) {
            database.disconnect();
            getLogger().info("Database connection closed.");
        }
    }

    public DatabaseConnection getDatabase() {
        return database;
    }
}