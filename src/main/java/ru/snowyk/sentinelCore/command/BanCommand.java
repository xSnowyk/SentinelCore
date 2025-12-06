package ru.snowyk.sentinelCore.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.snowyk.sentinelCore.model.PunishmentType;
import ru.snowyk.sentinelCore.service.PunishmentService;

import java.util.Arrays;
import java.util.UUID;

public class BanCommand implements CommandExecutor {

    private final PunishmentService service;

    public BanCommand(PunishmentService service) {
        this.service = service;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission("sentinel.ban")) {
            sender.sendMessage(Component.text("У вас нет прав!", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("Использование: /ban <ник> <причина>", NamedTextColor.RED));
            return true;
        }

        String targetName = args[0];
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        UUID targetUuid = target.getUniqueId();

        UUID executorUuid = (sender instanceof Player player) ? player.getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000");

        service.issuePunishment(targetUuid, executorUuid, PunishmentType.BAN, reason, -1)
                .thenAccept(v -> {
                   sender.sendMessage(Component.text("Игрок ", NamedTextColor.GREEN)
                           .append(Component.text(targetName, NamedTextColor.GREEN))
                           .append(Component.text(" был успешно забанен.", NamedTextColor.GREEN)));
                });

        return true;
    }
}