package org.xomakdev.rustgo.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.xomakdev.rustgo.Main;

import java.util.List;

public class GiveCustomToolCommand implements CommandExecutor {
    private final Main plugin;

    public GiveCustomToolCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду может выполнять только игрок.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage("Использование: /givecustomtool <название инструмента>");
            return true;
        }

        String toolKey = args[0].toLowerCase();
        if (!plugin.getConfig().getConfigurationSection("tools").contains(toolKey)) {
            player.sendMessage("Инструмент " + toolKey + " не найден в конфигурации!");
            return true;
        }

        String materialName = toolKey.split("_")[0] + "_axe";
        Material material = Material.getMaterial(materialName.toUpperCase());
        if (material == null) {
            player.sendMessage("Материал " + materialName + " не найден!");
            return true;
        }

        ItemStack tool = new ItemStack(material);
        ItemMeta meta = tool.getItemMeta();
        assert meta != null;

        meta.setDisplayName(plugin.getConfig().getString("tools." + toolKey + ".name").replace("&", "§"));
        meta.setCustomModelData(plugin.getConfig().getInt("tools." + toolKey + ".custom_model_data"));
        meta.setLore(plugin.getConfig().getStringList("tools." + toolKey + ".lore").stream()
                .map(line -> line.replace("&", "§"))
                .toList());
        tool.setItemMeta(meta);

        player.getInventory().addItem(tool);
        player.sendMessage("Инструмент " + toolKey + " добавлен в ваш инвентарь!");
        return true;
    }
}
