package ru.kainlight.lightcheck.EVENTS;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import ru.kainlight.lightcheck.API.LightCheckAPI;
import ru.kainlight.lightcheck.COMMON.lightlibrary.LightPlayer;
import ru.kainlight.lightcheck.Main;

import java.util.List;

public class CheckedListener implements Listener {

    private final Main plugin;

    public CheckedListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuitChecked(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (LightCheckAPI.get().isChecking(player)) {
            LightCheckAPI.get().getCheckedPlayer(player).approve();
            List<String> quitCommands = plugin.getConfig().getStringList("commands.quit");
            boolean abilityEnabled = !quitCommands.isEmpty();

            if (abilityEnabled) {
                quitCommands.forEach(command -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace("<player>", player.getName())));
            }

        }
    }

    @EventHandler
    public void onKickChecked(PlayerKickEvent event) {
        Player player = event.getPlayer();

        if (LightCheckAPI.get().isChecking(player)) {
            LightCheckAPI.get().getCheckedPlayer(player).approve();
            List<String> kickCommands = plugin.getConfig().getStringList("commands.kick");
            boolean abilityEnabled = !kickCommands.isEmpty();

            if (abilityEnabled) {
                var timer = LightCheckAPI.get().getTimer();

                if (timer != null && timer.get(player) >= 0) {
                    kickCommands.forEach(command -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace("<player>", player.getName())));
                }
            }

        }
    }

    @EventHandler
    public void onCommandsChecked(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (isCheckingAndAbilityEnabled(player, "block-chat.enable")) {
            List<String> allowedCommands = plugin.getConfig().getStringList("abilities.block-chat.allowed-commands");
            String[] message = event.getMessage().replace("/", "").split(" ");

            if (!allowedCommands.contains(message[0])) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onChatChecked(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (isCheckingAndAbilityEnabled(player, "block-chat.enable")) {
            event.setCancelled(true);

            Player staff = LightCheckAPI.get().getCheckedPlayers().inverse().get(player);
            String privateDialog = plugin.getMessageConfig().getConfig().getString("chat.dialog")
                    .replace("<username>", player.getName())
                    .replace("<message>", event.getMessage());

            LightPlayer.sendMessage(privateDialog, player, staff);
        }
    }

    @EventHandler
    public void onMoveChecked(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (isCheckingAndAbilityEnabled(player, "block-move")) {
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void onDropItemsChecked(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (isCheckingAndAbilityEnabled(player, "block-drops")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickupItemsChecked(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (isCheckingAndAbilityEnabled(player, "block-pickup")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPickupArrowChecked(PlayerPickupArrowEvent event) {
        Player player = event.getPlayer();

        if (isCheckingAndAbilityEnabled(player, "block-pickup")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageChecked(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (isCheckingAndAbilityEnabled(player, "block-damage")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamageChecked(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (isCheckingAndAbilityEnabled(player, "block-damage")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteractChecked(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (isCheckingAndAbilityEnabled(player, "block-interact")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGamemodeChangeChecked(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();

        if (isCheckingAndAbilityEnabled(player, "block-gamemode")) {
            event.setCancelled(true);
        }
    }

    private boolean isCheckingAndAbilityEnabled(Player player, String abilityName) {
        boolean isChecked = LightCheckAPI.get().isChecking(player);
        boolean abilityEnabled = plugin.getConfig().getBoolean("abilities." + abilityName);
        return isChecked && abilityEnabled;
    }

}