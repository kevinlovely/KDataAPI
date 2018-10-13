package io.github.kevinlovely.kd.data;

import io.github.kevinlovely.kd.KDataAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class Placeholder extends PlaceholderExpansion {

    public KDataAPI plugin;
    public Placeholder (KDataAPI plugin) { this.plugin = plugin; }

    @Override
    public String getIdentifier() {
        return "kd";
    }

    @Override
    public String getAuthor() {
        return "kevinlovely";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    public String onPlaceholderRequest(Player p, String s) {
        if (p == null) return "获取数据失败#100";
        if (s == null) return "获取数据失败#101";
        String[] a = s.split(":");
        if (!plugin.getConfigBase().isTable(a[0]) || !plugin.getConfigBase().isData(a[1], a[0])) return "获取数据失败#103";
        String r = null;
        r = String.valueOf(plugin.getMysqlBase().getData(a[1], a[0], p.getName()));
        return (r != null) ? r : "获取数据失败#104";
    }

}
