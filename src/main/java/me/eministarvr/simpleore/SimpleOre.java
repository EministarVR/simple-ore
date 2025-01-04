package me.eministarvr.simpleore;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleOre extends JavaPlugin implements Listener {

    private final String PREFIX = "§6[§eSimpleOre§6]"; // Prefix for all messages

    // Default spawn chances for ores
    private double coalOreChance = 0.05;  // Default 5% chance
    private double ironOreChance = 0.10;  // Default 10% chance
    private double goldOreChance = 0.15;  // Default 15% chance
    private double diamondOreChance = 0.20;  // Default 20% chance

    @Override
    public void onEnable() {
        getLogger().info(PREFIX + " §aThe plugin has been successfully enabled!");
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info(PREFIX + " §cThe plugin has been successfully disabled.");
    }

    // Command handler
    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("simpleore")) {
            if (args.length == 0) {
                sendHelpMessage(sender);
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("ore")) {
            openOreSelectionGUI(sender);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("credit")) {
            showCredits(sender);
            return true;
        }
        return false;
    }

    // Help message with a beautiful design
    private void sendHelpMessage(org.bukkit.command.CommandSender sender) {
        sender.sendMessage("§6§l----------------------------");
        sender.sendMessage(PREFIX + " §7Welcome to §eSimpleOre §7plugin! Here are the available commands:");
        sender.sendMessage("§8- /simpleore §7- §aShow this help message");
        sender.sendMessage("§8- /ore §7- §aOpen the ore selection menu");
        sender.sendMessage("§8- /credit §7- §aShow plugin credits");
        sender.sendMessage("§7For support, join our Discord: §bhttps://discord.gg/8UZkN7MzkF");
        sender.sendMessage("§6§l----------------------------");
    }

    // Open ore selection GUI
    private void openOreSelectionGUI(org.bukkit.command.CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PREFIX + " §cThis command can only be used by a player.");
            return;
        }

        Player player = (Player) sender;
        Inventory inventory = Bukkit.createInventory(null, 9, "§6§lSelect an Ore");

        // Add ore items to the inventory
        inventory.addItem(createOreItem(Material.COAL_ORE, "Coal Ore"));
        inventory.addItem(createOreItem(Material.IRON_ORE, "Iron Ore"));
        inventory.addItem(createOreItem(Material.GOLD_ORE, "Gold Ore"));
        inventory.addItem(createOreItem(Material.DIAMOND_ORE, "Diamond Ore"));

        player.openInventory(inventory);
    }

    // Create ore item with a custom name
    private ItemStack createOreItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    // Show credits for the plugin with a clean design
    private void showCredits(org.bukkit.command.CommandSender sender) {
        sender.sendMessage("§6§l----------------------------");
        sender.sendMessage(PREFIX + " §7Plugin developed by §aEministarVR");
        sender.sendMessage("§7If you need support or have feedback, join our Discord community:");
        sender.sendMessage("§bhttps://discord.gg/8UZkN7MzkF");
        sender.sendMessage("§6§l----------------------------");
    }

    // Handle ore selection and spawn chance adjustments
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Handle ore item clicks and open spawn chance menu
        if (item.getType() == Material.COAL_ORE) {
            openSpawnChanceMenu(player, "Coal Ore", coalOreChance);
        } else if (item.getType() == Material.IRON_ORE) {
            openSpawnChanceMenu(player, "Iron Ore", ironOreChance);
        } else if (item.getType() == Material.GOLD_ORE) {
            openSpawnChanceMenu(player, "Gold Ore", goldOreChance);
        } else if (item.getType() == Material.DIAMOND_ORE) {
            openSpawnChanceMenu(player, "Diamond Ore", diamondOreChance);
        }
    }

    // Open the spawn chance menu with buttons for increasing or decreasing
    private void openSpawnChanceMenu(Player player, String oreName, double currentChance) {
        Inventory inventory = Bukkit.createInventory(null, 9, "§6Adjust " + oreName + " Spawn Chance");

        // Add buttons to increase/decrease spawn chance (using wool for buttons)
        ItemStack increaseButton = createWoolButton(Material.GREEN_WOOL, "Increase Chance");
        ItemStack decreaseButton = createWoolButton(Material.RED_WOOL, "Decrease Chance");
        ItemStack arrow = new ItemStack(Material.ARROW);

        // Create an item with the current spawn chance as a name
        ItemStack chanceDisplay = new ItemStack(Material.PAPER);
        ItemMeta meta = chanceDisplay.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§eCurrent Spawn Chance: " + (currentChance * 100) + "%");
            chanceDisplay.setItemMeta(meta);
        }

        // Add items to the inventory
        inventory.setItem(2, increaseButton);
        inventory.setItem(6, decreaseButton);
        inventory.setItem(4, chanceDisplay);
        inventory.setItem(8, arrow);

        // Open the inventory for the player
        player.openInventory(inventory);
    }

    // Create wool button for the spawn chance menu
    private ItemStack createWoolButton(Material woolType, String name) {
        ItemStack wool = new ItemStack(woolType);
        ItemMeta meta = wool.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            wool.setItemMeta(meta);
        }
        return wool;
    }

    // Handle interaction with spawn chance menu (increase/decrease)
    @EventHandler
    public void onSpawnChanceMenuClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        // Prevent moving items in the menu
        event.setCancelled(true);

        // Handling ore selection and spawn chance menu interaction
        if (item.getType() == Material.COAL_ORE) {
            openSpawnChanceMenu(player, "Coal Ore", coalOreChance);
        } else if (item.getType() == Material.IRON_ORE) {
            openSpawnChanceMenu(player, "Iron Ore", ironOreChance);
        } else if (item.getType() == Material.GOLD_ORE) {
            openSpawnChanceMenu(player, "Gold Ore", goldOreChance);
        } else if (item.getType() == Material.DIAMOND_ORE) {
            openSpawnChanceMenu(player, "Diamond Ore", diamondOreChance);
        }

        // Handling increase or decrease button presses for spawn chances
        if (item.getType() == Material.GREEN_WOOL) {
            // Increase spawn chance by 5%
            if (event.getView().getTitle().contains("Coal Ore")) {
                coalOreChance = Math.min(coalOreChance + 0.05, 1.0);
            } else if (event.getView().getTitle().contains("Iron Ore")) {
                ironOreChance = Math.min(ironOreChance + 0.05, 1.0);
            } else if (event.getView().getTitle().contains("Gold Ore")) {
                goldOreChance = Math.min(goldOreChance + 0.05, 1.0);
            } else if (event.getView().getTitle().contains("Diamond Ore")) {
                diamondOreChance = Math.min(diamondOreChance + 0.05, 1.0);
            }
            player.sendMessage(PREFIX + " §aSpawn chance increased!");
        } else if (item.getType() == Material.RED_WOOL) {
            // Decrease spawn chance by 5%
            if (event.getView().getTitle().contains("Coal Ore")) {
                coalOreChance = Math.max(coalOreChance - 0.05, 0.0);
            } else if (event.getView().getTitle().contains("Iron Ore")) {
                ironOreChance = Math.max(ironOreChance - 0.05, 0.0);
            } else if (event.getView().getTitle().contains("Gold Ore")) {
                goldOreChance = Math.max(goldOreChance - 0.05, 0.0);
            } else if (event.getView().getTitle().contains("Diamond Ore")) {
                diamondOreChance = Math.max(diamondOreChance - 0.05, 0.0);
            }
            player.sendMessage(PREFIX + " §cSpawn chance decreased!");
        } else if (item.getType() == Material.ARROW) {
            // Close the inventory when the arrow is clicked
            player.closeInventory();
            player.sendMessage(PREFIX + " §7Returning to the previous menu...");
            System.out.println("Arrow clicked, closing inventory...");
        }
    }
}
