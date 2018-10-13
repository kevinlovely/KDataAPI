package io.github.kevinlovely.kd;

import io.github.kevinlovely.kd.commands.CommandKD;
import io.github.kevinlovely.kd.core.ConfigBase;
import io.github.kevinlovely.kd.core.MysqlBase;
import io.github.kevinlovely.kd.data.Placeholder;
import org.bukkit.plugin.java.JavaPlugin;

public class KDataAPI extends JavaPlugin {

    private CommandKD commandKD = new CommandKD(this);
    private ConfigBase configBase = new ConfigBase(this);
    private MysqlBase mysqlBase = new MysqlBase(this);
    public boolean isLoad = false;

    @Override
    public void onEnable() {

        log("[玩家动态数据] 插件开始加载!");
        log("插件版本: version1.0.0");
        log("作者: kevinlovely [QQ3407053348]");
        configBase.setupConfig();
        mysqlBase.setupMysql();
        log("注册命令接口...");
        getCommand("kd").setExecutor(new CommandKD(this));
        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new Placeholder(this).register();
            log("注册PlaceholderAPI成功!", true);
        } else {
            log("[变量注册出错]注册PlaceholderAPI失败!", false);
        }
        log("插件加载完成!");
        isLoad = true;

    }

    public void log(String m) {
        getServer().getConsoleSender().sendMessage("§bKDataAPI §f>> §b" + m);
    }
    public void log(String m, boolean c) {
        getServer().getConsoleSender().sendMessage("§bKDataAPI §f>> " + (c ? "§a" : "§c") + m);
    }

    public MysqlBase getMysqlBase() { return mysqlBase; }
    public ConfigBase getConfigBase() { return configBase; }

}
