package com.cinvmenu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CInvCommand implements CommandExecutor, TabCompleter {

    private final CInvMenu plugin;

    public CInvCommand(CInvMenu plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Extra permission check (also set in plugin.yml)
        if (!sender.hasPermission("cinvmenu.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("user") && args.length >= 2 && args[1].equalsIgnoreCase("select")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /cinv user select <player>");
                return true;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
            if (target.getName() == null && !target.hasPlayedBefore()) {
                // Still allow UUID-style or new names; just warn
            }
            UUID uuid = target.getUniqueId();
            plugin.getPlayerDataManager().select(uuid);
            String name = target.getName() != null ? target.getName() : args[2];
            sender.sendMessage(ChatColor.GREEN + "Selected " + name + " for creative inventory menu access.");
            if (target.isOnline()) {
                target.getPlayer().sendMessage(ChatColor.GREEN + "You have been granted access to /cmenu.");
            }
            return true;
        }

        if (sub.equals("remove")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /cinv remove <player>");
                return true;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            UUID uuid = target.getUniqueId();
            if (!plugin.getPlayerDataManager().isSelected(uuid)) {
                sender.sendMessage(ChatColor.YELLOW + "That player is not currently selected.");
                return true;
            }
            plugin.getPlayerDataManager().remove(uuid);
            String name = target.getName() != null ? target.getName() : args[1];
            sender.sendMessage(ChatColor.GREEN + "Removed " + name + " from creative inventory menu access.");
            if (target.isOnline()) {
                target.getPlayer().sendMessage(ChatColor.YELLOW + "Your /cmenu access has been removed.");
            }
            return true;
        }

        if (sub.equals("list")) {
            var selected = plugin.getPlayerDataManager().getSelectedPlayers();
            if (selected.isEmpty()) {
                sender.sendMessage(ChatColor.YELLOW + "No players are currently selected.");
                return true;
            }
            sender.sendMessage(ChatColor.GOLD + "Selected players (" + selected.size() + "):");
            for (UUID uuid : selected) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                String name = op.getName() != null ? op.getName() : uuid.toString();
                String status = op.isOnline() ? ChatColor.GREEN + "online" : ChatColor.GRAY + "offline";
                sender.sendMessage(ChatColor.WHITE + "- " + name + " " + status);
            }
            return true;
        }

        sendUsage(sender);
        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "CInvMenu commands:");
        sender.sendMessage(ChatColor.YELLOW + "/cinv user select <player> " + ChatColor.GRAY + "- Grant menu access");
        sender.sendMessage(ChatColor.YELLOW + "/cinv remove <player> " + ChatColor.GRAY + "- Revoke menu access");
        sender.sendMessage(ChatColor.YELLOW + "/cinv list " + ChatColor.GRAY + "- List selected players");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Return empty list so tab completion / suggestions stay empty (helps keep it hidden)
        return new ArrayList<>();
    }
}
