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

/*    @EventHandler
    void onM(InventoryClickEvent e){
        if(e.getClickedInventory().getName().equals("我是一个GUI")){
            e.setCancelled(true);//禁止拖动
            if(e.getCurrentItem()!=null) {//点到空气报错
            if(e.getCurrentItem().getItemMeta().hasDisplayName()) {//判断是不是有名字
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals("我是个按钮")){
                e.getWhoClicked().sendMessage("你点了我");
            }
            }
            }
        }
    }

    void onCommand(Player p) {
        Inventory inv = Bukkit.createInventory(p, 9, "我是一个GUI");
        p.openInventory(inv);
        ItemStack button1 = new ItemStack(Material.BOOK);
        ItemMeta meta = button1.getItemMeta();
        meta.setDisplayName("我是一个按钮");
        meta.setLore(new ArrayList<String>() {
            {
                add("我是第一行介绍");
                add("我是第二行介绍");
            }
        });
        button1.setItemMeta(meta);//这个很重要 相当于保存
        inv.addItem(button1);
        //禁止拿下来
    }*/
}
