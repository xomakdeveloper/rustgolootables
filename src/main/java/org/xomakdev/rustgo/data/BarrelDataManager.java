package org.xomakdev.rustgo.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BarrelDataManager {
    private File dataFile;
    private FileConfiguration dataConfig;

    public BarrelDataManager(File dataFolder) {
        setup(dataFolder);
    }

    private void setup(File dataFolder) {
        dataFile = new File(dataFolder, "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        if (!dataConfig.contains("barrels") || !(dataConfig.get("barrels") instanceof List)) {
            dataConfig.set("barrels", new ArrayList<>());
            saveData();
        }
    }

    public FileConfiguration getDataConfig() {
        return dataConfig;
    }

    public void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNextAvailableId() {
        List<Map<String, Object>> barrels = (List<Map<String, Object>>) dataConfig.getList("barrels");
        int id = 0;
        if (barrels != null) {
            for (Map<String, Object> barrel : barrels) {
                int barrelId = (int) barrel.get("id");
                if (barrelId >= id) {
                    id = barrelId + 1; // Если уже существует увеличиваем на 1
                }
            }
        }
        return id;
    }

    public void addBarrel(Map<String, Object> barrelData) {
        List<Map<String, Object>> barrels = (List<Map<String, Object>>) dataConfig.getList("barrels");
        if (barrels == null) {
            barrels = new ArrayList<>();
        }

        barrels.add(barrelData);
        dataConfig.set("barrels", barrels);
        saveData();
    }

    public void updateBarrel(int id, Map<String, Object> updatedData) {
        List<Map<String, Object>> barrels = (List<Map<String, Object>>) dataConfig.getList("barrels");
        if (barrels == null) return;

        for (int i = 0; i < barrels.size(); i++) {
            Map<String, Object> barrel = barrels.get(i);
            if ((int) barrel.get("id") == id) {
                barrels.set(i, updatedData);
                dataConfig.set("barrels", barrels);
                saveData();
                return;
            }
        }
    }

    public void removeBarrel(int id) {
        List<Map<String, Object>> barrels = (List<Map<String, Object>>) dataConfig.getList("barrels");
        if (barrels == null) return;

        barrels.removeIf(barrel -> (int) barrel.get("id") == id);
        dataConfig.set("barrels", barrels);
        saveData();
    }

    public Map<String, Object> findBarrelById(int id) {
        List<Map<String, Object>> barrels = (List<Map<String, Object>>) dataConfig.getList("barrels");
        if (barrels == null) return null;

        for (Map<String, Object> barrel : barrels) {
            if ((int) barrel.get("id") == id) {
                return barrel;
            }
        }

        return null;
    }

    public List<Map<String, Object>> getAllBarrels() {
        List<Map<String, Object>> barrels = (List<Map<String, Object>>) dataConfig.getList("barrels");
        return barrels != null ? barrels : new ArrayList<>();
    }
}
