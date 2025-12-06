package ru.snowyk.sentinelCore.model;

public enum PunishmentType {
    BAN(false),
    MUTE(false),
    WARN(true),
    KICK(true);

    private final boolean instant;

    PunishmentType(boolean instant) {
        this.instant = instant;
    }

    public boolean isInstant() {
        return instant;
    }
}