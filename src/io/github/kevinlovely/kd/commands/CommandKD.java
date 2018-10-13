package io.github.kevinlovely.kd.commands;

import io.github.kevinlovely.kd.KDataAPI;
import io.github.kevinlovely.kd.data.Data;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandKD implements CommandExecutor {

    public KDataAPI plugin;
    public CommandKD (KDataAPI plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        if (!cmd.getName().equalsIgnoreCase("kd")) return true;
        if (!sender.isOp() && !sender.hasPermission("kd.cmd")) {
            sender.sendMessage("§bKDataAPI §f>> §6抱歉, 你没有 kd.cmd 权限.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")){
            sender.sendMessage("§bKDataAPI §f>> §b正在重载配置...");
            plugin.getConfigBase().setupAllTable(false);
            sender.sendMessage("§bKDataAPI §f>> §a重载成功!");
            return true;
        }

        if (args.length == 5 && args[0].equalsIgnoreCase("new")){

            if (!(args[3].equals("STRING")) && !(args[3].equals("INT"))) {
                sender.sendMessage("§bKDataAPI §f>> §e数据类型§6需要为[§cSTRING(文本)/INT(整数)§6].");
                return true;
            }
            if (args[3].equals("INT") && !isNumeric(args[4])) {
                sender.sendMessage("§bKDataAPI §f>> §6数据为INT, 则§c默认值§6需要为一个§c整数§6.");
                return true;
            }
            if (plugin.getConfigBase().isData(args[2], args[1])) {
                sender.sendMessage("§bKDataAPI §f>> §6数据 §c" + args[2] + " §6已经存在!");
                return true;
            }
            plugin.getConfigBase().createData(args[2], args[1], args[3], args[4]);
            sender.sendMessage("§bKDataAPI §f>> §a创建数据 §b" + args[2] + " §a成功!");
            return true;
        }
        if (args.length == 6 && args[0].equalsIgnoreCase("set")){

            if (!plugin.getConfigBase().isTable(args[1])) {
                sender.sendMessage("§bKDataAPI §f>> §6表格 §c" + args[1] + " §6不存在.");
                return true;
            }
            if (!plugin.getConfigBase().isData(args[2])) {
                sender.sendMessage("§bKDataAPI §f>> §6数据 §c" + args[2] + " §6不存在.");
                return true;
            }

            if (!(args[3].equals("+")) && !(args[3].equals("-")) && !(args[3].equals(">"))) {
                sender.sendMessage("§bKDataAPI §f>> §e操作符§6需要为[§c+(加)/-(减)/>(设置)§6].");
                return true;
            }

            if (plugin.getConfigBase().getDataType(args[2], args[1]).equals(Data.DataType.INT) && !(isNumeric(args[4]))) {
                sender.sendMessage("§bKDataAPI §f>> §6数据的类型是INT, 需要整数类型的值.");
                return true;
            }
            if (!(plugin.getConfigBase().getDataType(args[2], args[1]).equals(Data.DataType.INT)) && (args[3].equals("+") || args[3].equals("-"))) {
                sender.sendMessage("§bKDataAPI §f>> §b只有类型为整数的数据才能使用+/-操作符.");
                return true;
            }
            if (args[3].equals(">")) {
                args[4] = args[4].replaceAll("[空格]", " ");
                args[4] = args[4].replaceAll("[换行]", "\n");
                args[4] = args[4].replaceAll("&", "§");
                plugin.getMysqlBase().setData(args[2], args[4], args[1], args[5]);
                sender.sendMessage("§bKDataAPI §f>> §a[>]数据修改成功.");
                return true;
            }
            if (args[3].equals("-")) {
                int i = (int) plugin.getMysqlBase().getData(args[2], args[1], args[5]) - Integer.parseInt(args[4]);
                plugin.getMysqlBase().setData(args[2], String.valueOf(i), args[1], args[5]);
                sender.sendMessage("§bKDataAPI §f>> §a[-]数据缩减成功.");
                return true;
            }

            if (args[3].equals("+")) {
                int i = (int) plugin.getMysqlBase().getData(args[2], args[1], args[5]) + Integer.parseInt(args[4]);
                plugin.getMysqlBase().setData(args[2], String.valueOf(i), args[1], args[5]);
                sender.sendMessage("§bKDataAPI §f>> §a[+]数据增加成功.");
                return true;
            }

            return true;
        }

        sendHelpMessage(sender);
        return true;
    }

    private void sendHelpMessage(CommandSender p) {
        p.sendMessage("§f§m========§b KDataAPI §f= §c使用说明 §f§m========");
        p.sendMessage("§d*创建一个数据:");
        p.sendMessage("§b/kd §cnew §e<表格> <数据名称> <数据类型> <默认值>");
        p.sendMessage("§b[数据类型:STRING(文本)/INT(整数)]");
        p.sendMessage("§d*设置一个玩家的数据:");
        p.sendMessage("§b/kd §cset §e<表格> <数据名称> <操作符> <值> <玩家>");
        p.sendMessage("§b[操作符: >(设置, STRING/INT)/+-(加减, INT)]");
        p.sendMessage("§b/kd §creload §e重载配置文件");
        p.sendMessage("§7By kevinlovely[凯文酱].QQ 3407053348");
        p.sendMessage("§f§m======================================");
    }

    private boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){ return false; }
        return true;
    }

}
