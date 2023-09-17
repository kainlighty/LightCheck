package ru.kainlight.lightcheck.COMMON.lightlibrary.UTILS;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.Audiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightcheck.Main;

import java.time.Duration;
import java.util.List;

@SuppressWarnings("unused")
public final class Messenger {

    private static final Messenger messenger = new Messenger();
    public static Messenger get() {
        return messenger;
    }


    public void sendClickableHoverMessage(Player player, String message, String hover, String command) {
        if(message == null) return;

        Component mainComponent = Parser.get().hex(message);
        Component hoverComponent = Parser.get().hex(hover);

        Component component = mainComponent
                .clickEvent(ClickEvent.runCommand(command))
                .hoverEvent(HoverEvent.showText(hoverComponent));

        getPlayer(player).sendMessage(component);
    }

    public void sendClickableMessage(Player player, String message, String command) {
        if(message == null) return;

        Component component = Parser.get().hex(message)
                .clickEvent(ClickEvent.runCommand(command));

        getPlayer(player).sendMessage(component);
    }

    public void sendMessage(String message, CommandSender... senders) {
        if(message == null) return;

        Component messageComponent = Parser.get().hex(message);
        for (CommandSender sender : senders) {
            Main.getInstance().getAudience().sender(sender).sendMessage(messageComponent);
        }
    }

    public void sendMessage(CommandSender sender, String message) {
        if(message == null) return;

        Component messageComponent = Parser.get().hex(message);
        getSender(sender).sendMessage(messageComponent);
    }

    public void sendMessage(CommandSender sender, List<String> message) {
        if(message == null || message.isEmpty()) return;

        message.forEach(msg -> sendMessage(sender, msg));
    }

    public void sendActionbar(Player player, String message) {
        if(message == null) return;

        Component messageComponent = Parser.get().hex(message);
        getPlayer(player).sendActionBar(messageComponent);
    }

    public void sendHoverMessage(Player player, String message, String hover) {
        if(message == null) return;

        Component mainComponent = Parser.get().hex(message);
        Component hoverComponent = Parser.get().hex(hover);

        mainComponent = mainComponent.hoverEvent(HoverEvent.showText(hoverComponent));
        getPlayer(player).sendMessage(mainComponent);
    }

    public void sendTitle(Player player, String title, String subtitle, long fadeIn, long stay, long fadeOut) {
        Component titleComponent = Parser.get().hex(title);
        Component subtitleComponent = Parser.get().hex(subtitle);

        Title.Times times = Title.Times.times(Duration.ofSeconds(fadeIn), Duration.ofSeconds(stay), Duration.ofSeconds(fadeOut));
        Title titleToSend = Title.title(titleComponent, subtitleComponent, times);

        getPlayer(player).showTitle(titleToSend);
    }

    public void sendTitle(Player player, Component title, Component subTitle) {
        Title resultTitle = Title.title(title, subTitle);
        getPlayer(player).showTitle(resultTitle);
    }

    public void sendMessageForAll(String message) {
        if(message == null) return;
        Bukkit.getServer().getOnlinePlayers().forEach(player -> sendMessage(player, message));
    }

    public void sendMessageForAll(String... message) {
        if(message == null) return;
        this.sendMessageForAll(List.of(message));
    }

    public void sendMessageForAll(List<String> message) {
        if(message == null) return;

        Bukkit.getServer().getOnlinePlayers().forEach(player -> sendMessage(player, message));
    }

    public Messenger logger(@NotNull String message) {
        getSender(Main.getInstance().getServer().getConsoleSender()).sendMessage(Parser.get().hex(message));
        return this;
    }

    public Audience getPlayer(Player player) {
        return Main.getInstance().getAudience().sender(player);
    }

    public Audience getSender(CommandSender sender) {
        return Main.getInstance().getAudience().sender(sender);
    }

}
