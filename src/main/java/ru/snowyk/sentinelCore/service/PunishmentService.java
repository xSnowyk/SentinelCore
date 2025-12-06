package ru.snowyk.sentinelCore.service;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.snowyk.sentinelCore.model.Punishment;
import ru.snowyk.sentinelCore.model.PunishmentType;
import ru.snowyk.sentinelCore.repostiory.PunishmentRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PunishmentService {

    private final Plugin plugin;
    private final PunishmentRepository repository;

    public PunishmentService(Plugin plugin, PunishmentRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
    }

    public CompletableFuture<Void> issuePunishment(UUID targetUuid, UUID execturoUuid, PunishmentType type, String reason, long duration) {
        Punishment punishment = new Punishment(targetUuid, execturoUuid, type, reason, duration);

        return CompletableFuture.runAsync(() -> {
            repository.save(punishment);
        }).thenRun(() -> {
            Bukkit.getScheduler().runTask(plugin, () -> applyPunishmentEffect(targetUuid, type, reason));
        });
    }

    public CompletableFuture<Punishment> getActiveBan(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> {
           List<Punishment> punishments = repository.findActiveByPlayer(playerUuid);

           return punishments.stream()
                   .filter(p -> p.getType() == PunishmentType.BAN)
                   .filter(Punishment::isEffective)
                   .findFirst()
                   .orElse(null);
        });
    }

    private void applyPunishmentEffect(UUID targetUuid, PunishmentType type, String reason) {
        Player player = Bukkit.getPlayer(targetUuid);
        if (player == null || !player.isOnline()) return;

        switch (type) {
            case KICK, BAN -> player.kick(formatKickMessage(type, reason));
            case MUTE -> player.sendMessage(Component.text("Вы были замучены! Причина " + reason, NamedTextColor.RED));
            case WARN -> player.sendMessage(Component.text("Вам выдано предупреждение! Причина: " + reason, NamedTextColor.YELLOW));
        }
    }

    private Component formatKickMessage(PunishmentType type, String reason) {
        return Component.text("Вас ", NamedTextColor.RED)
                .append(Component.text(
                        type == PunishmentType.BAN ? "забанили" : "кикнули",
                        NamedTextColor.RED
                ))
                .append(Component.text("!\n", NamedTextColor.RED))
                .append(Component.text("Причина: ", NamedTextColor.WHITE))
                .append(Component.text(reason, NamedTextColor.WHITE));
    }
}