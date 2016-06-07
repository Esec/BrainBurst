package com.quicklight.BrainBurst;

import com.quicklight.Commands.commands;
import com.quicklight.Listener.brainburstListener;
import com.quicklight.SQL.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Administrator on 2016/6/6 0006.
 */
public class Main extends JavaPlugin {
    public static JavaPlugin handle;
    public static MySQL mysql;
    //MySQL mysql = new MySQL(this);
    commands bbcommands = new commands(this);
    public static String prefix = "[加速世界]";
    private String datebase = getConfig().getString("mysql.datebase");
    private String username = getConfig().getString("mysql.user");
    private String password = getConfig().getString("mysql.password");
    private String pr = "§2§m§l  §2§m§l  §2§m§l  §4§m§l  §4§m§l  §4§m§l  §5§m§l  §5§m§l  §5§m§l   §6§l[§a§l加速世界§6§l]§5§m§l  §5§m§l  §5§m§l  §4§m§l  §4§m§l  §4§m§l  §2§m§l  §2§m§l §2§m§l ";

    public String getPr() {
        return pr;
    }

    public void setPr(String pr) {
        this.pr = pr;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDatebase() {
        return datebase;
    }

    public void setDatebase(String datebase) {
        this.datebase = datebase;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        handle = this;
        mysql = new MySQL(this);
        System.out.println(prefix + ChatColor.RED + "插件启动中");
        Bukkit.getPluginManager().registerEvents(new brainburstListener(), this);
        mysql.openConnection();
        mysql.createTable();
    }

    @Override
    public void onDisable() {
        System.out.println(prefix + ChatColor.RED + "插件卸载中");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("menu")) {
                bbcommands.menu(sender, command, label, args, mysql);
                return true;
            } else if (args[0].equalsIgnoreCase("admin")) {
                bbcommands.admin(sender, command, label, args, mysql);
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                bbcommands.reload(sender, command, label, args, mysql);
                return true;
            } else {
                bbcommands.showerror(sender, command, label, args, mysql);
                return true;
            }
        } else {
            return false;
        }
    }
}
