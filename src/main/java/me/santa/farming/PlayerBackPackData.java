package me.santa.farming;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerBackPackData {


    public static boolean existsPlayerData(Player p) {
        File f = new File(FarmingSystem.getPlugin().getDataFolder()+"/PlayerData/", p.getUniqueId().toString()+".yml");
        if (f.exists()) {
            return true;
        } else {
            return false;
        }
    }
    public static void createPlayerData(Player p) {
        if (!existsPlayerData(p)) {
            File f = new File(FarmingSystem.getPlugin().getDataFolder() + "/PlayerData/", p.getUniqueId().toString() +".yml");
            File folder = new File(FarmingSystem.getPlugin().getDataFolder() + "/PlayerData", "");
            folder.mkdirs();

            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
            List<ItemStack> list = new ArrayList<>();
            yml.set("backpack", list);
            yml.set("tokens", 0);
            try {
                yml.save(f);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static File getPlayerData(Player p) {

        if (existsPlayerData(p)) {
            return new File(FarmingSystem.getPlugin().getDataFolder()+"/PlayerData/", p.getUniqueId().toString()+".yml");
        } else {
            return null;
        }
    }

}
