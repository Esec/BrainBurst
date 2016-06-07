package com.quicklight.Listener;

import com.quicklight.SQL.MySQL;
import com.sun.org.glassfish.external.statistics.annotations.Reset;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

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
            mysql.pvp(killer.getName(),killed.getName());
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
                },20*60*30);
            }
        }
    }

    @EventHandler
    void onDamage(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player){
            if(map.get(e.getDamager())>=3){
                e.setCancelled(true);
            }
        }
    }
}
