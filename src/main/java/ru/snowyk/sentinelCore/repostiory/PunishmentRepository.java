package ru.snowyk.sentinelCore.repostiory;

import ru.snowyk.sentinelCore.model.Punishment;

import java.util.List;
import java.util.UUID;

public interface PunishmentRepository {

    void save(Punishment punishment);

    List<Punishment> findActiveByPlayer(UUID playerUuid);

    void revoke(int punishmentId);
}