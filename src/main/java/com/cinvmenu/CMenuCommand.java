package com.cinvmenu;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMenuCommand implements CommandExecutor {

    private final CInvMenu plugin;

    public CMenuCommand(CInvMenu plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (!plugin.getPlayerDataManager().isSelected(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You do not have access to the creative inventory menu.");
            return true;
        }

        plugin.getMenuManager().openMainMenu(player);
        return true;
    }
}
