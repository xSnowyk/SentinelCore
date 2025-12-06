package ru.snowyk.sentinelCore.model;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public enum ReportStatus {
    OPEN("Открыт", NamedTextColor.RED),
    IN_PROGRESS("На рассмотрении", NamedTextColor.YELLOW),
    RESOLVED("Решен", NamedTextColor.GREEN),
    REJECTED("Отклонен", NamedTextColor.GRAY);

    private final String displayName;
    private final TextColor color;

    ReportStatus(String displayName, TextColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TextColor getColor() {
        return color;
    }
}