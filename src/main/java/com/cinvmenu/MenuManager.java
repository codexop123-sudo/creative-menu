package com.cinvmenu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class MenuManager {

    private final CInvMenu plugin;

    // Category definitions (approximate vanilla creative tabs via keyword matching)
    public enum Category {
        BUILDING_BLOCKS("Building Blocks", Material.BRICKS),
        COMBAT("Combat", Material.DIAMOND_SWORD),
        TOOLS("Tools", Material.DIAMOND_PICKAXE),
        FOOD("Food", Material.COOKED_BEEF),
        REDSTONE("Redstone", Material.REDSTONE),
        DECORATION("Decoration", Material.PAINTING),
        MISC("Misc", Material.LAVA_BUCKET),
        ALL_ITEMS("All Items", Material.DIAMOND);

        public final String displayName;
        public final Material icon;

        Category(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
    }

    // Cached lists so we don't rebuild every open
    private final Map<Category, List<Material>> categoryItems = new EnumMap<>(Category.class);
    private List<Material> allItems = new ArrayList<>();

    public MenuManager(CInvMenu plugin) {
        this.plugin = plugin;
        buildItemLists();
    }

    private void buildItemLists() {
        List<Material> combat = new ArrayList<>();
        List<Material> tools = new ArrayList<>();
        List<Material> food = new ArrayList<>();
        List<Material> redstone = new ArrayList<>();
        List<Material> decoration = new ArrayList<>();
        List<Material> building = new ArrayList<>();
        List<Material> misc = new ArrayList<>();

        for (Material mat : Material.values()) {
            if (!mat.isItem()) continue;
            if (mat.name().startsWith("LEGACY_")) continue;
            // Skip air and technical items
            if (mat == Material.AIR || mat == Material.CAVE_AIR || mat == Material.VOID_AIR) continue;

            String name = mat.name().toLowerCase(Locale.ROOT);

            boolean categorized = false;

            // Combat
            if (name.contains("sword") || name.contains("bow") || name.contains("crossbow")
                    || name.contains("trident") || name.contains("shield") || name.contains("arrow")
                    || name.contains("helmet") || name.contains("chestplate") || name.contains("leggings")
                    || name.contains("boots") || name.contains("axe") && (name.contains("wooden") || name.contains("stone")
                    || name.contains("iron") || name.contains("golden") || name.contains("diamond") || name.contains("netherite"))
                    || name.contains("spear") || name.equals("mace")) {
                combat.add(mat);
                categorized = true;
            }

            // Tools (pickaxe, shovel, hoe, shears, flint_and_steel, etc.) – avoid double-counting axes already in combat
            if (!categorized && (name.contains("pickaxe") || name.contains("shovel") || name.contains("hoe")
                    || name.contains("shears") || name.contains("fishing_rod") || name.contains("flint_and_steel")
                    || name.contains("brush") || name.contains("compass") || name.contains("clock")
                    || name.contains("spyglass") || name.contains("lead") || name.contains("name_tag"))) {
                tools.add(mat);
                categorized = true;
            }

            // Food
            if (!categorized && (mat.isEdible() || name.contains("cake") || name.contains("cookie")
                    || name.contains("pie") || name.contains("stew") || name.contains("soup")
                    || name.contains("bread") || name.contains("apple") || name.contains("carrot")
                    || name.contains("potato") || name.contains("beef") || name.contains("pork")
                    || name.contains("chicken") || name.contains("mutton") || name.contains("rabbit")
                    || name.contains("cod") || name.contains("salmon") || name.contains("tropical")
                    || name.contains("pufferfish") || name.contains("melon") || name.contains("sweet_berries")
                    || name.contains("glow_berries") || name.contains("honey") || name.contains("milk_bucket")
                    || name.contains("dried_kelp") || name.contains("chorus_fruit"))) {
                food.add(mat);
                categorized = true;
            }

            // Redstone
            if (!categorized && (name.contains("redstone") || name.contains("piston") || name.contains("observer")
                    || name.contains("hopper") || name.contains("dropper") || name.contains("dispenser")
                    || name.contains("comparator") || name.contains("repeater") || name.contains("lever")
                    || name.contains("button") || name.contains("pressure_plate") || name.contains("tripwire")
                    || name.contains("daylight") || name.contains("target") || name.contains("note_block")
                    || name.contains("jukebox") || name.contains("sculk_sensor") || name.contains("calibrated_sculk")
                    || name.contains("crafter") || name.contains("copper_bulb") || name.contains("copper_door")
                    || name.contains("copper_trapdoor") || name.contains("lightning_rod"))) {
                redstone.add(mat);
                categorized = true;
            }

            // Decoration / plants / banners / etc.
            if (!categorized && (name.contains("flower") || name.contains("sapling") || name.contains("leaves")
                    || name.contains("carpet") || name.contains("banner") || name.contains("painting")
                    || name.contains("item_frame") || name.contains("glow_item_frame") || name.contains("armor_stand")
                    || name.contains("candle") || name.contains("lantern") || name.contains("torch")
                    || name.contains("campfire") || name.contains("sign") || name.contains("hanging_sign")
                    || name.contains("bed") || name.contains("pot") || name.contains("vase")
                    || name.contains("head") || name.contains("skull") || name.contains("trophy")
                    || name.contains("music_disc") || name.contains("book") || name.contains("frame")
                    || name.contains("bell") || name.contains("conduit") || name.contains("beacon")
                    || name.contains("end_crystal") || name.contains("dragon_egg") || name.contains("wither_rose")
                    || name.contains("spore_blossom") || name.contains("hanging_roots") || name.contains("moss")
                    || name.contains("azalea") || name.contains("vine") || name.contains("lily")
                    || name.contains("seagrass") || name.contains("kelp") || name.contains("coral")
                    || name.contains("sea_pickle") || name.contains("frogspawn") || name.contains("sculk")
                    || name.contains("froglight") || name.contains("ochre") || name.contains("verdant")
                    || name.contains("pearlescent"))) {
                decoration.add(mat);
                categorized = true;
            }

            // Building blocks (most solid blocks that aren't already categorized)
            if (!categorized && mat.isBlock() && (name.contains("stone") || name.contains("dirt")
                    || name.contains("sand") || name.contains("gravel") || name.contains("wood")
                    || name.contains("log") || name.contains("planks") || name.contains("slab")
                    || name.contains("stairs") || name.contains("wall") || name.contains("fence")
                    || name.contains("gate") || name.contains("door") || name.contains("trapdoor")
                    || name.contains("glass") || name.contains("pane") || name.contains("terracotta")
                    || name.contains("concrete") || name.contains("wool") || name.contains("brick")
                    || name.contains("prismarine") || name.contains("purpur") || name.contains("nether")
                    || name.contains("end_stone") || name.contains("basalt") || name.contains("blackstone")
                    || name.contains("deepslate") || name.contains("copper") || name.contains("iron_block")
                    || name.contains("gold_block") || name.contains("diamond_block") || name.contains("emerald_block")
                    || name.contains("lapis") || name.contains("coal_block") || name.contains("quartz")
                    || name.contains("obsidian") || name.contains("crying_obsidian") || name.contains("bedrock")
                    || name.contains("sponge") || name.contains("hay") || name.contains("bone_block")
                    || name.contains("honeycomb_block") || name.contains("slime_block") || name.contains("honey_block")
                    || name.contains("tnt") || name.contains("bookshelf") || name.contains("lectern")
                    || name.contains("barrel") || name.contains("chest") || name.contains("shulker")
                    || name.contains("furnace") || name.contains("smoker") || name.contains("blast_furnace")
                    || name.contains("anvil") || name.contains("grindstone") || name.contains("stonecutter")
                    || name.contains("loom") || name.contains("cartography") || name.contains("smithing")
                    || name.contains("fletching") || name.contains("brewing") || name.contains("cauldron")
                    || name.contains("composter") || name.contains("scaffolding") || name.contains("ladder")
                    || name.contains("rail") || name.contains("ice") || name.contains("snow")
                    || name.contains("clay") || name.contains("mud") || name.contains("rooted")
                    || name.contains("mangrove") || name.contains("cherry") || name.contains("bamboo")
                    || name.contains("crimson") || name.contains("warped") || name.contains("nylium")
                    || name.contains("netherrack") || name.contains("soul") || name.contains("magma")
                    || name.contains("glowstone") || name.contains("shroomlight") || name.contains("ochre_froglight")
                    || name.contains("verdant_froglight") || name.contains("pearlescent_froglight"))) {
                building.add(mat);
                categorized = true;
            }

            if (!categorized) {
                misc.add(mat);
            }
        }

        // Sort each list alphabetically by material name for consistent order
        Comparator<Material> byName = Comparator.comparing(m -> m.name());
        combat.sort(byName);
        tools.sort(byName);
        food.sort(byName);
        redstone.sort(byName);
        decoration.sort(byName);
        building.sort(byName);
        misc.sort(byName);

        categoryItems.put(Category.COMBAT, combat);
        categoryItems.put(Category.TOOLS, tools);
        categoryItems.put(Category.FOOD, food);
        categoryItems.put(Category.REDSTONE, redstone);
        categoryItems.put(Category.DECORATION, decoration);
        categoryItems.put(Category.BUILDING_BLOCKS, building);
        categoryItems.put(Category.MISC, misc);

        // All Items = everything unique, sorted
        Set<Material> unique = new LinkedHashSet<>();
        unique.addAll(building);
        unique.addAll(combat);
        unique.addAll(tools);
        unique.addAll(food);
        unique.addAll(redstone);
        unique.addAll(decoration);
        unique.addAll(misc);
        allItems = new ArrayList<>(unique);
        allItems.sort(byName);
        categoryItems.put(Category.ALL_ITEMS, allItems);
    }

    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(new MenuHolder(MenuHolder.Type.MAIN, null, 0), 27,
                ChatColor.DARK_PURPLE + "Creative Menu");

        // Place category icons in a nice row
        setIcon(inv, 10, Category.BUILDING_BLOCKS);
        setIcon(inv, 11, Category.COMBAT);
        setIcon(inv, 12, Category.TOOLS);
        setIcon(inv, 13, Category.FOOD);
        setIcon(inv, 14, Category.REDSTONE);
        setIcon(inv, 15, Category.DECORATION);
        setIcon(inv, 16, Category.MISC);

        // Diamond for All Items in the center bottom-ish
        setIcon(inv, 22, Category.ALL_ITEMS);

        player.openInventory(inv);
    }

    private void setIcon(Inventory inv, int slot, Category cat) {
        ItemStack item = new ItemStack(cat.icon);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + cat.displayName);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Click to browse");
            int count = categoryItems.getOrDefault(cat, Collections.emptyList()).size();
            lore.add(ChatColor.DARK_GRAY + "" + count + " items");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        inv.setItem(slot, item);
    }

    public void openCategory(Player player, Category category, int page) {
        List<Material> items = categoryItems.getOrDefault(category, Collections.emptyList());
        int itemsPerPage = 45; // 5 rows of 9, leaving bottom row for controls
        int totalPages = Math.max(1, (int) Math.ceil(items.size() / (double) itemsPerPage));
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        Inventory inv = Bukkit.createInventory(
                new MenuHolder(MenuHolder.Type.CATEGORY, category, page),
                54,
                ChatColor.DARK_PURPLE + category.displayName + ChatColor.DARK_GRAY + " (" + (page + 1) + "/" + totalPages + ")");

        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, items.size());

        int slot = 0;
        for (int i = start; i < end; i++) {
            Material mat = items.get(i);
            try {
                ItemStack stack = new ItemStack(mat);
                inv.setItem(slot, stack);
                slot++;
            } catch (Exception ignored) {
                // Some materials can't form ItemStacks in certain versions
            }
        }

        // Bottom row controls
        // Blue glass = previous
        if (page > 0) {
            inv.setItem(45, createControl(Material.BLUE_STAINED_GLASS_PANE, ChatColor.BLUE + "Previous Page"));
        } else {
            inv.setItem(45, createControl(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "No Previous"));
        }

        // Back to main menu
        inv.setItem(49, createControl(Material.ARROW, ChatColor.YELLOW + "Back to Categories"));

        // Red glass = next
        if (page < totalPages - 1) {
            inv.setItem(53, createControl(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Next Page"));
        } else {
            inv.setItem(53, createControl(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "No Next"));
        }

        player.openInventory(inv);
    }

    private ItemStack createControl(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    public List<Material> getItems(Category category) {
        return categoryItems.getOrDefault(category, Collections.emptyList());
    }
}
