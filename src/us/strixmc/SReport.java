package us.strixmc;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import us.strixmc.Commands.commandReport;
import us.strixmc.Commands.commandToggle;
import us.strixmc.Commands.strixreport;
import us.strixmc.GUI.reportGUI;
import us.strixmc.Utils.cooldowns;
import us.strixmc.Utils.updateChecker;
import us.strixmc.Utils.utils;

import java.io.File;

public class SReport extends JavaPlugin implements Listener {

    public static Plugin instance;

    @Override
    public void onEnable() {
        instance = this;
        if (getConfig().getBoolean("check_updates")) {
            updateChecker.init(this, 42151).requestUpdateCheck().whenComplete((result, exception) -> {
                if (result.requiresUpdate()) {
                    this.getLogger().info(String.format("An update is available! StrixReport %s may be downloaded on SpigotMC", result.getNewestVersion()));
                    return;
                }

                updateChecker.UpdateReason reason = result.getReason();
                if (reason == updateChecker.UpdateReason.UP_TO_DATE) {
                    this.getLogger().info(String.format("Your version of StrixReport (%s) is up to date!", result.getNewestVersion()));
                } else if (reason == updateChecker.UpdateReason.UNRELEASED_VERSION) {
                    this.getLogger().info(String.format("Your version of StrixReport (%s) is more recent than the one publicly available. Are you on a development build?", result.getNewestVersion()));
                } else {
                    this.getLogger().warning("Could not check for a new version of StrixReport. Reason: " + reason);
                }
            });
        }
        configHeader();

        loadEvents();
        loadCommands();

        cooldowns.createCooldown("report");

    }

    private void loadEvents() {
        Bukkit.getPluginManager().registerEvents(new reportGUI(), this);
        Bukkit.getPluginManager().registerEvents(new strixreport(), this);
        Bukkit.getPluginManager().registerEvents(new utils(), this);
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void loadCommands() {
        getCommand("strixreport").setTabCompleter(new strixreport());
        getCommand("togglereport").setExecutor(new commandToggle());
        getCommand("report").setExecutor(new commandReport());
        getCommand("strixreport").setExecutor(new strixreport());
    }

    @EventHandler
    public void updateJoin(PlayerJoinEvent e) {
        if (getConfig().getBoolean("check_updates") || e.getPlayer().isOp() || e.getPlayer().hasPermission(getConfig().getString("permissions.report.admin")) ) {
                updateChecker.init(this, 42151).requestUpdateCheck().whenComplete((result, exception) -> {
                    if (result.requiresUpdate()) {
                        e.getPlayer().sendMessage(String.format(utils.c("§cAn update is available! StrixReport %s may be downloaded on SpigotMC"), result.getNewestVersion()));
                        return;
                    }

                    updateChecker.UpdateReason reason = result.getReason();
                    if (reason == updateChecker.UpdateReason.UP_TO_DATE) {
                        e.getPlayer().sendMessage(String.format("§aYour version of StrixReport (%s) is up to date!", result.getNewestVersion()));
                    } else if (reason == updateChecker.UpdateReason.UNRELEASED_VERSION) {
                        e.getPlayer().sendMessage(String.format("§eYour version of StrixReport (%s) is more recent than the one publicly available. Are you on a development build?", result.getNewestVersion()));
                    } else {
                        e.getPlayer().sendMessage("§4Could not check for a new version of StrixReport. Reason: " + reason);
                    }
                });
        }
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
