package ru.snowyk.sentinelCore.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import ru.snowyk.sentinelCore.model.Punishment;
import ru.snowyk.sentinelCore.service.PunishmentService;

import java.util.Date;

public class PunishmentListener implements Listener {

    private final PunishmentService service;

    public PunishmentListener(PunishmentService service) {
        this.service = service;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        try {
            Punishment ban = service.getActiveBan(event.getUniqueId()).join();

            if (ban != null) {
                Component msg = Component.text("Вы были забанены на этом сервере!", NamedTextColor.RED)
                        .append(Component.text("\n\n"))
                        .append(Component.text("Причина: ", NamedTextColor.GRAY))
                        .append(Component.text(ban.getReason(), NamedTextColor.WHITE))
                        .append(Component.text("\n"))
                        .append(Component.text("Истекает: ", NamedTextColor.GRAY))
                        .append(Component.text(
                                ban.isPermanent() ? "Никогда" : new Date(ban.getExpirationTime()).toString(),
                                NamedTextColor.WHITE
                ));

                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text("Ошибка проверки данных. Попробуйте позже.", NamedTextColor.RED));
        }
    }
}