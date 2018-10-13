package io.github.kevinlovely.kd.core;

import io.github.kevinlovely.kd.KDataAPI;
import io.github.kevinlovely.kd.data.Data;
import java.io.File;
import java.util.HashMap;

public class ConfigBase {

    private KDataAPI plugin;
    private HashMap<String, HashMap> table = new HashMap();

    public ConfigBase (KDataAPI plugin) {
        this.plugin = plugin;
    }
    public void setupConfig() {
        plugin.log("正在加载配置文件..." );
        if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
            plugin.saveDefaultConfig();
            plugin.log("初始化配置文件成功!" ,true);
        }
        plugin.reloadConfig();
        plugin.log("加载配置文件成功!" ,true);
        plugin.log("正在读取配置数据...");
        setupAllTable(true);
        plugin.log("读取成功: " + table.size() + " 个表格", true);
    }

    public void setupAllTable(boolean b) {
        HashMap allTable = new HashMap();
        for (String tableName : plugin.getConfig().getConfigurationSection("Table").getKeys(false)) {
            HashMap allData = getAllDataByTableName(tableName, b);
            allTable.put(tableName, allData);
            if (b) plugin.log("成功读取表格: " + tableName + "[" + allData.size() + "个数据]");
        }
        table = allTable;
        if (plugin.isLoad) plugin.getMysqlBase().setupAllTable();
    }

    public HashMap getAllDataByTableName(String n, boolean b) {
        HashMap allData = new HashMap();
        Data data;
        int i = 0;
        for(String dataName : plugin.getConfig().getConfigurationSection("Table." + n).getKeys(false)) {
            if (plugin.getConfig().get("Table." + n + "." + dataName + ".Type").equals("STRING")) {
                data = new Data(Data.DataType.STRING);
                data.setDefaultData(plugin.getConfig().getString("Table." + n + ".Default"));
            } else if (plugin.getConfig().get("Table." + n + "." + dataName + ".Type").equals("INT")) {
                data = new Data(Data.DataType.INT);
                data.setDefaultData(plugin.getConfig().getInt("Table." + n + ".Default"));
            } else {
                plugin.log("[数据加载失败]Data:" + n + " 配置的Type不是一个有效的类型[STRING/INT]!", false);
                continue;
            }
            data.setName(dataName);
            i++;
            allData.put(i, data);
            if (b) plugin.log("成功读取数据: " + dataName + "[" + plugin.getConfig().get("Table." + n + "." + dataName + ".Type") + "]");
        }
        return allData;
    }

    public void createData(String name, String table, String type, String def) {
        if (!isTable(table)) createTable(table);
        plugin.getConfig().set("Table." + table + "." + name, null);
        plugin.getConfig().set("Table." + table + "." + name + ".Type", type);
        if (type.equals("INT")) {
            plugin.getConfig().set("Table." + table + "." + name + ".Default", Integer.parseInt(def));
        } else {
            plugin.getConfig().set("Table." + table + "." + name + ".Default", String.valueOf(def));
        }
        plugin.saveConfig();
        plugin.reloadConfig();
        setupAllTable(false);
    }

    private void createTable(String name) {
        plugin.getConfig().set("Table." + name, null);
        plugin.saveConfig();
        plugin.reloadConfig();
        setupAllTable(false);
    }

    public boolean isData(String name) {
        for (HashMap.Entry e : table.entrySet()) {
            HashMap thisTable = (HashMap) e.getValue();
            for (Object k: thisTable.keySet()) {
                Data data = (Data) thisTable.get(k);
                if (data.getName().equals(name)) return true;
            }
        }
        return false;
    }

    public boolean isData(String name, String tb) {
        for (HashMap.Entry e : table.entrySet()) {
            HashMap thisTable = (HashMap) e.getValue();
            if (!(e.getKey().equals(tb))) continue;
            for (Object k: thisTable.keySet()) {
                Data data = (Data) thisTable.get(k);
                if (data.getName().equals(name)) return true;
            }
        }
        return false;
    }
    public boolean isTable(String name) {
        for (HashMap.Entry e : table.entrySet()) {
            if (e.getKey().equals(name)) return true;
        }
        return false;
    }

    public HashMap getAllTable() {
        return table;
    }

    public Data.DataType getDataType(String dataName, String table) {
        if (plugin.getConfig().getString("Table." + table + "." + dataName + ".Type").equals("INT")) {
            return Data.DataType.INT;
        } else {
            return Data.DataType.STRING;
        }
    }

    public Object getDataDef(String dataName, String table) {
        if (getDataType(dataName, table).equals(Data.DataType.INT)) {
            return plugin.getConfig().getInt("Table." + table + "." + dataName + ".Type");
        } else {
            return plugin.getConfig().getString("Table." + table + "." + dataName + ".Type");
        }
    }

}
