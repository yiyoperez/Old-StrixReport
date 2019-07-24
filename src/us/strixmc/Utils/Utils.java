package us.strixmc.Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import us.strixmc.SReport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Utils implements Listener {

    public static HashMap<String, String> test;
    public static List<Player> toggledReports;
    public static HashMap<Player, Integer> Ramount;

    static {
        Utils.Ramount = new HashMap<>();
        Utils.toggledReports = new ArrayList<>();
        Utils.test = new HashMap<>();
    }

    public static String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> cList(List<String> list2) {
        ArrayList list = new ArrayList();
        for (String s : list2) {
            list.add(c(s));
        }
        return list;
    }

    public static void checkGUI() {
        if (SReport.instance.getConfig().getBoolean("toggle.report-gui.enabled")) {
            int rows = SReport.instance.getConfig().getInt("GUI.rows");
            if (SReport.instance.getConfig().getString("GUI.rows").equals("0")) {
                SReport.instance.getConfig().set("GUI.rows", Integer.parseInt("1"));
                SReport.instance.saveConfig();
                SReport.instance.reloadConfig();
            } else if (rows > 6) {
                SReport.instance.getConfig().set("GUI.rows", Integer.parseInt("6"));
                SReport.instance.saveConfig();
                SReport.instance.reloadConfig();
            }
            if (SReport.instance.getConfig().getString("GUI.name") == null) {
                SReport.instance.getConfig().createSection("GUI.name");
                SReport.instance.getConfig().set("GUI.name", "&cStrix&4Report &7GUI");
                SReport.instance.saveConfig();
                SReport.instance.reloadConfig();
            }
            if (SReport.instance.getConfig().getString("GUI.items").equalsIgnoreCase("[]") || SReport.instance.getConfig().getString("GUI.items").equalsIgnoreCase("''") || SReport.instance.getConfig().getString("GUI.items").equalsIgnoreCase("")) {
                SReport.instance.getConfig().createSection("GUI.items.example.name");
                SReport.instance.getConfig().createSection("GUI.items.example.id");
                SReport.instance.getConfig().createSection("GUI.items.example.slot");
                SReport.instance.getConfig().createSection("GUI.items.example.amount");
                SReport.instance.getConfig().createSection("GUI.items.example.data");
                SReport.instance.getConfig().createSection("GUI.items.example.lore");
                SReport.instance.getConfig().createSection("GUI.items.example2.name");
                SReport.instance.getConfig().createSection("GUI.items.example2.id");
                SReport.instance.getConfig().createSection("GUI.items.example2.slot");
                SReport.instance.getConfig().set("GUI.items.example.name", "&eFull Item");
                SReport.instance.getConfig().set("GUI.items.example.id", Integer.parseInt("35"));
                SReport.instance.getConfig().set("GUI.items.example.amount", Integer.parseInt("2"));
                SReport.instance.getConfig().set("GUI.items.example.data", Integer.parseInt("14"));
                SReport.instance.getConfig().set("GUI.items.example.slot", Integer.parseInt("1"));
                SReport.instance.getConfig().set("GUI.items.example.lore", Arrays.asList("", "&6This is a lore", ""));
                SReport.instance.getConfig().set("GUI.items.example2.name", "&eSimple Item");
                SReport.instance.getConfig().set("GUI.items.example2.id", Integer.parseInt("332"));
                SReport.instance.getConfig().set("GUI.items.example2.slot", Integer.parseInt("0"));
                SReport.instance.saveConfig();
                SReport.instance.reloadConfig();
            }
        }
    }

    public static void updateAmount(Player target){
        if (!Utils.Ramount.containsKey(target)) {
            Utils.Ramount.put(target, 0);
        }
        if (Utils.Ramount.containsKey(target)) {
            int amount = Utils.Ramount.get(target);
            if (amount != 1 || Utils.Ramount.get(target) != null) {
                Utils.Ramount.put(target, amount + 1);
            }
        }
    }

    public static String Prefix(String s) {
        if (SReport.instance.getConfig().getBoolean("toggle.prefix.enabled")) {
            return c(SReport.instance.getConfig().getString("prefix") + s);
        }
        return c(s);
    }

}
