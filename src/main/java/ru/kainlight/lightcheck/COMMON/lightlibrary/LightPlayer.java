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
import org.bukkit.plugin.Plugin;
import ru.kainlight.lightcheck.COMMON.lightlibrary.UTILS.Parser;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public final class LightPlayer {

    private static BukkitAudiences audience;
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

        if(audience == null) {
            sender.sendMessage(component);
            return;
        }

        audience.sender(sender).sendMessage(component);
    }

    public void sendClickableMessage(String message, String command) {
        if(message == null) return;

        Component component = Parser.get().hex(message)
                .clickEvent(ClickEvent.runCommand(command));

        if(audience == null) {
            sender.sendMessage(component);
            return;
        }

        audience.sender(sender).sendMessage(component);
    }

    public static void sendMessage(String message, Player... players) {
        if(message == null) return;

        Component component = Parser.get().hex(message);
        for (Player player : players) {

            if(audience == null) {
                player.sendMessage(component);
                return;
            } else {
                audience.player(player).sendMessage(component);
            }
        }
    }

    public void sendMessage(String message) {
        if(message == null) return;

        Component component = Parser.get().hex(message);

        if(audience == null) {
            sender.sendMessage(component);
            return;
        }

        audience.sender(sender).sendMessage(component);
    }

    public void sendMessage(List<String> message) {
        if(message == null || message.isEmpty()) return;

        message.forEach(this::sendMessage);
    }

    public void sendActionbar(String message) {
        if(message == null) return;

        Component component = Parser.get().hex(message);

        if(audience == null) {
            sender.sendActionBar(component);
            return;
        }

        audience.sender(sender).sendActionBar(component);
    }

    public void sendHoverMessage(String message, String hover) {
        if(message == null) return;

        Component component = Parser.get().hex(message);
        Component hoverComponent = Parser.get().hex(hover);

        component = component.hoverEvent(HoverEvent.showText(hoverComponent));

        if(audience == null) {
            sender.sendMessage(component);
            return;
        }

        audience.sender(sender).sendMessage(component);
    }

    @SuppressWarnings("all")
    public void sendTitle(String title, String subtitle, long fadeIn, long stay, long fadeOut) {
        Component titleComponent = Parser.get().hex(title);
        Component subtitleComponent = Parser.get().hex(subtitle);

        Title.Times times = Title.Times.of(Duration.ofSeconds(fadeIn), Duration.ofSeconds(stay), Duration.ofSeconds(fadeOut));
        Title titleToSend = Title.title(titleComponent, subtitleComponent, times);

        if(audience == null) {
            sender.showTitle(titleToSend);
            return;
        }

        audience.sender(sender).showTitle(titleToSend);
    }

    public void sendTitle(Component title, Component subTitle) {
        Title resultTitle = Title.title(title, subTitle);
        if(audience == null) {
            sender.showTitle(resultTitle);
            return;
        }
        audience.sender(sender).showTitle(resultTitle);
    }

    public void clearTitle() {
        if(audience == null) {
            sender.clearTitle();
            return;
        }
        audience.sender(sender).clearTitle();
    }

    public void showBossBar(BossBar bossBar) {
        if(audience == null) {
            sender.showBossBar(bossBar);
            return;
        }
        audience.sender(sender).showBossBar(bossBar);
    }
    public void hideBossBar(BossBar bossBar) {
        if(audience == null) {
            sender.hideBossBar(bossBar);
            return;
        }
        audience.sender(sender).hideBossBar(bossBar);
    }

    public static void sendMessageForAll(String message) {
        if(message == null) return;
        Component component = Parser.get().hex(message);

        Bukkit.getServer().getOnlinePlayers().forEach(online -> {

            if(audience == null) {
                online.sendMessage(component);
            } else {
                audience.player(online).sendMessage(component);
            }
        });
    }

    public static void sendMessageForAll(List<String> messages) {
        if(messages == null || messages.isEmpty()) return;

        Bukkit.getServer().getOnlinePlayers().forEach(online -> {
            messages.forEach(message -> {
                Component component = Parser.get().hex(message);
                if(audience == null) {
                    online.sendMessage(component);
                } else {
                    audience.player(online).sendMessage(component);
                }
            });
        });
    }

    public static void sendMessageForAll(String... message) {
        if(message == null) return;
        List<String> messages = Arrays.stream(message).toList();
        if(messages.isEmpty()) return;

        sendMessageForAll(messages);
    }

    public static void registerAudience(Plugin plugin) {
        try {
            audience = BukkitAudiences.create(plugin);
        } catch (NoSuchMethodError e) {}
    }

}
