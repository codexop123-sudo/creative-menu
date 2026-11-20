package com.cinvmenu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MenuListener implements Listener {

    private final CInvMenu plugin;

    public MenuListener(CInvMenu plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory inv = event.getInventory();
        if (!(inv.getHolder() instanceof MenuHolder holder)) return;

        event.setCancelled(true); // lock the GUI

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        // Only handle clicks inside the top inventory
        if (event.getRawSlot() >= inv.getSize()) return;

        if (holder.getType() == MenuHolder.Type.MAIN) {
            handleMainClick(player, clicked);
            return;
        }

        if (holder.getType() == MenuHolder.Type.CATEGORY) {
            handleCategoryClick(player, holder, clicked, event.getRawSlot());
        }
    }

    private void handleMainClick(Player player, ItemStack clicked) {
        Material type = clicked.getType();
        for (MenuManager.Category cat : MenuManager.Category.values()) {
            if (cat.icon == type) {
                plugin.getMenuManager().openCategory(player, cat, 0);
                return;
            }
        }
    }

    private void handleCategoryClick(Player player, MenuHolder holder, ItemStack clicked, int rawSlot) {
        Material type = clicked.getType();

        // Controls are on the bottom row (slots 45-53)
        if (rawSlot >= 45) {
            if (type == Material.BLUE_STAINED_GLASS_PANE) {
                // Previous page
                plugin.getMenuManager().openCategory(player, holder.getCategory(), holder.getPage() - 1);
                return;
            }
            if (type == Material.RED_STAINED_GLASS_PANE) {
                // Next page
                plugin.getMenuManager().openCategory(player, holder.getCategory(), holder.getPage() + 1);
                return;
            }
            if (type == Material.ARROW) {
                // Back to main
                plugin.getMenuManager().openMainMenu(player);
                return;
            }
            // Gray glass or anything else on control row does nothing
            return;
        }

        // Normal item click → give a full stack
        giveFullStack(player, type);
    }

    private void giveFullStack(Player player, Material material) {
        try {
            ItemStack stack = new ItemStack(material, material.getMaxStackSize());
            // Try to add to inventory; leftover drops at feet
            var leftover = player.getInventory().addItem(stack);
            if (!leftover.isEmpty()) {
                for (ItemStack left : leftover.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), left);
                }
                player.sendMessage(ChatColor.YELLOW + "Inventory full – extra items dropped at your feet.");
            } else {
                player.sendMessage(ChatColor.GREEN + "Received " + material.getMaxStackSize() + "x " + formatName(material));
            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Could not give that item.");
        }
    }

    private String formatName(Material mat) {
        String name = mat.name().toLowerCase().replace('_', ' ');
        StringBuilder sb = new StringBuilder();
        for (String word : name.split(" ")) {
            if (word.isEmpty()) continue;
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(' ');
        }
        return sb.toString().trim();
    }
}
