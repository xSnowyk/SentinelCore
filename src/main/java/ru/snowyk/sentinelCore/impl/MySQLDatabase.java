package ru.snowyk.sentinelCore.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ru.snowyk.sentinelCore.database.DatabaseConnection;
import ru.snowyk.sentinelCore.database.DatabaseCredentials;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLDatabase implements DatabaseConnection {

    private final DatabaseCredentials credentials;
    private HikariDataSource dataSource;

    public MySQLDatabase(DatabaseCredentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public void connect() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + credentials.host() + ":" + credentials.port() + "/" + credentials.database());
        config.setUsername(credentials.username());
        config.setPassword(credentials.password());

        config.setMaximumPoolSize(credentials.maxPoolSize());
        config.setPoolName("Sentinel-Pool");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database is not connected! Call connect() first.");
        }
        return dataSource.getConnection();
    }
}