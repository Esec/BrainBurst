package com.quicklight.SQL;

import com.quicklight.BrainBurst.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.sql.*;


/**
 * Created by Administrator on 2016/5/20.
 */
public class MySQL {
    Main main;
    PreparedStatement statement = null;
    Connection connection = null;
    ResultSet rs = null;
    String brainburst = "CREATE TABLE IF NOT EXISTS`brainburst`(`id` int(11) NOT NULL auto_increment, `username` varchar(255) NOT NULL, `hun` double(11,2) NOT NULL default '100.00', `zui` double(11,2) default '200.00', `lian` double(11,2) default '0.00', PRIMARY KEY  (`id`)) ENGINE=MyISAM DEFAULT CHARSET=gbk;";

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
}
