package us.strixmc;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import us.strixmc.Commands.CommandReport;
import us.strixmc.Commands.CommandToggle;
import us.strixmc.Commands.StrixReport;
import us.strixmc.GUI.ReportGUI;
import us.strixmc.Utils.Cooldowns;
import us.strixmc.Utils.TxtFile;
import us.strixmc.Utils.UpdateChecker;
import us.strixmc.Utils.Utils;

import java.io.File;

public class SReport extends JavaPlugin implements Listener {

    public static Plugin instance;

    @Override
    public void onEnable() {
        instance = this;
        if (getConfig().getBoolean("check_updates")) {
            UpdateChecker.init(this, 42151).requestUpdateCheck().whenComplete((result, exception) -> {
                if (result.requiresUpdate()) {
                    this.getLogger().info(String.format("An update is available! StrixReport %s may be downloaded on SpigotMC", result.getNewestVersion()));
                    return;
                }

                UpdateChecker.UpdateReason reason = result.getReason();
                if (reason == UpdateChecker.UpdateReason.UP_TO_DATE) {
                    this.getLogger().info(String.format("Your version of StrixReport (%s) is up to date!", result.getNewestVersion()));
                } else if (reason == UpdateChecker.UpdateReason.UNRELEASED_VERSION) {
                    this.getLogger().info(String.format("Your version of StrixReport (%s) is more recent than the one publicly available. Are you on a development build?", result.getNewestVersion()));
                } else {
                    this.getLogger().warning("Could not check for a new version of StrixReport. Reason: " + reason);
                }
            });
        }
        CreateLog();
        configHeader();

        loadEvents();
        loadCommands();

        Cooldowns.createCooldown("report");

    }

    private void loadEvents() {
        Bukkit.getPluginManager().registerEvents(new ReportGUI(), this);
        Bukkit.getPluginManager().registerEvents(new StrixReport(), this);
        Bukkit.getPluginManager().registerEvents(new Utils(), this);
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void loadCommands() {
        getCommand("strixreport").setTabCompleter(new StrixReport());
        getCommand("togglereport").setExecutor(new CommandToggle());
        getCommand("report").setExecutor(new CommandReport());
        getCommand("strixreport").setExecutor(new StrixReport());
    }

    @EventHandler
    public void updateJoin(PlayerJoinEvent e) {
        if (getConfig().getBoolean("check_updates")) {
            if (e.getPlayer().isOp() || e.getPlayer().hasPermission(getConfig().getString("permissions.report.admin"))) {
                UpdateChecker.init(this, 42151).requestUpdateCheck().whenComplete((result, exception) -> {
                    if (result.requiresUpdate()) {
                        e.getPlayer().sendMessage(String.format(Utils.c("§cAn update is available! StrixReport %s may be downloaded on SpigotMC"), result.getNewestVersion()));
                        return;
                    }

                    UpdateChecker.UpdateReason reason = result.getReason();
                    if (reason == UpdateChecker.UpdateReason.UP_TO_DATE) {
                        e.getPlayer().sendMessage(String.format("§aYour version of StrixReport (%s) is up to date!", result.getNewestVersion()));
                    } else if (reason == UpdateChecker.UpdateReason.UNRELEASED_VERSION) {
                        e.getPlayer().sendMessage(String.format("§eYour version of StrixReport (%s) is more recent than the one publicly available. Are you on a development build?", result.getNewestVersion()));
                    } else {
                        e.getPlayer().sendMessage("§4Could not check for a new version of StrixReport. Reason: " + reason);
                    }
                });
            }
        }
    }

    private void CreateLog() {
        TxtFile logFile = new TxtFile(this, File.separator + "Changelog", "StrixReport_" + instance.getDescription().getVersion());
        logFile.clear();
        logFile.addLine("ChangeLog File StrixReport Version: " + instance.getDescription().getVersion());
        logFile.addLine("Latest update  12/06/2018");
        logFile.addLine("");
        logFile.addLine(" - Fixed broken prefix line on toggle reports command.");
        logFile.addLine(" - Fixed \"Right click to change message\" on management gui.");
        logFile.addLine(" - Added click-teleport command config and is changeable on gui.");
        logFile.addLine(" - Added customizable report gui.");
        logFile.addLine(" - Added player reports amount.");
        logFile.addLine("");
        logFile.write();
    }

    private void configHeader() {
        FileConfiguration c = SReport.instance.getConfig();
        File fichero = new File(SReport.instance.getDataFolder() + "/config.yml");
        if (!fichero.exists()) {
            c.options().header("###########################################\n" +
                    "#    _____     _           _              #\n" +
                    "#   /  ___| __| |__       (_)             #\n" +
                    "#   \\ `--. |__| |__| _ __  _   __  __     #\n" +
                    "#    `--. \\   | |   | '_/ | |  \\ \\/ /     #\n" +
                    "#   /\\__/ /   | |_  | |   | |  |>  <|     #\n" +
                    "#   \\____/    \\___| |_|   |_|  /_/\\_\\     #\n" +
                    "#                                         " + "#\n" +
                    "# Developer: Sliide_                      #\n" +
                    "###########################################\n" +
                    "\n" +
                    "StrixReport " + SReport.instance.getDescription().getVersion() + " configuration file" +
                    "\n" + "\ncheck_updates: <true/false>" +
                    "\n  Should StrixReport check if there are any updates available on Spigot and inform ops there is an update available." +
                    "\n" +
                    "\n" +
                    "\n Cooldown" +
                    "\n   If you want to disable cooldown write \"0\" in cooldown !" +
                    "\n" + "\n Toggle" +
                    "\n   Here you can toggle every setting for the config." +
                    "\n" + "\n Permissions" +
                    "\n  All permissions are customizable but there can't be empty permissions." +
                    "\n" +
                    "\n Click Teleport" + "\n  This is a clickable message for the staff," +
                    "\n  this message can be used with placeholders also \"\\n\"." + "\n  Avaiable placeholders for click teleport:" +
                    "\n    %player% who used the command." + "\n    %target% who was reported." + "\n    %reason% the report message." +
                    "\n" + "\n Report GUI configuration layout:" + "\n  GUI:" + "\n    name: '&cStrix&4Report &7GUI'" + "\n    rows: 1" +
                    "\n    items:" +
                    "\n      example:" +
                    "\n        name: '&eExample' // *Necessary" +
                    "\n        id: 35 // The id can be numbers and text. *Necessary" +
                    "\n        slot: 1 // *Necessary" + "\n        amount: 1" +
                    "\n        data: 14 // If the item need use data this can be added." +
                    "\n        lore:" + "\n        - ''" + "\n        - '&6Example lore'" +
                    "\n        - ''" + "\n" + "\n" + "###########################################\n" +
                    "\n Sup the config is done." + "\n");
            c.options().copyDefaults(true);
            SReport.instance.saveConfig();
            SReport.instance.reloadConfig();
        }
    }
}
