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
import java.util.Optional;

public class Check implements CommandExecutor {

    private final Main plugin;

    public Check(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(commandSender.hasPermission("lightcheck.check"))) return true;

            this.sendHelpMessage(LightPlayer.of(commandSender));
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
        LightPlayer lightSender = LightPlayer.of(sender);
        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "list" -> {
                if (!sender.hasPermission("lightcheck.list")) return true;

                var checkedPlayers = LightCheckAPI.get().getCheckedPlayers();
                Integer checkedPlayersCount = checkedPlayers.size();

                final String header = plugin.getMessageConfig().getConfig().getString("list.header");
                final String text = plugin.getMessageConfig().getConfig().getString("list.body");
                final String footer = plugin.getMessageConfig().getConfig().getString("list.footer").replace("<count>", checkedPlayersCount.toString());

                lightSender.sendMessage(header);
                checkedPlayers.forEach(checked -> {
                    String message = text.replace("<inspector>", checked.getInspector().getName()).replace("<username>", checked.getPlayer().getName());
                    lightSender.sendMessage(message);
                });
                lightSender.sendMessage(footer);
            }
            case "confirm" -> {
                if (!LightCheckAPI.get().isChecking(sender)) return true;
                Optional<CheckedPlayer> checkedPlayer = LightCheckAPI.get().getCheckedPlayer(sender);
                if(checkedPlayer.isEmpty()) return true;
                CheckedPlayer checked = checkedPlayer.get();

                String approve_player = plugin.getMessageConfig().getConfig().getString("successfully.confirm")
                        .replace("<username>", sender.getName());
                LightPlayer.of(checked.getInspector()).sendMessage(approve_player);

                checked.approve();

                List<String> getApproveCommands = plugin.getConfig().getStringList("commands.approve");
                if(!getApproveCommands.isEmpty()) {
                    getApproveCommands.forEach(commands -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), commands.replace("<player>", checked.getPlayer().getName())));
                }
            }
            case "approve" -> {
                if (!sender.hasPermission("lightcheck.approve")) return true;

                Optional<CheckedPlayer> checkedPlayer = LightCheckAPI.get().getCheckedPlayerByInspector(sender);
                if (checkedPlayer.isEmpty()) return true;
                CheckedPlayer checked = checkedPlayer.get();

                String approve_staff = plugin.getMessageConfig().getConfig().getString("successfully.approve")
                        .replace("<username>", checked.getPlayer().getName());
                lightSender.sendMessage(approve_staff);

                checked.approve();

                List<String> getApproveCommands = plugin.getConfig().getStringList("commands.approve");
                if(!getApproveCommands.isEmpty()) {
                    getApproveCommands.forEach(approveCommands -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), approveCommands.replace("<player>", checked.getPlayer().getName())));
                }
            }
            case "disprove" -> {
                if (!(sender.hasPermission("lightcheck.disprove"))) return true;

                Optional<CheckedPlayer> checkedPlayer = LightCheckAPI.get().getCheckedPlayerByInspector(sender);
                if (checkedPlayer.isEmpty()) return true;
                CheckedPlayer checked = checkedPlayer.get();

                String disproved_for_staff = plugin.getMessageConfig().getConfig().getString("successfully.disprove.staff")
                        .replace("<username>", checked.getPlayer().getName());
                lightSender.sendMessage(disproved_for_staff);

                boolean titleEnabled = plugin.getConfig().getBoolean("settings.title");
                if (titleEnabled) {
                    String titleMessage = plugin.getMessageConfig().getConfig().getString("screen.disprove-title");
                    String subTitleMessage = plugin.getMessageConfig().getConfig().getString("screen.disprove-subtitle");
                    LightPlayer.of(checked.getPlayer()).sendTitle(titleMessage, subTitleMessage, 1, 3, 1);
                }

                String disproved_for_player = plugin.getMessageConfig().getConfig().getString("successfully.disprove.player");
                LightPlayer.of(checked.getPlayer()).sendMessage(disproved_for_player);

                checked.disprove();
            }
            case "timer" -> {
                if (!sender.hasPermission("lightcheck.timer.continue") && !sender.hasPermission("lightcheck.timer.stop")) return true;
                Optional<CheckedPlayer> checkedPlayer = LightCheckAPI.get().getCheckedPlayerByInspector(sender);
                if (checkedPlayer.isEmpty()) return true;
                CheckedPlayer checked = checkedPlayer.get();

                if (args[1].equalsIgnoreCase("continue") && sender.hasPermission("lightcheck.timer.continue")) {
                    String message = plugin.getMessageConfig().getConfig().getString("successfully.timer.continue")
                            .replace("<username>", checked.getPlayer().getName())
                            .replace("<value>", checked.getTimer().toString());
                    lightSender.sendMessage(message);
                    checked.startTimer();
                    return true;
                }

                if (args[1].equalsIgnoreCase("stop") && sender.hasPermission("lightcheck.timer.stop")) {
                    String message = plugin.getMessageConfig().getConfig().getString("successfully.timer.stop").replace("<username>", checked.getPlayer().getName());
                    lightSender.sendMessage(message);
                    checked.stopTimer();
                    return true;
                }
                return true;
            }
            case "stopall", "stop-all" -> {
                if (!(sender.hasPermission("lightcheck.stop-all"))) return true;

                String stopall = plugin.getMessageConfig().getConfig().getString("successfully.stop-all");
                lightSender.sendMessage(stopall);

                LightCheckAPI.get().stopAll();
            }
            default -> {
                if (!sender.hasPermission("lightcheck.check")) return true;

                if (args.length == 1) {
                    String username = args[0];
                    Player player = plugin.getServer().getPlayer(username);

                    if (player == null) {
                        String notFound = plugin.getMessageConfig().getConfig().getString("errors.not-found");
                        lightSender.sendMessage(notFound);
                        return true;
                    }

                    if (player.hasPermission("lightcheck.bypass")) {
                        String already = plugin.getMessageConfig().getConfig().getString("errors.bypass").replace("<username>", player.getName());
                        lightSender.sendMessage(already);
                        return true;
                    }

                    if (player.equals(sender)) {
                        String already_self = plugin.getMessageConfig().getConfig().getString("errors.call-self");
                        lightSender.sendMessage(already_self);
                        return true;
                    }

                    if(LightCheckAPI.get().isCheckingByInspector(sender)) {
                        String already_self = plugin.getMessageConfig().getConfig().getString("errors.already-self");
                        lightSender.sendMessage(already_self);
                        return true;
                    }

                    Optional<CheckedPlayer> checkedPlayer = LightCheckAPI.get().getCheckedPlayer(player);
                    if (checkedPlayer.isPresent()) {
                        String already = plugin.getMessageConfig().getConfig().getString("errors.already");
                        lightSender.sendMessage(already);
                        return true;
                    }

                    LightCheckAPI.get().check(player, sender);
                    String call = plugin.getMessageConfig().getConfig().getString("successfully.call").replace("<username>", username);
                    lightSender.sendMessage(call);
                }
                return true;
            }
        }

        return true;
    }

    private void sendHelpMessage(LightPlayer sender) {
        String lang = plugin.getConfig().getString("language");

        sender.sendMessage(" ");
        if (lang.equalsIgnoreCase("russian")) {
            sender.sendMessage(" &c&m   &e&l LIGHTCHECK ПОМОЩЬ &c&m   ");
            sender.sendMessage(" &c&l» &a/check list &8- &7список текущих проверок");
            sender.sendMessage(" &c&l» &a/check <player> &8- &7вызвать на проверку");
            sender.sendMessage(" &c&l» &a/check confirm &8- &7признаться виновным");
            sender.sendMessage(" &c&l» &a/check approve &8- &7признать виновным");
            sender.sendMessage(" &c&l» &a/check disprove  &8- &7признать невиновным");
            sender.sendMessage(" &c&l» &a/check timer stop &8- &7отключить таймер");
            sender.sendMessage(" &c&l» &a/check stop-all &8- &7отменить все текущие проверки");
            sender.sendMessage(" &c&l» &a/check reload &8- &7перезагрузить конфигурации (для консоли)");
        } else {
            sender.sendMessage(" &c&m   &e&l LIGHTCHECK HELP &c&m   ");
            sender.sendMessage(" &c&l» &a/check list &8- &7the list of currently checking");
            sender.sendMessage(" &c&l» &a/check <player> &8- &7summon to check");
            sender.sendMessage(" &c&l» &a/check confirm &8- &7plead guilty");
            sender.sendMessage(" &c&l» &a/check approve &8- &7find guilty");
            sender.sendMessage(" &c&l» &a/check disprove  &8- &7find not guilty");
            sender.sendMessage(" &c&l» &a/check timer stop &8- &7disable the timer");
            sender.sendMessage(" &c&l» &a/check stop-all &8- &7cancel all current checks");
            sender.sendMessage(" &c&l» &a/check reload &8- &7reload all configurations (only console)");
        }
        sender.sendMessage(" ");
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
                    return Arrays.asList("continue", "stop");
                }
            }
            return null;
        }
    }

}