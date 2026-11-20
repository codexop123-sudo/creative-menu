package com.cinvmenu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MenuHolder implements InventoryHolder {

    public enum Type {
        MAIN,
        CATEGORY
    }

    private final Type type;
    private final MenuManager.Category category;
    private final int page;

    public MenuHolder(Type type, MenuManager.Category category, int page) {
        this.type = type;
        this.category = category;
        this.page = page;
    }

    public Type getType() {
        return type;
    }

    public MenuManager.Category getCategory() {
        return category;
    }

    public int getPage() {
        return page;
    }

    @Override
    public Inventory getInventory() {
        return null; // not used
    }
}
