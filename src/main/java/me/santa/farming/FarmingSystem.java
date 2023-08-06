package me.santa.farming;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class FarmingSystem extends JavaPlugin  implements Listener {

    private static Plugin plugin;
    @Override
    public void onEnable() {
        plugin = this;
        System.out.println("Farming System By SantaBoost has activated!");
        getServer().getPluginManager().registerEvents(new BackPack(), this);

    }



    @Override
    public void onDisable() {
        System.out.println("Farming System By SantaBoost has deactivated!");
    }



    public static Plugin getPlugin() {
        return plugin;
    }
}
