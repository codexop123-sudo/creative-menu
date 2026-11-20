package com.cinvmenu;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerDataManager {

    private final CInvMenu plugin;
    private final File file;
    private FileConfiguration config;
    private final Set<UUID> selected = new HashSet<>();

    public PlayerDataManager(CInvMenu plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "selected-players.yml");
        load();
    }

    public void load() {
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create selected-players.yml: " + e.getMessage());
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        selected.clear();
        List<String> list = config.getStringList("selected");
        for (String s : list) {
            try {
                selected.add(UUID.fromString(s));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void save() {
        config.set("selected", selected.stream().map(UUID::toString).collect(Collectors.toList()));
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save selected-players.yml: " + e.getMessage());
        }
    }

    public boolean isSelected(UUID uuid) {
        return selected.contains(uuid);
    }

    public void select(UUID uuid) {
        selected.add(uuid);
        save();
    }

    public void remove(UUID uuid) {
        selected.remove(uuid);
        save();
    }

    public Set<UUID> getSelectedPlayers() {
        return Collections.unmodifiableSet(selected);
    }
}
