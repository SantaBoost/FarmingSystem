package me.santa.farming;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;

public class BackPack implements Listener {


    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "Q");
        suffixes.put(1_000_000_000_000_000_000L, "QT");
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }























    @EventHandler
    public void onMine(BlockBreakEvent event) {
        event.getPlayer().playSound(event.getPlayer().getLocation(),Sound.ENTITY_ARROW_HIT_PLAYER, 10, 2);
        //FarmingSystem.getPlugin().getServer().broadcastMessage("e");
        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.hasItemMeta()) {
            if (item.getItemMeta().getDisplayName().contains("Farming Hoe")) {
                if (event.getBlock().getType() == Material.BEETROOTS || event.getBlock().getType() == Material.WHEAT || event.getBlock().getType() == Material.CARROTS || event.getBlock().getType() == Material.POTATOES) {
                    //FarmingSystem.getPlugin().getServer().broadcastMessage("Debug #1");
                     Material block = event.getBlock().getType();
                    Ageable ageable = (Ageable) event.getBlock().getBlockData();
                    ageable.setAge(0);






                    int amount = 0;
                    if (item.getType() == Material.NETHERITE_HOE) {
                        amount = amount + 6;
                    } else if (item.getType() == Material.DIAMOND_HOE) {
                        amount = amount + 5;
                    } else if (item.getType() == Material.GOLDEN_HOE) {
                        amount = amount + 4;
                    } else if (item.getType() == Material.IRON_HOE) {
                        amount = amount + 3;
                    } else if (item.getType() == Material.STONE_HOE) {
                        amount = amount + 2;
                    } else if (item.getType() == Material.WOODEN_HOE) {
                        amount = amount + 1;
                    }
                    if (amount >= 1) {
                        //FarmingSystem.getPlugin().getServer().broadcastMessage("Debug #2");
                        Collection<ItemStack> drops = event.getBlock().getDrops();
                        event.setDropItems(false);
                        File f = PlayerBackPackData.getPlayerData(p);
                        YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
                        List list;
                        if (drops instanceof List)
                            list = (List) drops;
                        else
                            list = new ArrayList(drops);
                        List<ItemStack> items = (List<ItemStack>) yml.get("backpack");
                        for (int i = 0; i < drops.size(); i++) {

                            ItemStack drop = (ItemStack) list.get(i);
                            drop.setAmount(amount);


                            items.add(drop);


                        }
                        yml.set("backpack", items);
                        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 10, 2);
                        try {
                            yml.save(f);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        FarmingSystem.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(FarmingSystem.getPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                ageable.setAge(0);
                                event.getBlock().setType(block);
                            }
                        }, 6000);
                    } else {
                        if (!p.getGameMode().equals(GameMode.CREATIVE)) {
                            event.getBlock().setBlockData(ageable);
                        }
                    }
                }
            }


        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        String[] args = command.split(" ");
        if (args[0].equals("/backpack")) {
            event.setCancelled(true);
            openBackpack(event.getPlayer());
        }
    }
    public Material getSeeds(Material b) {
        if (b == Material.WHEAT) {
            return Material.WHEAT_SEEDS;

        } else if (b == Material.POTATOES) {
            return Material.POTATO;

        } else if (b == Material.CARROTS) {
            return Material.CARROT;
        } else if (b == Material.BEETROOTS) {
            return Material.BEETROOT;
        } else {
            return null;
        }
    }
    public void openBackpack(Player p) {

        File f = PlayerBackPackData.getPlayerData(p);
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
        List<ItemStack> items = (List<ItemStack>) yml.get("backpack");
        Inventory inv = Bukkit.createInventory(null, 54, "Farming BackPack");
        ItemStack sellHopper = new ItemStack(Material.HOPPER);
        ItemMeta sellHopperMeta = sellHopper.getItemMeta();
        sellHopperMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Click me to sell!"));
        long tokens = yml.getInt("tokens");
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&dFarming Tokens: &f" + format(tokens).toString()));
        sellHopperMeta.setLore(lore);
        sellHopper.setItemMeta(sellHopperMeta);
        ItemStack frame = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemStack frame2 = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta framemeta = frame.getItemMeta();
        framemeta.setDisplayName(" ");

        ItemMeta framemeta2 = frame2.getItemMeta();
        framemeta2.setDisplayName(" ");
        frame2.setItemMeta(framemeta2);
        frame.setItemMeta(framemeta2);
        inv.setItem(53, frame);
        inv.setItem(52, frame);
        inv.setItem(51, frame);
        inv.setItem(50, frame);
        inv.setItem(49, frame);
        inv.setItem(48, frame);
        inv.setItem(47, frame);
        inv.setItem(46, frame);
        inv.setItem(45, frame);
        inv.setItem(0, frame);
        inv.setItem(1, frame);
        inv.setItem(2, frame);
        inv.setItem(3, frame);
        inv.setItem(4, frame);
        inv.setItem(5, frame);
        inv.setItem(6, frame);
        inv.setItem(7, frame);
        inv.setItem(8, frame);
        inv.setItem(9, frame);
        inv.setItem(17, frame);
        inv.setItem(18, frame);
        inv.setItem(26, frame);
        inv.setItem(27, frame);
        inv.setItem(35, frame);
        inv.setItem(36, frame);
        inv.setItem(44, frame);
        inv.setItem(37, frame2);
        inv.setItem(38, frame2);
        inv.setItem(39, frame2);
        inv.setItem(41, frame2);
        inv.setItem(42, frame2);
        inv.setItem(43, frame2);
        inv.setItem(40, sellHopper);
        for (int i = 0; i < items.size(); i++) {
            inv.addItem(items.get(i));
        }
        p.openInventory(inv);


    }

@EventHandler
public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Farming BackPack")) {
            Player p = (Player) event.getWhoClicked();
            event.setCancelled(true);
            if (event.getSlot() == 40) {

                sell((Player) event.getWhoClicked());
                p.closeInventory();
                openBackpack(p);

            }
        }




}
public void sell(Player p) {
    File f = PlayerBackPackData.getPlayerData(p);
    YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
    List<ItemStack> list = (List<ItemStack>) yml.get("backpack");
    //p.sendMessage(list.toString());
    if (!list.isEmpty()) {
        long tokens = 0;
        long temp = 0;
        for (int i = 0; i < list.size(); i++) {
            tokens = yml.getInt("tokens");
            //p.sendMessage(list.get(i).getType().toString());
            if (list.get(i).getType() == Material.WHEAT) {
                int amount = list.get(i).getAmount();
                temp = temp+15*amount;
                tokens = tokens + 15*amount;
                yml.set("tokens", tokens);

            } else if (list.get(i).getType() == Material.WHEAT_SEEDS) {
                int amount = list.get(i).getAmount();
                temp = temp+7*amount;
                tokens = tokens + 7*amount;
                yml.set("tokens", tokens);

            } else if (list.get(i).getType() == Material.CARROT) {
                int amount = list.get(i).getAmount();
                temp = temp+30*amount;
                tokens = tokens + 30*amount;
                yml.set("tokens", tokens);

            } else if (list.get(i).getType() == Material.BEETROOT) {
                int amount = list.get(i).getAmount();
                temp = temp+45*amount;
                tokens = tokens + 45*amount;
                yml.set("tokens", tokens);

            }else if (list.get(i).getType() == Material.BEETROOT_SEEDS) {
                int amount = list.get(i).getAmount();
                temp = temp+22*amount;
                tokens = tokens + 22*amount;
                yml.set("tokens", tokens);

            }else if (list.get(i).getType() == Material.POTATO) {
                int amount = list.get(i).getAmount();
                temp = temp+60*amount;
                tokens = tokens + 60*amount;


            }
        }
        list.clear();
        yml.set("tokens", tokens);
        try {
            yml.save(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou sold your backpack with worth of &b&l&n" + format(temp) + " farming tokens&a."));
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 1);
    } else {
        p.sendMessage("Nothing to sell");
    }
}
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerBackPackData.createPlayerData(event.getPlayer());
    }

}
