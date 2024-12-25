package org.xomakdev.rustgo.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.xomakdev.rustgo.Main;
import org.xomakdev.rustgo.data.BarrelDataManager;

import java.util.List;
import java.util.Map;

public class DeleteBarrelCommand implements CommandExecutor {
    private final Main plugin;

    public DeleteBarrelCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Использование: /deletebarrel <id>");
            return true;
        }

        try {
            int id = Integer.parseInt(args[0]);
            BarrelDataManager dataManager = plugin.getBarrelDataManager();
            List<Map<?, ?>> barrels = dataManager.getDataConfig().getMapList("barrels");

            for (Map<?, ?> barrel : barrels) {
                if ((int) barrel.get("id") == id) {
                    Location loc = new Location(
                            Bukkit.getWorld("world"),
                            (int) barrel.get("x"),
                            (int) barrel.get("y"),
                            (int) barrel.get("z")
                    );

                    loc.getBlock().setType(Material.AIR);
                    dataManager.removeBarrel(id);
                    sender.sendMessage("Бочка #" + id + " удалена.");
                    return true;
                }
            }

            sender.sendMessage("Бочка с ID " + id + " не найдена.");
        } catch (NumberFormatException e) {
            sender.sendMessage("ID должен быть числом.");
        }

        return true;
    }
}
