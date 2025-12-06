package ru.snowyk.sentinelCore.service;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import ru.snowyk.sentinelCore.model.Report;
import ru.snowyk.sentinelCore.model.ReportStatus;
import ru.snowyk.sentinelCore.repostiory.ReportRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ReportService {

    private final Plugin plugin;
    private final ReportRepository repository;

    public ReportService(Plugin plugin, ReportRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
    }

    public CompletableFuture<Void> createReport(UUID reporter, UUID reported, String reason) {
        Report report = new Report(reporter, reported, reason);

        return CompletableFuture.runAsync(() -> {
            repository.save(report);
        }).thenRun(() -> {
            Bukkit.getScheduler().runTask(plugin, () -> notifyAdmins(report));
        });
    }

    public CompletableFuture<List<Report>> getOpenReports() {
        return CompletableFuture.supplyAsync(() ->
            repository.findByStatus(ReportStatus.OPEN, 54)
        );
    }

    public CompletableFuture<Void> resolveReport(int reportId, boolean accepted) {
        return CompletableFuture.runAsync(() -> {
            ReportStatus newStatus = accepted ? ReportStatus.RESOLVED : ReportStatus.REJECTED;
            repository.updateStatus(reportId, newStatus);
        });
    }

    private void notifyAdmins(Report report) {
        Component msg = Component.text("[", NamedTextColor.DARK_GRAY)
                .append(Component.text("Report", NamedTextColor.RED))
                .append(Component.text("]", NamedTextColor.DARK_GRAY))
                .append(Component.text(" Игрок ", NamedTextColor.WHITE))
                .append(Component.text(Bukkit.getOfflinePlayer(report.getReporterUuid()).getName(), NamedTextColor.YELLOW))
                .append(Component.text(" пожаловался на ", NamedTextColor.WHITE))
                .append(Component.text(Bukkit.getOfflinePlayer(report.getReportedUuid()).getName(), NamedTextColor.RED))
                .append(Component.text(". Причина: ", NamedTextColor.WHITE))
                .append(Component.text(report.getReason(), NamedTextColor.GRAY));

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("sentinel.admin"))
                .forEach(p -> p.sendMessage(msg));
    }
}