package ru.snowyk.sentinelCore.repostiory.impl;

import ru.snowyk.sentinelCore.database.DatabaseConnection;
import ru.snowyk.sentinelCore.model.Report;
import ru.snowyk.sentinelCore.model.ReportStatus;
import ru.snowyk.sentinelCore.repostiory.ReportRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MySQLReportRepository implements ReportRepository {

    private final DatabaseConnection database;

    public MySQLReportRepository(DatabaseConnection database) {
        this.database = database;
    }

    @Override
    public void save(Report report) {
        String sql = "INSERT INTO sentinel_reports (reporter_uuid, reported_uuid, reason, created_at, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, report.getReporterUuid().toString());
            preparedStatement.setString(2, report.getReportedUuid().toString());
            preparedStatement.setString(3, report.getReason());
            preparedStatement.setLong(4, report.getCreatedAt());
            preparedStatement.setInt(5, report.getStatus().ordinal());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Report> findByStatus(ReportStatus status, int limit) {
        List<Report> list = new ArrayList<>();
        String sql = "SELECT * FROM sentinel_reports WHERE status = ? ORDER BY created_at ASC LIMIT ?";

        try (Connection connection = database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, status.ordinal());
            preparedStatement.setInt(2, limit);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapResultSetToReport(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Optional<Report> findById(int id) {
        String sql = "SELECT * FROM sentinel_reports WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToReport(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void updateStatus(int id, ReportStatus newStatus) {
        String sql = "UPDATE sentinel_reports SET status = ? WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, newStatus.ordinal());
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Report mapResultSetToReport(ResultSet resultSet) throws SQLException {
        return new Report(
                resultSet.getInt("id"),
                UUID.fromString(resultSet.getString("reporter_uuid")),
                UUID.fromString(resultSet.getString("reported_uuid")),
                resultSet.getString("reason"),
                resultSet.getLong("created_at"),
                ReportStatus.values()[resultSet.getInt("status")]
        );
    }
}