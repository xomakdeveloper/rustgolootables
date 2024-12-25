package org.xomakdev.rustgo.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BarrelBossBarManager {
    private final Map<Player, BossBar> playerBossBars = new HashMap<>();
    private final Map<Location, BossBar> barrelBossBars = new HashMap<>();

    public void showBossBar(Player player, Location loc, String title, double progress) {
        BossBar bossBar = barrelBossBars.get(loc);

        if (bossBar == null) {
            bossBar = Bukkit.createBossBar(title, BarColor.WHITE, BarStyle.SOLID);
            barrelBossBars.put(loc, bossBar);
        }

        bossBar.setTitle(title);
        bossBar.setProgress(progress);

        if (!bossBar.getPlayers().contains(player)) {
            bossBar.addPlayer(player);
        }

        playerBossBars.put(player, bossBar);
    }

    public void hideBossBar(Player player) {
        BossBar bossBar = playerBossBars.remove(player);
        if (bossBar != null) {
            bossBar.removePlayer(player);
        }
    }

    public void updateBossBarForAll(Location loc, String title, double progress) {
        BossBar bossBar = barrelBossBars.get(loc);
        if (bossBar != null) {
            bossBar.setTitle(title);
            bossBar.setProgress(progress);
        }
    }

    public void removeBossBar(Location loc) {
        BossBar bossBar = barrelBossBars.remove(loc);
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }

    public void removeAllBossBars() {
        for (BossBar bossBar : barrelBossBars.values()) {
            bossBar.removeAll();
        }
        barrelBossBars.clear();
        playerBossBars.clear();
    }
}
