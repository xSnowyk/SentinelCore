package ru.snowyk.sentinelCore.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ru.snowyk.sentinelCore.model.Report;
import ru.snowyk.sentinelCore.service.ReportService;

import java.util.Arrays;
import java.util.List;

public class ReportCommand implements CommandExecutor {

    private final ReportService service;

    public ReportCommand(ReportService service) {
        this.service = service;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только для игроков.");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("view")) {
            if (!player.hasPermission("sentinel.admin")) return true;
            openReportGui(player);
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(Component.text("Использование: /report <ник> <причина>", NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Component.text("Игрок не найден онлайн.", NamedTextColor.RED));
            return true;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        service.createReport(player.getUniqueId(), target.getUniqueId(), reason)
                .thenRun(() -> player.sendMessage(Component.text("Жалоба отправлена! Админы рассмотрят её.", NamedTextColor.GREEN)));

        return true;
    }

    private void openReportGui(Player admin) {
        service.getOpenReports().thenAccept(reports -> {
            Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("SentinelCore"), () -> {
                Inventory gui = Bukkit.createInventory(null, 54, Component.text("Активные жалобы"));

                for (Report report : reports) {
                    ItemStack item = new ItemStack(Material.PAPER);
                    ItemMeta meta = item.getItemMeta();
                    String reportedName = Bukkit.getOfflinePlayer(report.getReportedUuid()).getName();

                    meta.displayName(Component.text("Жалоба на " + reportedName, NamedTextColor.RED));
                    meta.lore(List.of(
                            Component.text("Причина: ", NamedTextColor.GRAY)
                                    .append(Component.text(report.getReason(), NamedTextColor.WHITE))
                                    .append(Component.text("Статус: ", NamedTextColor.GRAY))
                                    .append(Component.text(report.getStatus().getDisplayName(), NamedTextColor.GRAY))
                                    .append(Component.text("ID:", NamedTextColor.DARK_GRAY))
                                    .append(Component.text(report.getId(), NamedTextColor.DARK_GRAY))
                    ));
                    item.setItemMeta(meta);
                    gui.addItem(item);
                }
                admin.openInventory(gui);
            });
        });
    }
}