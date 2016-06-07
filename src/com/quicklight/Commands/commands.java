package com.quicklight.Commands;

import com.quicklight.BrainBurst.Main;
import com.quicklight.SQL.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Administrator on 2016/6/7 0007.
 */
public class commands {
    Main main;

    public commands(Main main) {
        this.main = main;
    }

    //玩家指令设计为GUI
    public void menu(CommandSender sender, Command command, String label, String[] args, MySQL mySQL) {
        if (args.length == 1) {
            sender.sendMessage(main.getPr());
            sender.sendMessage("§a§l/" + label + " menu player      §d§l-§e§l 打开面板");
        } else if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("player")) {
                //跳转GUI
                mySQL.playergui(sender);
            }
        }
    }

    //管理员指令
    public void admin(CommandSender sender, Command command, String label, String[] args, MySQL mySQL) {
        if (args.length == 1) {
            sender.sendMessage(main.getPr());
            sender.sendMessage("§a§l/" + label + " admin point      §d§l-§e§l 管理玩家货币");
        } else if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("point")) {
                if (sender.hasPermission("brainburst.admin") || sender.isOp()) {
                    //跳转adminpoint
                    adminpoint(sender, command, label, args, mySQL);
                } else {
                    noPermission();
                }
            }
        }
    }

    //指令adminpoint
    private void adminpoint(CommandSender sender, Command command, String label, String[] args, MySQL mySQL) {
        if (args.length == 2) {
            sender.sendMessage(main.getPr());
            sender.sendMessage("§d§l-§e§l 货币代码【1-魂】【2-罪】【3-炼】");
            sender.sendMessage("§a§l/" + label + " admin point add    【玩家】【货币】 【数量】      §d§l-§e§l 增加指定玩家指定货币");
            sender.sendMessage("§a§l/" + label + " admin point addll  【货币】【数量】              §d§l-§e§l 增加所有玩家指定货币");
            sender.sendMessage("§a§l/" + label + " admin point remove 【玩家】【货币】 【数量】      §d§l-§e§l 删除指定玩家指定货币");
            sender.sendMessage("§a§l/" + label + " admin point select 【玩家】      §d§l-§e§l 查询指定玩家货币");
            sender.sendMessage("§a§l/" + label + " admin point clear  【玩家】【货币】      §d§l-§e§l 清空指定玩家指定货币");
        } else if (args.length >= 3) {
            if (args[2].equalsIgnoreCase("add")) {
                //跳转增加
                mySQL.pointadd(sender, args[3], args[4], args[5]);
            } else if (args[2].equalsIgnoreCase("addall")) {
                //跳转增加全体
                mySQL.pointaddall(sender, args[3], args[4]);
            } else if (args[2].equalsIgnoreCase("remove")) {
                //跳转删除
                mySQL.pointdelete(sender, args[3], args[4], args[5]);
            } else if (args[2].equalsIgnoreCase("select")) {
                //跳转查询
                mySQL.pointselect(sender, args[3]);
            } else if (args[2].equalsIgnoreCase("clear")) {
                //跳转清空
                mySQL.pointclear(sender, args[3], args[4]);
            }
        }
    }

    //重载插件

    public void reload(CommandSender sender, Command command, String label, String[] args, MySQL mySQL) {
    }

    //指令错误
    public void showerror(CommandSender sender, Command command, String label, String[] args, MySQL mySQL) {
        sender.sendMessage(main.getPr());
        sender.sendMessage("§a§l/" + label + " menu       §d§l-§e§l 打开BB菜单");
        sender.sendMessage("§a§l/" + label + " admin     §d§l-§e§l 管理员指令");
        sender.sendMessage("§a§l/" + label + " reload     §d§l-§e§l 插件重载");
    }

    //没有权限
    public void noPermission() {
        System.out.println(Main.prefix + "§4§l你没有权限这么做");
    }
}
