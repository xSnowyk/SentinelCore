package ru.snowyk.sentinelCore.database;

public record DatabaseCredentials(
        String host,
        int port,
        String database,
        String username,
        String password,
        int maxPoolSize
) {
    public DatabaseCredentials {
        if (maxPoolSize <= 0) {
            maxPoolSize = 10;
        }
    }
}