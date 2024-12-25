package org.xomakdev.rustgo.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.xomakdev.rustgo.Main;
import org.xomakdev.rustgo.data.BarrelDataManager;

import java.util.List;
import java.util.Map;

public class ListBarrelsCommand implements CommandExecutor {
    private final Main plugin;

    public ListBarrelsCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BarrelDataManager dataManager = plugin.getBarrelDataManager();
        List<Map<?, ?>> barrels = dataManager.getDataConfig().getMapList("barrels");

        if (barrels.isEmpty()) {
            sender.sendMessage("В базе данных нет бочек.");
            return true;
        }

        sender.sendMessage("Все записанные бочки:");
        for (Map<?, ?> barrel : barrels) {
            int id = (int) barrel.get("id");
            int x = (int) barrel.get("x");
            int y = (int) barrel.get("y");
            int z = (int) barrel.get("z");
            int respawnTime = (int) barrel.get("respawn_time");
            int currentHp = (int) barrel.get("current_hp");

            sender.sendMessage("#" + id + " - (" + x + ", " + y + ", " + z + ") - респавн тайм: "
                    + respawnTime + " сек - HP: " + currentHp);
        }

        return true;
    }
}
