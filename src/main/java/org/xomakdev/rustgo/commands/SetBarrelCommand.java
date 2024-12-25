package org.xomakdev.rustgo.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.xomakdev.rustgo.Main;
import org.xomakdev.rustgo.data.BarrelDataManager;

import java.util.HashMap;
import java.util.Map;

public class SetBarrelCommand implements CommandExecutor {
    private final Main plugin;

    public SetBarrelCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду может выполнять только игрок.");
            return true;
        }

        Player player = (Player) sender;
        Location loc = player.getLocation().getBlock().getLocation();
        loc.getBlock().setType(Material.BARRIER);

        String barrelType = "wood_barrel"; // В идеале тип должен быть передан через аргумент в команде
        int maxHp = plugin.getConfig().getInt("barrels." + barrelType + ".max_hp", 100);
        int respawnTime = plugin.getConfig().getInt("barrels." + barrelType + ".respawn_time", 30);

        // Генерация айди для бочки
        BarrelDataManager dataManager = plugin.getBarrelDataManager();
        int id = dataManager.getNextAvailableId();

        Map<String, Object> barrelData = new HashMap<>();
        barrelData.put("id", id);
        barrelData.put("x", loc.getBlockX());
        barrelData.put("y", loc.getBlockY());
        barrelData.put("z", loc.getBlockZ());
        barrelData.put("type", barrelType);
        barrelData.put("current_hp", maxHp);
        barrelData.put("respawn_time", respawnTime);

        dataManager.addBarrel(barrelData);

        ArmorStand armorStand = player.getWorld().spawn(loc.add(0, 1, 0), ArmorStand.class);
        armorStand.setVisible(true);
        armorStand.setGravity(false);

        ItemStack barrelItem = new ItemStack(Material.IRON_HOE);
        ItemMeta meta = barrelItem.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(12345);
            barrelItem.setItemMeta(meta);
        }

        armorStand.getEquipment().setHelmet(barrelItem);

        player.sendMessage("Бочка установлена с " + maxHp + " HP и типом " + barrelType + ".");
        return true;
    }
}
