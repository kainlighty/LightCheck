package ru.kainlight.lightcheck.COMMON.lightlibrary.UTILS;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public final class Parser {

    private static final Parser parser = new Parser();
    public static Parser get() {
        return parser;
    }

    private final Pattern pattern = Pattern.compile("#?&?([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})");

    public Component hex(String message) {
        if(message.equals("")) return Component.text("");

        StringBuffer buffer = new StringBuffer();
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String colorCode = matcher.group(1);
            try {
                TextColor color = TextColor.fromHexString(colorCode);
                if (color != null) {
                    String replacement = TextColor.color(color).toString();
                    matcher.appendReplacement(buffer, replacement);
                }
            } catch (IllegalArgumentException ignored) {}
        }
        matcher.appendTail(buffer);
        TextComponent result = LegacyComponentSerializer.legacy('&').deserialize(buffer.toString());
        return result;
    }

    public Component hex(Component message) {
        String serializedMessage = LegacyComponentSerializer.legacySection().serialize(message);
        return hex(serializedMessage);
    }

    public String hexString(Component message) {
        if(message == null) return "";

        String serializedMessage = LegacyComponentSerializer.legacySection().serialize(message);
        return hexString(serializedMessage);
    }

    public String hexString(String message) {
        if(message == null) return "";

        Component serializedMessage = hex(message);
        return LegacyComponentSerializer.legacySection().serialize(serializedMessage);
    }

    public String replacedString(@NotNull Component text, String replaceOn, String replaceable) {
        Component component = text.replaceText(TextReplacementConfig.builder()
                .matchLiteral(replaceOn)
                .replacement(replaceable)
                .build());
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public Component replacedComponent(@NotNull Component text, String replaceOn, String replaceable) {
        return text.replaceText(TextReplacementConfig.builder()
                .matchLiteral(replaceOn)
                .replacement(replaceable)
                .build());
    }
}