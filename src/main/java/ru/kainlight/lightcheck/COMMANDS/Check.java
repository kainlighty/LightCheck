package ru.kainlight.lightcheck.COMMANDS;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightcheck.API.CheckedPlayer;
import ru.kainlight.lightcheck.API.LightCheckAPI;
import ru.kainlight.lightcheck.COMMON.lightlibrary.LightPlayer;
import ru.kainlight.lightcheck.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Check implements CommandExecutor {

    private final Main plugin;

    public Check(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, String label, String[] args) {

        if (args.length == 0) {
            if(!commandSender.hasPermission("lightcheck.approve")) return true;

            this.sendHelpMessage(commandSender);
            return true;
        }

        if (args.length == 1 && !(commandSender instanceof Player)) {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.saveDefaultConfig();
                plugin.getMessageConfig().saveDefaultConfig();
                plugin.reloadConfig();
                plugin.getMessageConfig().reloadConfig();

                plugin.getLogger().info("-- Configurations reloaded --");
            }

            return true;
        }

        if (!(commandSender instanceof Player sender)) return true;
        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "list" -> {
                if (!sender.hasPermission("lightcheck.list")) return true;

                var checkedPlayers = LightCheckAPI.get().getCheckedPlayers();
                Integer checkedPlayersCount = checkedPlayers.size();

                final String header = plugin.getMessageConfig().getConfig().getString("list.header");
                final String text = plugin.getMessageConfig().getConfig().getString("list.body");
                final String footer = plugin.getMessageConfig().getConfig().getString("list.footer").replace("<count>", checkedPlayersCount.toString());

                LightPlayer.of(sender).sendMessage(header);
                checkedPlayers.forEach((inspector, checked) -> {
                    String message = text.replace("<inspector>", inspector.getName()).replace("<username>", checked.getName());
                    LightPlayer.of(sender).sendMessage(message);
                });
                LightPlayer.of(sender).sendMessage(footer);
            }
            case "confirm" -> {
                if (!LightCheckAPI.get().isChecking(sender)) return true;
                CheckedPlayer checkedPlayer = LightCheckAPI.get().getCheckedPlayer(sender);

                String approve_player = plugin.getMessageConfig().getConfig().getString("successfully.confirm")
                        .replace("<username>", sender.getName());
                LightPlayer.of(checkedPlayer.getPlayer()).sendMessage(approve_player);

                checkedPlayer.approve();

                List<String> getApproveCommands = plugin.getConfig().getStringList("commands.approve");
                if(!getApproveCommands.isEmpty()) {
                    getApproveCommands.forEach(commands -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), commands.replace("<player>", checkedPlayer.getPlayer().getName())));
                }
            }
            case "approve" -> {
                if (!sender.hasPermission("lightcheck.approve")) return true;

                CheckedPlayer checkedPlayer = LightCheckAPI.get().getCheckedPlayerByInspector(sender);
                if (checkedPlayer == null || checkedPlayer.getPlayer() == null) return true;

                String approve_staff = plugin.getMessageConfig().getConfig().getString("successfully.approve")
                        .replace("<username>", checkedPlayer.getPlayer().getName());
                LightPlayer.of(sender).sendMessage(approve_staff);

                checkedPlayer.approve();

                List<String> getApproveCommands = plugin.getConfig().getStringList("commands.approve");
                if(!getApproveCommands.isEmpty()) {
                    getApproveCommands.forEach(approveCommands -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), approveCommands.replace("<player>", checkedPlayer.getPlayer().getName())));
                }
            }
            case "disprove" -> {
                if (!(sender.hasPermission("lightcheck.disprove"))) return true;

                CheckedPlayer checkedPlayer = LightCheckAPI.get().getCheckedPlayerByInspector(sender);
                if (checkedPlayer == null || checkedPlayer.getPlayer() == null) return true;

                String disproved_for_staff = plugin.getMessageConfig().getConfig().getString("successfully.disprove.staff")
                        .replace("<username>", checkedPlayer.getPlayer().getName());
                LightPlayer.of(sender).sendMessage(disproved_for_staff);

                boolean titleEnabled = plugin.getConfig().getBoolean("settings.title");
                if (titleEnabled) {
                    String titleMessage = plugin.getMessageConfig().getConfig().getString("screen.disprove-title");
                    String subTitleMessage = plugin.getMessageConfig().getConfig().getString("screen.disprove-subtitle");
                    LightPlayer.of(checkedPlayer.getPlayer()).sendTitle(titleMessage, subTitleMessage, 1, 3, 1);
                }

                String disproved_for_player = plugin.getMessageConfig().getConfig().getString("successfully.disprove.player");
                LightPlayer.of(checkedPlayer.getPlayer()).sendMessage(disproved_for_player);

                checkedPlayer.disprove();
            }
            case "timer" -> {
                if (!LightCheckAPI.get().isChecking(sender)) return true;
                CheckedPlayer checkedPlayer = LightCheckAPI.get().getCheckedPlayerByInspector(sender);

                if (args[1].equalsIgnoreCase("stop")) {
                    if (!sender.hasPermission("lightcheck.timer.stop")) return true;

                    checkedPlayer.stopTimer();
                    String message = plugin.getMessageConfig().getConfig().getString("successfully.timer.stop").replace("<username>", checkedPlayer.getPlayer().getName());
                    LightPlayer.of(sender).sendMessage(message);
                }
                return true;
            }
            case "stopall", "stop-all" -> {
                if (!(sender.hasPermission("lightcheck.stop-all"))) return true;

                String stopall = plugin.getMessageConfig().getConfig().getString("successfully.stop-all");
                LightPlayer.of(sender).sendMessage(stopall);

                LightCheckAPI.get().stopAll();
            }
            default -> {
                if (!sender.hasPermission("lightcheck.check")) return true;

                if (args.length == 1) {
                    String username = args[0];
                    Player player = plugin.getServer().getPlayer(username);
                    CheckedPlayer checkedPlayer = LightCheckAPI.get().getCheckedPlayer(player);

                    if (player == null) {
                        String notFound = plugin.getMessageConfig().getConfig().getString("errors.not-found");
                        LightPlayer.of(sender).sendMessage(notFound);
                        return true;
                    }

                    if (player.equals(sender)) return true;

                    if (LightCheckAPI.get().getCheckedPlayers().containsKey(sender)) {
                        String already_self = plugin.getMessageConfig().getConfig().getString("errors.already-self");
                        LightPlayer.of(sender).sendMessage(already_self);
                        return true;
                    }

                    if (LightCheckAPI.get().getCheckedPlayers().containsValue(player)) {
                        String already = plugin.getMessageConfig().getConfig().getString("errors.already");
                        LightPlayer.of(sender).sendMessage(already);
                        return true;
                    }

                    if (player.hasPermission("lightcheck.bypass")) {
                        String already = plugin.getMessageConfig().getConfig().getString("errors.bypass").replace("<username>", player.getName());
                        LightPlayer.of(sender).sendMessage(already);
                        return true;
                    }

                    checkedPlayer.call(sender);

                    String call = plugin.getMessageConfig().getConfig().getString("successfully.call").replace("<username>", username);
                    LightPlayer.of(sender).sendMessage(call);
                }
                return true;
            }
        }

        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        if (!(sender.hasPermission("lightcheck.check"))) return;

        String lang = plugin.getConfig().getString("language");

        sender.sendMessage("");
        if (lang.equalsIgnoreCase("russian")) {
            LightPlayer.of(sender).sendMessage(" &c&m   &e&l LIGHTCHECK ПОМОЩЬ &c&m   ");
            LightPlayer.of(sender).sendMessage(" &c&l» &a/check list &8- &7список текущих проверок");
            LightPlayer.of(sender).sendMessage(" &c&l» &a/check <player> &8- &7вызвать на проверку");
            LightPlayer.of(sender).sendMessage(" &c&l» &a/check confirm &8- &7признаться виновным");
            LightPlayer.of(sender).sendMessage(" &c&l» &a/check approve &8- &7признать виновным");
            LightPlayer.of(sender).sendMessage(" &c&l» &a/check disprove  &8- &7признать невиновным");
            LightPlayer.of(sender).sendMessage(" &c&l» &a/check timer stop &8- &7отключить таймер");
            LightPlayer.of(sender).sendMessage(" &c&l» &a/check stop-all &8- &7отменить все текущие проверки");
            LightPlayer.of(sender).sendMessage(" &c&l» &a/check reload &8- &7перезагрузить конфигурации (для консоли)");
        } else {
            LightPlayer.of(sender).sendMessage(" &c&m   &e&l LIGHTCHECK HELP &c&m   ");
            LightPlayer.of(sender).sendMessage(" &c&l» &a/check list &8- &7the list of currently checking");
            LightPlayer.of(sender).sendMessage(" &c&l» &a/check <player> &8- &7summon to check");
            LightPlayer.of(sender).sendMessage(" &c&l» &a/check confirm &8- &7plead guilty");
            LightPlayer.of(sender).sendMessage(" &c&l» &a/check approve &8- &7find guilty");
            LightPlayer.of(sender).sendMessage(" &c&l» &a/check disprove  &8- &7find not guilty");
            LightPlayer.of(sender).sendMessage(" &c&l» &a/check timer stop &8- &7disable the timer");
            LightPlayer.of(sender).sendMessage(" &c&l» &a/check stop-all &8- &7cancel all current checks");
            LightPlayer.of(sender).sendMessage(" &c&l» &a/check reload &8- &7reload all configurations (only console)");
        }
        sender.sendMessage("");
    }


    public static final class Completer implements TabCompleter {

        private final Main plugin;

        public Completer(Main plugin) {
            this.plugin = plugin;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
            if (!sender.hasPermission("lightcheck.check")) return null;

            if (cmd.getName().equalsIgnoreCase("lightcheck") || cmd.getName().equalsIgnoreCase("check")) {
                if (args.length == 1) {
                    List<String> completionsCopy = new ArrayList<>(Arrays.asList("list", "approve", "disprove", "timer", "stop-all"));
                    List<String> playerNames = plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList();
                    completionsCopy.addAll(playerNames);
                    return completionsCopy;
                } else if (args.length == 2 && args[0].equalsIgnoreCase("timer")) {
                    return Arrays.asList("stop");
                }
            }
            return null;
        }
    }

}