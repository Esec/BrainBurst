package com.quicklight.SQL;

import com.quicklight.BrainBurst.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.*;
import java.util.ArrayList;


/**
 * Created by Administrator on 2016/5/20.
 */
public class MySQL {
    Main main;
    PreparedStatement statement = null;
    Connection connection = null;
    ResultSet rs = null;
    String brainburst = "CREATE TABLE IF NOT EXISTS`brainburst`(`id` int(11) NOT NULL auto_increment, `username` varchar(255) NOT NULL, `hun` double(11,2) NOT NULL default '100.00', `zui` double(11,2) default '200.00', `lian` double(11,2) default '0.00', PRIMARY KEY  (`id`)) ENGINE=MyISAM DEFAULT CHARSET=gbk;";
    String welcome = " §2§m§l  §3§m§l  §4§lWelcome §5§lto §7§lthe §6§lAccel World§3§m§l  §2§m§l §2§m§l ";

    public MySQL(Main main) {
        this.main = main;
    }

    //打开数据库连接
    public synchronized Connection openConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = (Connection) DriverManager.getConnection(main.getDatebase(), main.getUsername(), main.getPassword());
                return connection;
            }
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("§c连接数据库失败！");
            return connection;
        }
        return connection;
    }

    //创建数据库表
    public synchronized void createTable() {
        if (connection != null) {
            try {
                PreparedStatement createtable = connection.prepareStatement(brainburst);
                createtable.execute();
                createtable.close();
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage("§c创建数据库表失败！");
            } finally {
                closeConnection();
            }
        }
    }

    //关闭数据库连接
    private synchronized void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("§c关闭数据库失败！");
        }

    }

    //玩家PVP奖惩BUG
    public void pvp(String killer, String killed) {
        //定义变量用于存储玩家货币,sql语句
        int killerhun = 0, killerzui = 0, killedhun = 0, killedzui = 0;
        String sql;
        //获取玩家货币
        try {
            sql = "SELECT hun,zui FROM brainburst WHERE username='" + killed + "';";
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();
            if (rs.next()) {
                killedhun = rs.getInt(1);
                killedzui = rs.getInt(2);
            }
            sql = "SELECT hun,zui FROM brainburst WHERE username='" + killer + "';";
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();
            if (rs.next()) {
                killerhun = rs.getInt(1);
                killerzui = rs.getInt(2);
            }
            if (killedhun != 0 || killedzui != 0) {
                try {
                    statement = connection.prepareStatement(sql);
                    //货币计算
                    killerhun = killerhun + 1;
                    killerzui = killerzui + 10;
                    killedhun = killedhun - 2;
                    killedzui = killedzui - 20;
                    //判断玩家被击杀后货币是否小于0
                    if (killedhun < 0) {
                        killedhun = 0;
                    }
                    if (killedzui < 0) {
                        killedzui = 0;
                    }
                    String killersql = "UPDATE brainburst SET hun=" + killerhun + ",zui=" + killerzui + " WHERE username='" + killer + "';";
                    String killedsql = "UPDATE brainburst SET hun=" + killedhun + ",zui=" + killedzui + " WHERE username='" + killed + "';";
                    statement = connection.prepareStatement(killersql);
                    int killeri = statement.executeUpdate();
                    statement = connection.prepareStatement(killedsql);
                    int killedi = statement.executeUpdate();
                    if (killeri == 1 || killedi == 1) {
                        Bukkit.getPlayer(killer).sendMessage(main.getPr());
                        Bukkit.getPlayer(killer).sendMessage(ChatColor.AQUA + "你击杀了" + killed);
                        Bukkit.getPlayer(killer).sendMessage(ChatColor.AQUA + "魂+1，罪+10");
                        Bukkit.getPlayer(killed).sendMessage(main.getPr());
                        Bukkit.getPlayer(killed).sendMessage(ChatColor.DARK_AQUA + "你被" + killer + "击杀了");
                        Bukkit.getPlayer(killed).sendMessage(ChatColor.DARK_AQUA + "魂-2，罪-20");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                Bukkit.getPlayer(killer).sendMessage(main.getPr());
                Bukkit.getPlayer(killer).sendMessage(ChatColor.AQUA + "你击杀了" + killed);
                Bukkit.getPlayer(killer).sendMessage(ChatColor.AQUA + "但是没有获得任何奖励");
                Bukkit.getPlayer(killed).sendMessage(main.getPr());
                Bukkit.getPlayer(killed).sendMessage(ChatColor.DARK_AQUA + "你被" + killer + "击杀了");
                Bukkit.getPlayer(killed).sendMessage(ChatColor.DARK_AQUA + "可惜你身无分文");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //玩家加入服务器检测
    public void joinserver(String name) {
        try {
            connection = openConnection();
            String sql = "SELECT * FROM brainburst WHERE username=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            rs = statement.executeQuery();
            //如果不存在
            if (!rs.next()) {
                sql = "INSERT INTO brainburst (username) VALUES('" + name + "')";
                statement = connection.prepareStatement(sql);
                statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //增加指定玩家指定货币
    public void pointadd(CommandSender sender, String arg, String arg1, String arg2) {
        int score = 0;
        try {
            connection = openConnection();
            if (arg1.equalsIgnoreCase("1")) {
                arg1 = "hun";
            } else if (arg1.equalsIgnoreCase("2")) {
                arg1 = "zui";
            } else if (arg1.equalsIgnoreCase("3")) {
                arg1 = "lian";
            } else {
                sender.sendMessage(Main.prefix + "§4§l参数错误");
            }
            //查询数据库获取玩家默认积分
            String sql = "SELECT " + arg1 + " FROM brainburst WHERE username=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, arg);
            rs = statement.executeQuery();
            if (rs.next()) {
                score = rs.getInt(1);
            }
            sql = "UPDATE brainburst SET " + arg1 + "=?+" + score + " WHERE username=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, arg2);
            statement.setString(2, arg);
            int i = statement.executeUpdate();
            if (i == 1) {
                sender.sendMessage("为" + arg + "添加" + arg1 + arg2 + "点成功");
            } else {
                sender.sendMessage("为" + arg + "添加" + arg1 + arg2 + "点失败");
            }
        } catch (SQLException e1) {
            sender.sendMessage(Main.prefix + "§4§l查询失败请检查数据库连接");
        }
    }

    //增加所有玩家指定货币
    public void pointaddall(CommandSender sender, String arg, String arg1) {
        int score = 0;
        String sql, username;
        try {
            connection = openConnection();
            if (arg.equalsIgnoreCase("1")) {
                arg = "hun";
            } else if (arg.equalsIgnoreCase("2")) {
                arg = "zui";
            } else if (arg.equalsIgnoreCase("3")) {
                arg = "lian";
            } else {
                sender.sendMessage(Main.prefix + "§4§l参数错误");
            }
            //查询数据库内所有玩家信息
            sql = "SELECT username," + arg + " FROM brainburst";
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();
            System.out.println(sql);
            while (rs.next()) {
                username = rs.getString(1);
                score = rs.getInt(2);
                sql = "UPDATE brainburst SET " + arg + "=?+" + score + " WHERE username=?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, arg1);
                statement.setString(2, username);
                int i = statement.executeUpdate();
                if (i == 1) {
                    sender.sendMessage("为" + username + "添加" + arg + arg1 + "点成功");
                } else {
                    sender.sendMessage("为" + username + "添加" + arg + arg1 + "点失败");
                }
            }
        } catch (SQLException e) {
            sender.sendMessage(Main.prefix + "§4§l查询失败请检查数据库连接");
        }
    }

    //删除指定玩家指定货币
    public void pointdelete(CommandSender sender, String arg, String arg1, String arg2) {
        int score = 0;
        try {
            connection = openConnection();
            if (arg1.equalsIgnoreCase("1")) {
                arg1 = "hun";
            } else if (arg1.equalsIgnoreCase("2")) {
                arg1 = "zui";
            } else if (arg1.equalsIgnoreCase("3")) {
                arg1 = "lian";
            } else {
                sender.sendMessage(Main.prefix + "§4§l参数错误");
            }
            //查询数据库获取玩家默认积分
            String sql = "SELECT " + arg1 + " FROM brainburst WHERE username=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, arg);
            rs = statement.executeQuery();
            if (rs.next()) {
                score = rs.getInt(1);
                if (score - Integer.parseInt(arg2) < 0) {
                    score = 0;
                } else {
                    score = score - Integer.parseInt(arg2);
                }

            }
            sql = "UPDATE brainburst SET " + arg1 + "=" + score + " WHERE username=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, arg);
            int i = statement.executeUpdate();
            if (i == 1) {
                sender.sendMessage("为" + arg + "删除" + arg1 + arg2 + "点成功");
            } else {
                sender.sendMessage("为" + arg + "删除" + arg1 + arg2 + "点失败");
            }
        } catch (SQLException e1) {
            sender.sendMessage(Main.prefix + "§4§l查询失败请检查数据库连接");
        }
    }

    //查询指定玩家货币
    public void pointselect(CommandSender sender, String arg) {
        try {
            connection = openConnection();
            //查询数据库获取玩家默认积分
            String sql = "SELECT hun,zui,lian FROM brainburst WHERE username=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, arg);
            rs = statement.executeQuery();
            if (rs.next()) {
                int hun = rs.getInt(1);
                int zui = rs.getInt(2);
                int lian = rs.getInt(3);
                sender.sendMessage("玩家" + arg + "共有魂:" + hun + "点,罪:" + zui + "点,炼:" + lian + "点");
            }
        } catch (SQLException e1) {
            sender.sendMessage(Main.prefix + "§4§l查询失败请检查数据库连接");
        }
    }

    //情况指定玩家指定货币
    public void pointclear(CommandSender sender, String arg, String arg1) {
        int score = 0;
        try {
            connection = openConnection();
            if (arg1.equalsIgnoreCase("1")) {
                arg1 = "hun";
            } else if (arg1.equalsIgnoreCase("2")) {
                arg1 = "zui";
            } else if (arg1.equalsIgnoreCase("3")) {
                arg1 = "lian";
            } else {
                sender.sendMessage(Main.prefix + "§4§l参数错误");
            }
            String sql = "UPDATE brainburst SET " + arg1 + "=0 WHERE username=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, arg);
            int i = statement.executeUpdate();
            if (i == 1) {
                sender.sendMessage("为" + arg + "清空" + arg1 + "成功");
            } else {
                sender.sendMessage("为" + arg + "清空" + arg1 + "失败");
            }
        } catch (SQLException e1) {
            sender.sendMessage(Main.prefix + "§4§l查询失败请检查数据库连接");
        }
    }

    //playergui
    public void playergui(CommandSender sender) {
        Player p;
        if (sender instanceof Player) {
            p = (Player) sender;
        } else {
            return;
        }
        Inventory inv = Bukkit.createInventory(p, 36, welcome);
        p.openInventory(inv);
        ItemStack qianghua = new ItemStack(Material.ANVIL);
        ItemMeta meta = qianghua.getItemMeta();
        meta.setDisplayName("§5§l强化功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入强化功能");
            }
        });
        qianghua.setItemMeta(meta);
        inv.setItem(0, qianghua);

        ItemStack jinhua = new ItemStack(Material.DIAMOND);
        meta = jinhua.getItemMeta();
        meta.setDisplayName("§5§l进化功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入进化功能");
            }
        });
        jinhua.setItemMeta(meta);
        inv.setItem(2, jinhua);

        ItemStack shuxing = new ItemStack(Material.DIAMOND_SWORD);
        meta = shuxing.getItemMeta();
        meta.setDisplayName("§5§l属性功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入属性功能");
            }
        });
        shuxing.setItemMeta(meta);
        inv.setItem(4, shuxing);

        ItemStack xilian = new ItemStack(Material.APPLE);
        meta = xilian.getItemMeta();
        meta.setDisplayName("§5§l洗练功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入洗练功能");
            }
        });
        xilian.setItemMeta(meta);
        inv.setItem(6, xilian);

        ItemStack jinglian = new ItemStack(Material.GOLDEN_APPLE);
        meta = jinglian.getItemMeta();
        meta.setDisplayName("§5§l精炼功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入精炼功能");
            }
        });
        jinglian.setItemMeta(meta);
        inv.setItem(8, jinglian);

        ItemStack jingjie = new ItemStack(Material.BEACON);
        meta = jingjie.getItemMeta();
        meta.setDisplayName("§5§l境界功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入境界功能");
            }
        });
        jingjie.setItemMeta(meta);
        inv.setItem(9, jingjie);

        ItemStack chenghao = new ItemStack(Material.DAYLIGHT_DETECTOR);
        meta = chenghao.getItemMeta();
        meta.setDisplayName("§5§l称号功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入称号功能");
            }
        });
        chenghao.setItemMeta(meta);
        inv.setItem(11, chenghao);

        ItemStack dakong = new ItemStack(Material.GOLD_PICKAXE);
        meta = dakong.getItemMeta();
        meta.setDisplayName("§5§l打孔功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入打孔功能");
            }
        });
        dakong.setItemMeta(meta);
        inv.setItem(13, dakong);

        ItemStack xiangqian = new ItemStack(Material.FLINT_AND_STEEL);
        meta = xiangqian.getItemMeta();
        meta.setDisplayName("§5§l镶嵌功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入镶嵌功能");
            }
        });
        xiangqian.setItemMeta(meta);
        inv.setItem(15, xiangqian);

        ItemStack fengying = new ItemStack(Material.NAME_TAG);
        meta = fengying.getItemMeta();
        meta.setDisplayName("§5§l封印功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入封印功能");
            }
        });
        fengying.setItemMeta(meta);
        inv.setItem(17, fengying);

        ItemStack hecheng = new ItemStack(Material.WORKBENCH);
        meta = hecheng.getItemMeta();
        meta.setDisplayName("§5§l合成功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入合成功能");
            }
        });
        hecheng.setItemMeta(meta);
        inv.setItem(18, hecheng);

        ItemStack duanzao = new ItemStack(Material.PISTON_STICKY_BASE);
        meta = duanzao.getItemMeta();
        meta.setDisplayName("§5§l锻造功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入锻造功能");
            }
        });
        duanzao.setItemMeta(meta);
        inv.setItem(20, duanzao);

        ItemStack pilao = new ItemStack(Material.CHORUS_PLANT);
        meta = pilao.getItemMeta();
        meta.setDisplayName("§5§l疲劳功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入疲劳功能");
            }
        });
        pilao.setItemMeta(meta);
        inv.setItem(22, pilao);

        ItemStack shulian = new ItemStack(Material.REDSTONE);
        meta = shulian.getItemMeta();
        meta.setDisplayName("§5§l熟练功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入熟练功能");
            }
        });
        shulian.setItemMeta(meta);
        inv.setItem(24, shulian);

        ItemStack fenjie = new ItemStack(Material.SHEARS);
        meta = fenjie.getItemMeta();
        meta.setDisplayName("§5§l分解功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入分解功能");
            }
        });
        fenjie.setItemMeta(meta);
        inv.setItem(26, fenjie);

        ItemStack chibang = new ItemStack(Material.FEATHER);
        meta = chibang.getItemMeta();
        meta.setDisplayName("§5§l翅膀功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入翅膀功能");
            }
        });
        chibang.setItemMeta(meta);
        inv.setItem(27, chibang);

        ItemStack xingbie = new ItemStack(Material.IRON_INGOT);
        meta = xingbie.getItemMeta();
        meta.setDisplayName("§5§l性别功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入性别功能");
            }
        });
        xingbie.setItemMeta(meta);
        inv.setItem(29, xingbie);

        ItemStack dujie = new ItemStack(Material.BLAZE_ROD);
        meta = dujie.getItemMeta();
        meta.setDisplayName("§5§l渡劫功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入渡劫功能");
            }
        });
        dujie.setItemMeta(meta);
        inv.setItem(31, dujie);

        ItemStack feisheng = new ItemStack(Material.NETHER_STAR);
        meta = feisheng.getItemMeta();
        meta.setDisplayName("§5§l飞升功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入飞升功能");
            }
        });
        feisheng.setItemMeta(meta);
        inv.setItem(33, feisheng);

        ItemStack shop = new ItemStack(Material.GOLD_NUGGET);
        meta = shop.getItemMeta();
        meta.setDisplayName("§5§l商城功能");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击进入商城功能");
            }
        });
        shop.setItemMeta(meta);
        inv.setItem(35, shop);
    }

    //qianghuagui
    public void qianghuagui(CommandSender sender) {
        Player p;
        if (sender instanceof Player) {
            p = (Player) sender;
        } else {
            return;
        }
        Inventory inv = Bukkit.createInventory(p, 9, "§5§l强化功能");
        p.openInventory(inv);
        ItemStack wuqi = new ItemStack(Material.ARROW);
        ItemMeta meta = wuqi.getItemMeta();
        meta.setDisplayName("§5§l强化武器");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击开始强化武器");
            }
        });
        wuqi.setItemMeta(meta);
        inv.setItem(0, wuqi);

        ItemStack toukui = new ItemStack(Material.DIAMOND_HELMET);
        meta = toukui.getItemMeta();
        meta.setDisplayName("§5§l强化头盔");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击开始强化头盔");
            }
        });
        toukui.setItemMeta(meta);
        inv.setItem(2, toukui);

        ItemStack xiongjia = new ItemStack(Material.DIAMOND_CHESTPLATE);
        meta = xiongjia.getItemMeta();
        meta.setDisplayName("§5§l强化胸甲");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击开始强化胸甲");
            }
        });
        xiongjia.setItemMeta(meta);
        inv.setItem(4, xiongjia);

        ItemStack hutui = new ItemStack(Material.DIAMOND_LEGGINGS);
        meta = hutui.getItemMeta();
        meta.setDisplayName("§5§l强化护腿");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击开始强化护腿");
            }
        });
        hutui.setItemMeta(meta);
        inv.setItem(6, hutui);

        ItemStack xiezi = new ItemStack(Material.DIAMOND_BOOTS);
        meta = xiezi.getItemMeta();
        meta.setDisplayName("§5§l强化鞋子");
        meta.setLore(new ArrayList<String>() {
            {
                add("§c§l点击开始强化鞋子");
            }
        });
        xiezi.setItemMeta(meta);
        inv.setItem(8, xiezi);
    }
}
