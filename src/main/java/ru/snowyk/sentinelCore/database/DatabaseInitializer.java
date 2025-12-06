package ru.snowyk.sentinelCore.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private final DatabaseConnection database;

    public DatabaseInitializer(DatabaseConnection database) {
        this.database = database;
    }

    public void initTables() throws SQLException {
        try (Connection connection = database.getConnection();
             Statement statement = connection.createStatement()) {

            String reportsTable = """
                CREATE TABLE IF NOT EXISTS sentinel_reports (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    reporter_uuid VARCHAR(36) NOT NULL,
                    reported_uuid VARCHAR(36) NOT NULL,
                    reason TEXT NOT NULL,
                    created_at BIGINT NOT NULL,
                    status INT DEFAULT 0
                );
            """;

            String punishmentsTable = """
                CREATE TABLE IF NOT EXISTS sentinel_punishments (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    player_uuid VARCHAR(36) NOT NULL,
                    executor_uuid VARCHAR(36) NOT NULL,
                    type VARCHAR(16) NOT NULL,
                    reason TEXT NOT NULL,
                    created_at BIGINT NOT NULL,
                    duration BIGINT NOT NULL,
                    active TINYINT(1) DEFAULT 1
                );
            """;

            statement.execute(reportsTable);
            statement.execute(punishmentsTable);
        }
    }
}