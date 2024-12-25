package org.xomakdev.rustgo;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.xomakdev.rustgo.commands.DeleteBarrelCommand;
import org.xomakdev.rustgo.commands.ListBarrelsCommand;
import org.xomakdev.rustgo.commands.RespawnAllBarrelsCommand;
import org.xomakdev.rustgo.commands.SetBarrelCommand;
import org.xomakdev.rustgo.commands.GiveCustomToolCommand;
import org.xomakdev.rustgo.data.BarrelDataManager;
import org.xomakdev.rustgo.listeners.BarrelInteractionListener;
import org.xomakdev.rustgo.utils.BarrelBossBarManager;

public class Main extends JavaPlugin {
    private BarrelDataManager barrelDataManager;
    private BarrelBossBarManager bossBarManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.barrelDataManager = new BarrelDataManager(getDataFolder());
        this.bossBarManager = new BarrelBossBarManager();

        getCommand("setbarrel").setExecutor(new SetBarrelCommand(this));
        getCommand("givecustomtool").setExecutor(new GiveCustomToolCommand(this));
        getCommand("respawnall").setExecutor(new RespawnAllBarrelsCommand(this));
        getCommand("deletebarrel").setExecutor(new DeleteBarrelCommand(this));
        getCommand("listbarrels").setExecutor(new ListBarrelsCommand(this));
        Bukkit.getPluginManager().registerEvents(new BarrelInteractionListener(this), this);

        getLogger().info("RustGo Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        bossBarManager.removeAllBossBars();
        getLogger().info("RustGo Plugin Disabled!");
    }

    public BarrelDataManager getBarrelDataManager() {
        return barrelDataManager;
    }

    public BarrelBossBarManager getBossBarManager() {
        return bossBarManager;
    }
}
