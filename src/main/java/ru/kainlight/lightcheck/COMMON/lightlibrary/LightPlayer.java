package ru.kainlight.lightcheck.COMMON.lightlibrary;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.kainlight.lightcheck.COMMON.lightlibrary.UTILS.Parser;
import ru.kainlight.lightcheck.Main;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public final class LightPlayer {

    private static final BukkitAudiences audience = BukkitAudiences.create(Main.getInstance());
    private final CommandSender sender;

    private LightPlayer(CommandSender sender) {
        this.sender = sender;
    }

    public static LightPlayer of(CommandSender sender) {
        return new LightPlayer(sender);
    }

    public void sendClickableHoverMessage(String message, String hover, String command) {
        if(message == null) return;

        Component mainComponent = Parser.get().hex(message);
        Component hoverComponent = Parser.get().hex(hover);
        Component component = mainComponent
                .clickEvent(ClickEvent.runCommand(command))
                .hoverEvent(HoverEvent.showText(hoverComponent));

        audience.sender(sender).sendMessage(component);
    }

    public void sendClickableMessage(String message, String command) {
        if(message == null) return;
        Component component = Parser.get().hex(message);
        component = component.clickEvent(ClickEvent.runCommand(command));

        audience.sender(sender).sendMessage(component);
    }

    public static void sendMessage(String message, Player... players) {
        if(message == null) return;

        Component component = Parser.get().hex(message);
        for (Player player : players) {
            audience.player(player).sendMessage(component);
        }
    }

    public void sendMessage(String message) {
        if(message == null) return;
        Component component = Parser.get().hex(message);

        audience.sender(sender).sendMessage(component);
    }

    public void sendMessage(List<String> message) {
        if(message == null || message.isEmpty()) return;

        message.forEach(this::sendMessage);
    }

    public void sendActionbar(String message) {
        if(message == null) return;
        Component component = Parser.get().hex(message);

        audience.sender(sender).sendActionBar(component);
    }

    public void sendHoverMessage(String message, String hover) {
        if(message == null) return;

        Component component = Parser.get().hex(message);
        Component hoverComponent = Parser.get().hex(hover);
        component = component.hoverEvent(HoverEvent.showText(hoverComponent));

        audience.sender(sender).sendMessage(component);
    }

    @SuppressWarnings("all")
    public void sendTitle(String title, String subtitle, long fadeIn, long stay, long fadeOut) {
        Component titleComponent = Parser.get().hex(title);
        Component subtitleComponent = Parser.get().hex(subtitle);

        Title.Times times = Title.Times.of(Duration.ofSeconds(fadeIn), Duration.ofSeconds(stay), Duration.ofSeconds(fadeOut));
        Title titleToSend = Title.title(titleComponent, subtitleComponent, times);

        audience.sender(sender).showTitle(titleToSend);
    }

    public void sendTitle(Component title, Component subTitle) {
        Title resultTitle = Title.title(title, subTitle);
        audience.sender(sender).showTitle(resultTitle);
    }

    public void clearTitle() {
        audience.sender(sender).clearTitle();
    }

    public void showBossBar(BossBar bossBar) {
        audience.sender(sender).showBossBar(bossBar);
    }
    public void hideBossBar(BossBar bossBar) {
        audience.sender(sender).hideBossBar(bossBar);
    }

    public static void sendMessageForAll(String message) {
        if(message == null) return;
        Component component = Parser.get().hex(message);

        Bukkit.getServer().getOnlinePlayers().forEach(online -> audience.player(online).sendMessage(component));
    }

    public static void sendMessageForAll(List<String> messages) {
        if(messages == null || messages.isEmpty()) return;

        Bukkit.getServer().getOnlinePlayers().forEach(online -> {
            messages.forEach(message -> {
                Component component = Parser.get().hex(message);
                audience.player(online).sendMessage(component);
            });
        });
    }

    public static void sendMessageForAll(String... message) {
        if(message == null) return;
        List<String> messages = Arrays.stream(message).toList();
        if(messages.isEmpty()) return;

        sendMessageForAll(messages);
    }

}
