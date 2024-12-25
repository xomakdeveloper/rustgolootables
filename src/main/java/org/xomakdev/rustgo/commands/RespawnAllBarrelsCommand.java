package org.xomakdev.rustgo.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.xomakdev.rustgo.Main;
import org.xomakdev.rustgo.data.BarrelDataManager;

import java.util.List;
import java.util.Map;

public class RespawnAllBarrelsCommand implements CommandExecutor {
    private final Main plugin;
    private final BarrelDataManager dataManager;

    public RespawnAllBarrelsCommand(Main plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getBarrelDataManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<Map<String, Object>> barrels = dataManager.getAllBarrels();
        if (barrels == null || barrels.isEmpty()) {
            sender.sendMessage("No barrels found to respawn.");
            return true;
        }

        for (Map<String, Object> barrel : barrels) {
            if (barrel != null) {
                String type = (String) barrel.get("type");
                int maxHp = getMaxHpForType(type);
                barrel.put("current_hp", maxHp);
                dataManager.updateBarrel((int) barrel.get("id"), barrel);
            }
        }

        sender.sendMessage("All barrels have been respawned with maximum HP.");
        return true;
    }

    private int getMaxHpForType(String type) {
        return plugin.getConfig().getInt("barrels." + type + ".hp", 100);
    }
}