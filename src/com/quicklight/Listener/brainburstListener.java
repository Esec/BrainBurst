package com.quicklight.Listener;

import com.quicklight.SQL.MySQL;
import com.sun.org.glassfish.external.statistics.annotations.Reset;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import static com.quicklight.BrainBurst.Main.handle;
import static com.quicklight.BrainBurst.Main.mysql;

/**
 * Created by Administrator on 2016/6/7 0007.
 */
public class brainburstListener implements Listener {
    HashMap<Player, Integer> map = new HashMap<>();//记录玩家被击杀的次数

    @EventHandler
    void onLogin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        MySQL sql = mysql;
        sql.joinserver(p.getName());
    }

    @EventHandler
    void onChat(PlayerDeathEvent e) {
        if (e.getEntity().getKiller() != null) {
            //killer 击杀者 killed被击杀者
            Player killer = e.getEntity().getKiller();
            Player killed = e.getEntity();
            mysql.pvp(killer.getName(), killed.getName());
            if (map.containsKey(killed)) {
                map.put(killed, map.get(killed) + 1);
                if (map.get(killed) == 1) {
                }
            } else {
                map.put(killed, 1);
                Bukkit.getScheduler().runTaskLater(handle, new Runnable() {
                    @Override
                    public void run() {
                        map.remove(killed);
                    }
                }, 20 * 60 * 30);
            }
        }
    }

    @EventHandler
    void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            if (map.get(e.getDamager()) >= 3) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    void onM(InventoryClickEvent e) {
        if (e.getClickedInventory().getName().equals("§6§lBrainBurst")) {
            e.setCancelled(true);//禁止拖动
            if (e.getCurrentItem() != null) {//点到空气报错
                if (e.getCurrentItem().getItemMeta().hasDisplayName()) {//判断是不是有名字
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l强化功能")) {
                        e.getWhoClicked().sendMessage("你点了强化");
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l进化功能")) {
                        e.getWhoClicked().sendMessage("你点了进化");
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l属性功能")) {
                        e.getWhoClicked().sendMessage("你点了属性");
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l洗练功能")) {
                        e.getWhoClicked().sendMessage("你点了洗练");
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l精炼功能")) {
                        e.getWhoClicked().sendMessage("你点了精炼");
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l境界功能")) {
                        e.getWhoClicked().sendMessage("你点了境界");
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l称号功能")) {
                        e.getWhoClicked().sendMessage("你点了称号");
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l打孔功能")) {
                        e.getWhoClicked().sendMessage("你点了打孔");
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l镶嵌功能")) {
                        e.getWhoClicked().sendMessage("你点了镶嵌");
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l封印功能")) {
                        e.getWhoClicked().sendMessage("你点了封印");
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l合成功能")) {
                        e.getWhoClicked().sendMessage("你点了合成");
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l锻造功能")) {
                        e.getWhoClicked().sendMessage("你点了锻造");
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l疲劳功能")) {
                        e.getWhoClicked().sendMessage("你点了疲劳");
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l熟练功能")) {
                        e.getWhoClicked().sendMessage("你点了熟练");
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l分解功能")) {
                        e.getWhoClicked().sendMessage("你点了分解");
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l翅膀功能")) {
                        e.getWhoClicked().sendMessage("你点了翅膀");
                    }
                    else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l性别功能")) {
                        e.getWhoClicked().sendMessage("你点了性别");
                    }
                    else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l渡劫功能")) {
                        e.getWhoClicked().sendMessage("你点了渡劫");
                    }
                    else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l飞升功能")) {
                        e.getWhoClicked().sendMessage("你点了飞升");
                    }
                    else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§l商城功能")) {
                        e.getWhoClicked().sendMessage("你点了商城");
                    }
                }
            }
        }
    }
}
