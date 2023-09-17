package ru.kainlight.lightcheck.COMMANDS;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightcheck.API.CheckedPlayer;
import ru.kainlight.lightcheck.API.LightCheckAPI;
import ru.kainlight.lightcheck.COMMON.lightlibrary.LightLib;
import ru.kainlight.lightcheck.Main;

import java.util.*;

public class Check implements CommandExecutor {

    private final Main plugin;

    public Check(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("lightcheck").setTabCompleter(new Completer(plugin));
    }

    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, String label, String[] args) {

        if (args.length == 0) {
            sendHelpMessage(commandSender);
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.saveDefaultConfig();
                plugin.getMessageConfig().saveDefaultConfig();
                plugin.reloadConfig();
                plugin.getMessageConfig().reloadConfig();

                plugin.getSLF4JLogger().info("-- Configurations reloaded --");
                return true;
            }
            if (args[0].equalsIgnoreCase("reconfig")) {
                LightLib.updateConfig(plugin);
                plugin.messageConfig.updateConfig();

                plugin.getSLF4JLogger().info("-- Configurations updated --");
                return true;
            }
        }

        if (!(commandSender instanceof Player sender)) return true;
        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "list" -> {
                if (!commandSender.hasPermission("lightcheck.list")) return true;

                var checkedPlayers = LightCheckAPI.get().getCheckedPlayers();
                Integer checkedPlayersCount = checkedPlayers.size();

                final String header = plugin.getMessageConfig().getConfig().getString("list.header");
                final String text = plugin.getMessageConfig().getConfig().getString("list.body");
                final String footer = plugin.getMessageConfig().getConfig().getString("list.footer").replace("<count>", checkedPlayersCount.toString());

                plugin.getMessenger().sendMessage(commandSender, header);
                checkedPlayers.forEach((inspector, checked) -> {
                    String message = text.replace("<inspector>", inspector.getName()).replace("<username>", checked.getName());
                    plugin.getMessenger().sendMessage(commandSender, message);
                });
                plugin.getMessenger().sendMessage(commandSender, footer);
            }
            case "confirm" -> {
                if (!LightCheckAPI.get().isChecking(sender)) return true;

                CheckedPlayer checkedPlayer = LightCheckAPI.get().getCheckedPlayer(sender);

                String approve_player = plugin.getMessageConfig().getConfig().getString("successfully.confirm")
                        .replace("<username>", sender.getName());
                plugin.getMessenger().sendMessage(checkedPlayer.getInspector(), approve_player);

                checkedPlayer.approve();
            }
            case "approve" -> {
                if (!commandSender.hasPermission("lightcheck.approve")) return true;

                CheckedPlayer checkedPlayer = LightCheckAPI.get().getCheckedPlayerByInspector(sender);
                if (checkedPlayer == null || checkedPlayer.getPlayer() == null) return true;

                String approve_staff = plugin.getMessageConfig().getConfig().getString("successfully.approve")
                        .replace("<username>", checkedPlayer.getPlayer().getName());
                plugin.getMessenger().sendMessage(commandSender, approve_staff);

                checkedPlayer.approve();
            }
            case "disprove" -> {
                if (!(commandSender.hasPermission("lightcheck.disprove"))) return true;

                CheckedPlayer checkedPlayer = LightCheckAPI.get().getCheckedPlayerByInspector(sender);
                if (checkedPlayer == null || checkedPlayer.getPlayer() == null) return true;

                String disproved_for_staff = plugin.getMessageConfig().getConfig().getString("successfully.disprove.staff")
                        .replace("<username>", checkedPlayer.getPlayer().getName());
                plugin.getMessenger().sendMessage(commandSender, disproved_for_staff);

                boolean titleEnabled = plugin.getConfig().getBoolean("settings.title");
                if (titleEnabled) {
                    String titleMessage = plugin.getMessageConfig().getConfig().getString("screen.disprove-title");
                    String subTitleMessage = plugin.getMessageConfig().getConfig().getString("screen.disprove-subtitle");
                    plugin.getMessenger().sendTitle(checkedPlayer.getPlayer(), titleMessage, subTitleMessage, 1, 3, 1);
                }

                String disproved_for_player = plugin.getMessageConfig().getConfig().getString("successfully.disprove.player");
                plugin.getMessenger().sendMessage(checkedPlayer.getPlayer(), disproved_for_player);

                checkedPlayer.disprove();
            }
            case "timer" -> {
                CheckedPlayer checkedPlayer = LightCheckAPI.get().getCheckedPlayerByInspector(sender);

                // ! TODO: Ошибка при добавлении времени связанная с прогрессом боссбара
                /*if(args[1].equalsIgnoreCase("add")) {
                    if (!sender.hasPermission("lightcheck.timer.add")) return true;

                    if(!args[2].matches("\\d+")) {
                        plugin.getMessenger().sendMessage(sender, "&cNot numbers");
                        return true;
                    }

                    Long value = Long.parseLong(args[2]);
                    checkedPlayer.addTime(value);

                    String message = plugin.getMessageConfig().getConfig().getString("successfully.timer.add")
                            .replace("<username>", checkedPlayer.getPlayer().getName())
                            .replace("<value>", value.toString());
                    plugin.getMessenger().sendMessage(sender, message);
                } else */

                if (args[1].equalsIgnoreCase("stop")) {
                    if (!sender.hasPermission("lightcheck.timer.stop")) return true;

                    checkedPlayer.stopTimer();
                    String message = plugin.getMessageConfig().getConfig().getString("successfully.timer.stop").replace("<username>", checkedPlayer.getPlayer().getName());
                    plugin.getMessenger().sendMessage(sender, message);
                }
                return true;
            }
            case "stopall", "stop-all" -> {
                if (!(commandSender.hasPermission("lightcheck.stop-all"))) return true;

                String stopall = plugin.getMessageConfig().getConfig().getString("successfully.stop-all");
                plugin.getMessenger().sendMessage(commandSender, stopall);

                LightCheckAPI.get().stopAll();
            }
            default -> {
                if (!commandSender.hasPermission("lightcheck.check")) return true;

                if (args.length == 1) {
                    String username = args[0];
                    Player player = plugin.getServer().getPlayer(username);
                    CheckedPlayer checkedPlayer = LightCheckAPI.get().getCheckedPlayer(player);

                    if (player == null) {
                        String notFound = plugin.getMessageConfig().getConfig().getString("errors.not-found");
                        plugin.getMessenger().sendMessage(commandSender, notFound);
                        return true;
                    }

                    if (player.equals(commandSender)) return true;

                    if (LightCheckAPI.get().getCheckedPlayers().containsKey(commandSender)) {
                        String already_self = plugin.getMessageConfig().getConfig().getString("errors.already-self");
                        plugin.getMessenger().sendMessage(commandSender, already_self);
                        return true;
                    }

                    if (LightCheckAPI.get().getCheckedPlayers().containsValue(player)) {
                        String already = plugin.getMessageConfig().getConfig().getString("errors.already");
                        plugin.getMessenger().sendMessage(commandSender, already);
                        return true;
                    }

                    if (player.hasPermission("lightcheck.bypass")) {
                        String already = plugin.getMessageConfig().getConfig().getString("errors.bypass").replace("<username>", player.getName());
                        plugin.getMessenger().sendMessage(commandSender, already);
                        return true;
                    }

                    checkedPlayer.call(sender);

                    String call = plugin.getMessageConfig().getConfig().getString("successfully.call").replace("<username>", username);
                    plugin.getMessenger().sendMessage(commandSender, call);
                }
                return true;
            }
        }

        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        if (!(sender.hasPermission("lightcheck.check"))) return;

        String lang = plugin.getConfig().getString("language");

        plugin.getMessenger().sendMessage(sender,"");
        if (lang.equalsIgnoreCase("russian")) {
            plugin.getMessenger().sendMessage(sender, " &c&m   &e&l LIGHTCHECK ПОМОЩЬ &c&m   ");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check list &8- &7список текущих проверок");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check <player> &8- &7вызвать на проверку");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check confirm &8- &7признаться виновным");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check approve &8- &7признать виновным");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check disprove  &8- &7признать невиновным");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check timer stop &8- &7отключить таймер");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check stop-all &8- &7отменить все текущие проверки");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check reload &8- &7перезагрузить конфигурации (для консоли)");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check reconfig &8- &7обновить конфигурации (для консоли)");
        } else {
            plugin.getMessenger().sendMessage(sender, " &c&m   &e&l LIGHTCHECK HELP &c&m   ");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check list &8- &7the list of currently checking");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check <player> &8- &7summon to check");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check confirm &8- &7plead guilty");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check approve &8- &7find guilty");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check disprove  &8- &7find not guilty");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check timer stop &8- &7disable the timer");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check stop-all &8- &7cancel all current checks");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check reload &8- &7reload all configurations (only console)");
            plugin.getMessenger().sendMessage(sender, " &c&l» &a/check reconfig &8- &7update all configurations (only console)");
        }
        plugin.getMessenger().sendMessage(sender,"");
    }


    final class Completer implements TabCompleter {

        private final Main plugin;

        final Map<String, List<String>> completions = new HashMap<>();

        public Completer(Main plugin) {
            this.plugin = plugin;
            completions.putIfAbsent("all", Arrays.asList("list", "approve", "disprove", "timer", "stop-all"));
            completions.putIfAbsent("timer", Arrays.asList("stop"));
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
            if (!sender.hasPermission("lightcheck.check")) return null;

            if (cmd.getName().equalsIgnoreCase("lightcheck") || cmd.getName().equalsIgnoreCase("check")) {
                if (args.length == 1) {
                    List<String> completionsCopy = new ArrayList<>(completions.get("all"));
                    List<String> playerNames = plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList();
                    completionsCopy.addAll(playerNames);
                    return completionsCopy;
                } else if (args.length == 2 && args[0].equalsIgnoreCase("timer")) {
                    return completions.get("timer");
                }
            }
            return null;
        }
    }

}