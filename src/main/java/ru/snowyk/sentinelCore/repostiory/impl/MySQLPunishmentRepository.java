package ru.snowyk.sentinelCore.repostiory.impl;

import ru.snowyk.sentinelCore.database.DatabaseConnection;
import ru.snowyk.sentinelCore.model.Punishment;
import ru.snowyk.sentinelCore.model.PunishmentType;
import ru.snowyk.sentinelCore.repostiory.PunishmentRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySQLPunishmentRepository implements PunishmentRepository {

    private final DatabaseConnection database;

    public MySQLPunishmentRepository(DatabaseConnection database) {
        this.database = database;
    }

    @Override
    public void save(Punishment punishment) {
        String sql = "INSERT INTO sentinel_punishments (player_uuid, executor_uuid, type, reason, created_at, duration, active) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, punishment.getPlayerUuid().toString());
            preparedStatement.setString(2, punishment.getExecutorUuid().toString());
            preparedStatement.setString(3, punishment.getType().name());
            preparedStatement.setString(4, punishment.getReason());
            preparedStatement.setLong(5, punishment.getCreatedAt());
            preparedStatement.setLong(6, punishment.getDuration());
            preparedStatement.setBoolean(7, punishment.isActive());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Punishment> findActiveByPlayer(UUID playerUuid) {
        List<Punishment> list = new ArrayList<>();
        String sql = "SELECT * FROM sentinel_punishments WHERE player_uuid = ? AND active = 1";

        try (Connection connection = database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, playerUuid.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapResultSetToPunishment(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void revoke(int punishmentId) {
        String sql = "UPDATE sentinel_punishments SET active = 0 WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, punishmentId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Punishment mapResultSetToPunishment(ResultSet resultSet) throws SQLException {
        return new Punishment(
                resultSet.getInt("id"),
                UUID.fromString(resultSet.getString("player_uuid")),
                UUID.fromString(resultSet.getString("executor_uuid")),
                PunishmentType.valueOf(resultSet.getString("type")),
                resultSet.getString("reason"),
                resultSet.getLong("created_at"),
                resultSet.getLong("duration"),
                resultSet.getBoolean("active")
        );
    }
}