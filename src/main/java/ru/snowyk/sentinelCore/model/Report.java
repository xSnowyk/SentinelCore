package ru.snowyk.sentinelCore.model;

import java.util.UUID;

public class Report {

    private final int id;
    private final UUID reporterUuid;
    private final UUID reportedUuid;
    private final String reason;
    private final long createdAt;

    private ReportStatus status;

    public Report(int id, UUID reporterUuid, UUID reportedUuid, String reason, long createdAt, ReportStatus status) {
        this.id = id;
        this.reporterUuid = reporterUuid;
        this.reportedUuid = reportedUuid;
        this.reason = reason;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Report(UUID reporterUuid, UUID reportedUuid, String reason) {
        this(0, reporterUuid, reportedUuid, reason, System.currentTimeMillis(), ReportStatus.OPEN);
    }

    // Методы бизнес-логики
    public void markInProgress() {
        this.status = ReportStatus.IN_PROGRESS;
    }

    public void resolve() {
        this.status = ReportStatus.RESOLVED;
    }

    public void reject() {
        this.status = ReportStatus.REJECTED;
    }

    public int getId() {
        return id;
    }

    public UUID getReporterUuid() {
        return reporterUuid;
    }

    public UUID getReportedUuid() {
        return reportedUuid;
    }

    public String getReason() {
        return reason;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public ReportStatus getStatus() {
        return status;
    }
}