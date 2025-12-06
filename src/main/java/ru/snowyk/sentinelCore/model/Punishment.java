package ru.snowyk.sentinelCore.model;

import java.util.UUID;

public class Punishment {

    private final int id;
    private final UUID playerUuid;
    private final UUID executorUuid;
    private final PunishmentType type;
    private final String reason;
    private final long createdAt;
    private final long duration;

    private boolean active;

    public Punishment(int id, UUID playerUuid, UUID executorUuid, PunishmentType type, String reason, long createdAt, long duration, boolean active) {
        this.id = id;
        this.playerUuid = playerUuid;
        this.executorUuid = executorUuid;
        this.type = type;
        this.reason = reason;
        this.createdAt = createdAt;
        this.duration = duration;
        this.active = active;
    }

    public Punishment(UUID playerUuid, UUID executorUuid, PunishmentType type, String reason, long duration) {
        this(0, playerUuid, executorUuid, type, reason, System.currentTimeMillis(), duration, true);
    }

    public boolean isEffective() {
        if (!active) {
            return false;
        }
        if (type.isInstant()) {
            return false;
        }
        if (isPermanent()) {
            return true;
        }
        return getExpirationTime() > System.currentTimeMillis();
    }

    public boolean isPermanent() {
        return duration == -1;
    }

    public long getExpirationTime() {
        if (isPermanent()) return Long.MAX_VALUE;
        return createdAt + duration;
    }

    public void revoke() {
        this.active = false;
    }

    public int getId() {
        return id;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public UUID getExecutorUuid() {
        return executorUuid;
    }

    public PunishmentType getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isActive() {
        return active;
    }
}