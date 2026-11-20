package com.cinvmenu;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class CInvMenu extends JavaPlugin {

    private PlayerDataManager playerDataManager;
    private MenuManager menuManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        playerDataManager = new PlayerDataManager(this);
        menuManager = new MenuManager(this);

        // Register /cinv
        PluginCommand cinvCmd = getCommand("cinv");
        if (cinvCmd != null) {
            CInvCommand cinvCommand = new CInvCommand(this);
            cinvCmd.setExecutor(cinvCommand);
            cinvCmd.setTabCompleter(cinvCommand);
            // Hide from tab / suggestions as much as possible
            cinvCmd.setPermission("cinvmenu.admin");
        }

        // Register /cmenu
        PluginCommand cmenuCmd = getCommand("cmenu");
        if (cmenuCmd != null) {
            CMenuCommand cmenuCommand = new CMenuCommand(this);
            cmenuCmd.setExecutor(cmenuCommand);
            // No tab completer so it stays hidden-ish
        }

        // Listener for GUI clicks
        Bukkit.getPluginManager().registerEvents(new MenuListener(this), this);

        getLogger().info("CInvMenu enabled. Selected players: " + playerDataManager.getSelectedPlayers().size());
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.save();
        }
        getLogger().info("CInvMenu disabled.");
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }
}
