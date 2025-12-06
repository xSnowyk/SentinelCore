package ru.snowyk.sentinelCore.repostiory;

import ru.snowyk.sentinelCore.model.Report;
import ru.snowyk.sentinelCore.model.ReportStatus;

import java.util.List;
import java.util.Optional;

public interface ReportRepository {

    void save(Report report);

    List<Report> findByStatus(ReportStatus status, int limit);

    Optional<Report> findById(int id);

    void updateStatus(int id, ReportStatus newStatus);
}