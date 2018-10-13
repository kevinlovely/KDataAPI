package io.github.kevinlovely.kd.core;

import io.github.kevinlovely.kd.KDataAPI;
import io.github.kevinlovely.kd.data.Data;

import java.sql.*;
import java.util.HashMap;

public class MysqlBase {

    private KDataAPI plugin;
    private Statement s;
    public MysqlBase (KDataAPI plugin) { this.plugin = plugin; }

    Connection c = null;

    public void setupMysql() {

        plugin.log("开始连接数据库...");
        final String DB_URL = "jdbc:mysql://"
                + plugin.getConfig().getString("MysqlConnection.Host") + ":"
                + plugin.getConfig().getString("MysqlConnection.Port") + "/"
                + plugin.getConfig().getString("MysqlConnection.Database");
        final String USER = plugin.getConfig().getString("MysqlConnection.User");
        final String PASS = plugin.getConfig().getString("MysqlConnection.Password");
        plugin.log("加载Mysql驱动...");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            c = DriverManager.getConnection(DB_URL, USER, PASS);
            s = c.createStatement();
            plugin.log("连接数据库成功!", true);
            setupAllTable();
        } catch (ClassNotFoundException e) {
            plugin.log("[请检查Java核心/版本不兼容]加载JDBC驱动出错[ClassNotFoundException], 以下是报错信息:", false);
            e.printStackTrace();
        } catch (SQLException e) {
            plugin.log("[数据库配置错误/未创建库/初次加载]连接Mysql出错[SQLException], 以下是报错信息:", false);
            e.printStackTrace();
        }
    }

    public void setupAllTable() {
        HashMap table = plugin.getConfigBase().getAllTable();
        for (Object k : table.keySet()) { checkTable((String) k); }
    }

    private void checkTable(String name) {
        try {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + name + " (Player varchar(32),Name varchar(64),Data varchar(256),Type varchar(16));");
            if (!plugin.isLoad) plugin.log("检索Mysql表格: " + name + " 成功!");
        } catch (SQLException e) {
            plugin.log("[数据库出错]尝试创建表格: " + name + " 出错, 以下是报错信息:", false);
            e.printStackTrace();
        }
    }


    public Object getData(String dataname, String table, String playerName) {
        ResultSet rs;
        try {
            rs = s.executeQuery("SELECT * FROM " + table + " WHERE Player='" + playerName + "' AND Name='" + dataname + "'");
            while (rs.next()) {
                if (rs.getString("Type").equals("INT")) {
                    return rs.getInt("Data");
                } else {
                    return rs.getString("Data");
                }
            }
        } catch (SQLException e) {
            plugin.log("[数据库出错]查询玩家数据出错, 以下是报错信息:", false);
            e.printStackTrace();
        }
        String def = null;
        String type = null;
        if (plugin.getConfigBase().getDataType(dataname, table).equals(Data.DataType.INT)) {
            type = "INT";
            def = Integer.toString((Integer) plugin.getConfigBase().getDataDef(dataname, table));
        } else if (plugin.getConfigBase().getDataType(dataname, table).equals(Data.DataType.STRING)) {
            type = "STRING";
            def = (String) plugin.getConfigBase().getDataDef(dataname, table);
        }
        try {
            s.executeUpdate("INSERT INTO " + table + " (`Player`,`Name`,`Data`,`Type`) VALUES ('" + playerName + "'" + ", '"
                    + dataname + "', '" + def + "', '" + type + "')");
        } catch (SQLException e) {
            plugin.log("[数据库出错]插入玩家数据出错, 以下是报错信息:", false);
            e.printStackTrace();
        }
        return def;
    }

    public void setData(String name, String data, String table, String playerName) {
        getData(name, table, playerName);
        try {
            s.executeUpdate("UPDATE " + table +  " SET Data='" + data + "' WHERE Player='" + playerName + "' AND Name='" + name + "'");
        } catch (SQLException e) {
            plugin.log("[数据库出错]写入玩家数据出错, 以下是报错信息:", false);
            e.printStackTrace();
        }
    }

}
