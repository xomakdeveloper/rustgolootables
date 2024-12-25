package org.xomakdev.rustgo.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.xomakdev.rustgo.Main;
import org.xomakdev.rustgo.data.BarrelDataManager;
import org.xomakdev.rustgo.utils.BarrelBossBarManager;

import java.util.List;
import java.util.Map;

public class BarrelInteractionListener implements Listener {
    private final Main plugin;
    private final BarrelDataManager dataManager;
    private final BarrelBossBarManager bossBarManager;

    public BarrelInteractionListener(Main plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getBarrelDataManager();
        this.bossBarManager = plugin.getBossBarManager();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.BARRIER) {
            Player player = event.getPlayer();
            Vector blockVector = event.getClickedBlock().getLocation().toVector();
            Location blockLoc = event.getClickedBlock().getLocation();

            Map<String, Object> barrel = dataManager.findBarrelById(findBarrelIdByLocation(blockVector));
            if (barrel == null) return;

            int currentHp = (int) barrel.get("current_hp");
            int maxHp = getMaxHpForType((String) barrel.get("type"));
            int damage = getItemDamage(player.getInventory().getItemInMainHand());

            currentHp -= damage;
            if (currentHp <= 0) {
                event.getClickedBlock().setType(Material.AIR);

                for (ArmorStand armorStand : blockLoc.getWorld().getEntitiesByClass(ArmorStand.class)) {
                    if (armorStand.getLocation().distance(blockLoc.add(0.5, 1, 0.5)) <= 1.0) {
                        armorStand.remove(); // Удаление арморстенда ( НЕРАБОТАЕТ )
                        break;
                    }
                }

                bossBarManager.removeBossBar(event.getClickedBlock().getLocation());

                barrel.put("current_hp", maxHp);
                dataManager.updateBarrel((int) barrel.get("id"), barrel);

                int respawnTime = (int) barrel.get("respawn_time");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        event.getClickedBlock().getLocation().getBlock().setType(Material.BARRIER);

                        ArmorStand armorStand = blockLoc.getWorld().spawn(blockLoc.add(0.5, 1, 0.5), ArmorStand.class);
                        armorStand.setVisible(true);
                        armorStand.setGravity(false);

                        ItemStack barrelItem = new ItemStack(Material.IRON_HOE);
                        ItemMeta meta = barrelItem.getItemMeta();
                        if (meta != null) {
                            meta.setCustomModelData(12345);
                            barrelItem.setItemMeta(meta);
                        }
                        armorStand.getEquipment().setHelmet(barrelItem);
                    }
                }.runTaskLater(plugin, respawnTime * 20L);

                dropLoot(event.getClickedBlock().getLocation(), (String) barrel.get("type"));
            } else {
                barrel.put("current_hp", currentHp);
                dataManager.updateBarrel((int) barrel.get("id"), barrel);

                bossBarManager.updateBossBarForAll(
                        event.getClickedBlock().getLocation(),
                        "HP: " + currentHp + "/" + maxHp,
                        (double) currentHp / maxHp
                );
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getTargetBlockExact(5) != null && player.getTargetBlockExact(5).getType() == Material.BARRIER) {
            Vector blockVector = player.getTargetBlockExact(5).getLocation().toVector();

            Map<String, Object> barrel = dataManager.findBarrelById(findBarrelIdByLocation(blockVector));
            if (barrel == null) {
                bossBarManager.hideBossBar(player);
                return;
            }

            int currentHp = (int) barrel.get("current_hp");
            int maxHp = getMaxHpForType((String) barrel.get("type"));
            bossBarManager.showBossBar(
                    player,
                    player.getTargetBlockExact(5).getLocation(),
                    "HP: " + currentHp + "/" + maxHp,
                    (double) currentHp / maxHp
            );
        } else {
            bossBarManager.hideBossBar(player);
        }
    }

    private int findBarrelIdByLocation(Vector blockVector) {
        for (Map<String, Object> barrel : dataManager.getAllBarrels()) {
            Vector barrelVector = new Vector((int) barrel.get("x"), (int) barrel.get("y"), (int) barrel.get("z"));
            if (barrelVector.equals(blockVector)) {
                return (int) barrel.get("id");
            }
        }
        return -1; // Бочки нет
    }

    private int getMaxHpForType(String type) {
        return plugin.getConfig().getInt("barrels." + type + ".hp", 100);
    }

    private int getItemDamage(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 15;
        String itemKey = item.getItemMeta().getLocalizedName();
        return plugin.getConfig().getInt("tools." + itemKey + ".damage", 1);
    }

    private void dropLoot(Location location, String type) {
        List<String> loot = plugin.getConfig().getStringList("barrels." + type + ".loot");
        for (String item : loot) {
            Material material = Material.matchMaterial(item);
            if (material != null) {
                location.getWorld().dropItemNaturally(location, new ItemStack(material));
            }
        }
    }
}
