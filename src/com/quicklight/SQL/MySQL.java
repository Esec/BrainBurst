package com.quicklight.SQL;

import com.quicklight.BrainBurst.Main;
import org.bukkit.Bukkit;
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
            System.out.println(sql);
            if (rs.next()) {
                score = rs.getInt(1);
            }
            sql = "UPDATE brainburst SET " + arg1 + "=?+" + score + " WHERE username=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, arg2);
            statement.setString(2, arg);
            int i = statement.executeUpdate();
            if (i == 1) {
                sender.sendMessage(main.getPr() + "为" + arg + "添加" + arg + arg2 + "点成功");
            } else {
                sender.sendMessage(main.getPr() + "为" + arg + "添加" + arg + arg2 + "点失败");
            }
        } catch (SQLException e1) {
            sender.sendMessage(Main.prefix + "§4§l查询失败请检查数据库连接");
        }
    }

    //玩家PVP奖惩
    public void pvp(String killer, String killed) {
        int hun, zui;
        try {
            connection = openConnection();
            //击杀
            String sql = "SELECT hun,zui FROM brainburst WHERE username='" + killer + "';";
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();
            if (rs.next()) {
                hun = rs.getInt(1);
                zui = rs.getInt(2);
                sql = "UPDATE brainburst SET (hun,zui) VALUES(" + hun + "+1," + zui + "+10)";
                statement = connection.prepareStatement(sql);
                int i = statement.executeUpdate();
                if (i == 1) {
                   Bukkit.getPlayer(killer).sendMessage(main.getPr() + "你击杀了" + killed);
                }
            }

            //被击杀
            sql = "SELECT hun,zui FROM brainburst WHERE username='" + killed + "';";
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();
            if (rs.next()) {
                hun = rs.getInt(1);
                zui = rs.getInt(2);
                sql = "UPDATE brainburst SET (hun,zui) VALUES(" + hun + "-2," + zui + "-20)";
                statement = connection.prepareStatement(sql);
                int i = statement.executeUpdate();
                if (i == 1) {
                    Bukkit.getPlayer(killed).sendMessage(main.getPr() + "你被" + killer + "击杀了");
                }
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
}
