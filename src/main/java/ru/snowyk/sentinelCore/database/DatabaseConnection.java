package ru.snowyk.sentinelCore.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnection {

    void connect();

    void disconnect();

    Connection getConnection() throws SQLException;
}