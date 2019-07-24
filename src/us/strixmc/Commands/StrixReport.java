package us.strixmc.Commands;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.strixmc.SReport;
import us.strixmc.Utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StrixReport implements CommandExecutor, TabCompleter, Listener {

    private List<Player> newReported = new ArrayList<>();
    private List<Player> newSelfReport = new ArrayList<>();
    private List<Player> newCommand = new ArrayList<>();
    private List<Player> newCooldown = new ArrayList<>();
    private List<Player> newCooldown2 = new ArrayList<>();
    private List<Player> newReminder = new ArrayList<>();
    private List<Player> newPrefix = new ArrayList<>();


    private static ItemStack item(Material material, String name, List<String> lore, int amount, int data) {
        ItemStack item = new ItemStack(material, amount, (short) data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(ctranslate(lore));
        item.setItemMeta(meta);
        return item;
    }

    private static List<String> ctranslate(List<String> lore) {
        List<String> lore2 = new ArrayList();
        Iterator var3 = lore.iterator();

        while (var3.hasNext()) {
            String eilute = (String) var3.next();
            lore2.add(ChatColor.translateAlternateColorCodes('&', eilute));
        }

        return lore2;
    }

    private static void messages(Player p) {
        Inventory inv = Bukkit.createInventory(null, 45, Utils.c("&cMessages GUI"));

        ItemStack back = (item(Material.REDSTONE, "&cBack", Arrays.asList("&7Back to Manage GUI"), 1, 0));

        ItemStack noperms = (item(Material.PAINTING, "&cNo permissions", Arrays.asList("&cMessage&7: " + SReport.instance.getConfig().getString("no-permissions")), 1, 0));
        ItemStack prefix = (item(Material.PAINTING, "&cPrefix", Arrays.asList("&cPrefix &7: " + SReport.instance.getConfig().getString("prefix")), 1, 0));
        ItemStack reported = (item(Material.PAINTING, "&cReported", Arrays.asList("&cMessage&7: " + SReport.instance.getConfig().getString("reported")), 1, 0));
        ItemStack reminder = (item(Material.PAINTING, "&cReminder", Arrays.asList("&cMessage&7: " + SReport.instance.getConfig().getString("reminder")), 1, 0));
        ItemStack notfound = (item(Material.PAINTING, "&cNot found", Arrays.asList("&cMessage&7: " + SReport.instance.getConfig().getString("not-found")), 1, 0));
        ItemStack selfreport = (item(Material.PAINTING, "&cSelf Report", Arrays.asList("&cMessage&7: " + SReport.instance.getConfig().getString("self-report")), 1, 0));
        ItemStack clickteleport = (item(Material.PAINTING, "&cClick Teleport", Arrays.asList("&cMessage&7: " + SReport.instance.getConfig().getString("click-teleport.message"), "&cHover&7: " + SReport.instance.getConfig().getString("click-teleport.hover")), 1, 0));
        ItemStack usage = new ItemStack(Material.PAINTING);
        ItemMeta usageM = usage.getItemMeta();
        ArrayList lore = new ArrayList();
        lore.add(Utils.c("&cMessages&7:"));
        for (String s : SReport.instance.getConfig().getStringList("usage")) {
            lore.add(Utils.c(s));
        }
        usageM.setLore(lore);
        usageM.setDisplayName(Utils.c("&cUsage"));
        usage.setItemMeta(usageM);
        ItemStack report = new ItemStack(Material.PAINTING);
        ItemMeta reportM = report.getItemMeta();
        ArrayList rlore = new ArrayList();
        rlore.add(Utils.c("&cMessages&7:"));
        for (String s : SReport.instance.getConfig().getStringList("report")) {
            rlore.add(Utils.c(s));
        }
        reportM.setLore(rlore);
        reportM.setDisplayName(Utils.c("&cReport"));
        report.setItemMeta(reportM);
        ItemStack border = (item(Material.STAINED_GLASS_PANE, " ", Arrays.asList(""), 1, 15));
        for (int i = 0; i < 9; ++i) {
            inv.setItem(i, border);
        }
        for (int i = 35; i < 45; ++i) {
            inv.setItem(i, border);
        }
        inv.setItem(10, noperms);
        inv.setItem(11, prefix);
        inv.setItem(12, reported);
        inv.setItem(13, reminder);
        inv.setItem(14, notfound);
        inv.setItem(15, selfreport);
        inv.setItem(16, clickteleport);
        inv.setItem(19, usage);
        inv.setItem(20, report);
        inv.setItem(9, border);
        inv.setItem(17, border);
        inv.setItem(18, border);
        inv.setItem(26, border);
        inv.setItem(27, border);
        inv.setItem(35, border);
        inv.setItem(40, back);

        p.openInventory(inv);
    }

    private static void manage(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, Utils.c("&cStrix&4Report &7Management"));


        if (SReport.instance.getConfig().getBoolean("reminder")) {
            ItemStack reminder = (item(Material.REDSTONE_TORCH_ON, "&cReminder", Arrays.asList("&cStatus&7: " + SReport.instance.getConfig().getString("toggle.reminder.enabled"), "&cMessage&7: " + SReport.instance.getConfig().getString("reminder"), " ", "&7Left Click to toggle.", "&7Right Click to change message."), 1, 0));
            inv.setItem(15, reminder);
        } else {
            ItemStack reminder = (item(Material.TORCH, "&cReminder", Arrays.asList("&cStatus&7: " + SReport.instance.getConfig().getString("toggle.reminder.enabled"), "&cMessage&7: " + SReport.instance.getConfig().getString("reminder"), " ", "&7Left Click to toggle.", "&7Right Click to change message."), 1, 0));
            inv.setItem(15, reminder);
        }

        ItemStack prefix = (item(Material.BOOK, "&cPrefix", Arrays.asList("&cStatus&7: " + SReport.instance.getConfig().getString("toggle.prefix.enabled"), "&cMessage&7: " + SReport.instance.getConfig().getString("prefix"), " ", "&7Left Click to toggle.", "&7Right Click to change message."), 1, 0));
        ItemStack messages = (item(Material.BOOK_AND_QUILL, "&cMessages", Arrays.asList("&7Click to open messages GUI."), 1, 0));
        ItemStack selfreport = (item(Material.ANVIL, "&cSelf Report", Arrays.asList("&cStatus&7: " + SReport.instance.getConfig().getString("toggle.self-report.enabled"), "&cMessage&7: " + SReport.instance.getConfig().getString("self-report"), " ", "&7Left Click to toggle.", "&7Right Click to change message."), 1, 0));
        ItemStack cooldown = (item(Material.WATCH, "&cCooldown", Arrays.asList("&cStatus&7: " + SReport.instance.getConfig().getInt("cooldown"), "&cMessage&7: " + SReport.instance.getConfig().getString("on-cooldown"), " ", "&7Left Click to change cooldown.", "&7Right Click to change message."), 1, 0));
        ItemStack clickteleport = (item(Material.EYE_OF_ENDER, "&cClick Teleport", Arrays.asList("&cStatus&7: " + SReport.instance.getConfig().getString("toggle.click-teleport.enabled"), "&cCommand&7: " + SReport.instance.getConfig().getString("click-teleport.command"), "", "&7Left Click to Toggle.", "&7Right Click to change command."), 1, 0));
        ItemStack reported = (item(Material.IRON_FENCE, "&cReported", Arrays.asList("&cStatus&7: " + SReport.instance.getConfig().getString("toggle.reported.enabled"), "&cMessage&7: " + SReport.instance.getConfig().getString("reported"), "", "&7Left Click to Toggle.", "&7Right Click to change message."), 1, 0));
        ItemStack reportgui = (item(Material.CHEST, "&cReport GUI", Arrays.asList("&cStatus&7: " + SReport.instance.getConfig().getString("toggle.report-gui.enabled"), "&cRows&7: " + SReport.instance.getConfig().getInt("GUI.rows"), "", "&7Left Click to Toggle.", "&7Right Click to change rows."), 1, 0));

        ItemStack border = (item(Material.STAINED_GLASS_PANE, " ", Arrays.asList(""), 1, 15));
        for (int i = 0; i < 9; ++i) {
            inv.setItem(i, border);
        }
        for (int i = 45; i < 54; ++i) {
            inv.setItem(i, border);
        }

        /*
        String configVersion = SReport.instance.getConfig().getString("version");
        if (!configVersion.equals(SReport.instance.getDescription().getVersion())) {
            ItemStack info = (item(Material.INK_SACK, "&6Info", Arrays.asList("", "&cStrix&4Report &7has a new version!", "", "&cCurrent Version&7: &6" + SReport.instance.getDescription().getVersion(), ""), 1, 14));
            inv.setItem(4, info);
        }
        /*/

        inv.setItem(9, border);
        inv.setItem(17, border);
        inv.setItem(18, border);
        inv.setItem(26, border);
        inv.setItem(27, border);
        inv.setItem(35, border);
        inv.setItem(36, border);
        inv.setItem(44, border);
        inv.setItem(11, prefix);
        inv.setItem(13, selfreport);
        inv.setItem(21, reportgui);
        inv.setItem(23, reported);
        inv.setItem(29, cooldown);
        inv.setItem(31, clickteleport);
        inv.setItem(33, messages);

        p.openInventory(inv);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("strixreport")) {
            StringBuilder sb = new StringBuilder();
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (SReport.instance.getConfig().getString("permissions.report.admin") == null || SReport.instance.getConfig().getString("permissions.report.admin") == "") {
                    p.sendMessage(Utils.c("&cpermissions.report.admin &7can't be empty or null."));
                } else {
                    if (p.hasPermission(SReport.instance.getConfig().getString("permissions.report.admin"))) {
                        if (args.length == 0) {
                            String[] subcommands = new String[]{"manage", "toggle", "reload", "help"};
                            for (String subcommand : subcommands) {
                                if (sb.length() > 0) {
                                    sb.append("§c, §7");
                                }
                                sb.append("§7").append(subcommand);
                            }
                            p.sendMessage(Utils.c("&cUsage: &7/" + label + " <sub-command>"));
                            p.sendMessage(Utils.c("  &cAvaiable sub-command(s) for '" + label + "' are " + sb.toString().replace("[", "").replace("]", "") + "&c."));
                        } else if (args[0].equalsIgnoreCase("toggle")) {
                            if (args.length == 1) {
                                String[] modules = new String[]{"self-report", "prefix", "reminder", "reported", "click-teleport", "report-gui"};
                                for (String module : modules) {
                                    if (sb.length() > 0) {
                                        sb.append("§c, §7");
                                    }
                                    sb.append("§7").append(module);
                                }
                                p.sendMessage(Utils.c("&cUsage: &7/" + label + " toggle <module>"));
                                p.sendMessage(Utils.c("  &cAvaiable module(s) for " + label + " are " + sb.toString().replace("[", "").replace("]", "") + "&c."));
                            }
                            if (args.length == 2) {
                                if (args[1].equalsIgnoreCase("prefix")) {
                                    if (SReport.instance.getConfig().getBoolean("toggle.prefix.enabled")) {
                                        SReport.instance.getConfig().set("toggle.prefix.enabled", Boolean.FALSE);
                                        p.sendMessage(Utils.c("&7Module &cprefix &7has been &cdisabled."));
                                    } else {
                                        SReport.instance.getConfig().set("toggle.prefix.enabled", Boolean.TRUE);
                                        p.sendMessage(Utils.c("&7Module &cprefix &7was &aenabled."));
                                    }
                                    SReport.instance.saveConfig();
                                    SReport.instance.reloadConfig();
                                } else if (args[1].equalsIgnoreCase("self-report")) {
                                    if (SReport.instance.getConfig().getBoolean("toggle.self-report.enabled")) {
                                        SReport.instance.getConfig().set("toggle.self-report.enabled", false);
                                        p.sendMessage(Utils.c("&7Module &cself report &7has been &cdisabled."));
                                    } else {
                                        SReport.instance.getConfig().set("toggle.self-report.enabled", true);
                                        p.sendMessage(Utils.c("&7Module &cself report &7was &aenabled."));
                                    }
                                    SReport.instance.saveConfig();
                                    SReport.instance.reloadConfig();
                                } else if (args[1].equalsIgnoreCase("reminder")) {
                                    if (SReport.instance.getConfig().getBoolean("toggle.reminder.enabled")) {
                                        SReport.instance.getConfig().set("toggle.reminder.enabled", false);
                                        p.sendMessage(Utils.c("&7Module &creminder &7has been &cdisabled."));
                                    } else {
                                        SReport.instance.getConfig().set("toggle.reminder.enabled", true);
                                        p.sendMessage(Utils.c("&7Module &creminder &7was &aenabled."));
                                    }
                                    SReport.instance.saveConfig();
                                    SReport.instance.reloadConfig();
                                } else if (args[1].equalsIgnoreCase("click-teleport")) {
                                    if (SReport.instance.getConfig().getBoolean("toggle.click-teleport.enabled")) {
                                        SReport.instance.getConfig().set("toggle.click-teleport.enabled", false);
                                        p.sendMessage(Utils.c("&7Module &cclick teleport &7has been &cdisabled."));
                                    } else {
                                        SReport.instance.getConfig().set("toggle.click-teleport.enabled", true);
                                        p.sendMessage(Utils.c("&7Module &cclick teleport &7was &aenabled."));
                                    }
                                    SReport.instance.saveConfig();
                                    SReport.instance.reloadConfig();
                                } else if (args[1].equalsIgnoreCase("reported")) {
                                    if (SReport.instance.getConfig().getBoolean("toggle.reported.enabled")) {
                                        SReport.instance.getConfig().set("toggle.reported.enabled", false);
                                        p.sendMessage(Utils.c("&7Module &creported &7has been &cdisabled."));
                                    } else {
                                        SReport.instance.getConfig().set("toggle.reported.enabled", true);
                                        p.sendMessage(Utils.c("&7Module &creported &7was &aenabled."));
                                    }
                                    SReport.instance.saveConfig();
                                    SReport.instance.reloadConfig();
                                } else if (args[1].equalsIgnoreCase("report-gui")) {
                                    if (SReport.instance.getConfig().getBoolean("toggle.report-gui.enabled")) {
                                        SReport.instance.getConfig().set("toggle.report-gui.enabled", false);
                                        p.sendMessage(Utils.c("&7Module &creport-gui &7has been &cdisabled."));
                                    } else {
                                        SReport.instance.getConfig().set("toggle.report-gui.enabled", true);
                                        p.sendMessage(Utils.c("&7Module &creport-gui &7was &aenabled."));
                                    }
                                    SReport.instance.saveConfig();
                                    SReport.instance.reloadConfig();
                                } else if (args[1].equalsIgnoreCase("updater")) {
                                    if (SReport.instance.getConfig().getBoolean("check_updates")) {
                                        SReport.instance.getConfig().set("check_updates", false);
                                        p.sendMessage(Utils.c("&7Update checker &7has been &cdisabled."));
                                    } else {
                                        SReport.instance.getConfig().set("check_updates", true);
                                        p.sendMessage(Utils.c("&7Update checker &7was &aenabled."));
                                    }
                                    SReport.instance.saveConfig();
                                    SReport.instance.reloadConfig();
                                }
                                else {
                                    p.sendMessage(Utils.c("&cUnknown module &c/" + label + " " + args[0]));
                                    p.sendMessage(Utils.c("  &cAvaiable module(s) for " + label + " are &7self-report, prefix, reminder, click-teleport."));
                                }
                            }
                            if (args.length > 2) {
                                p.sendMessage(Utils.c("&cError in command syntax, To many arguments."));
                            }
                        } else if (args[0].equalsIgnoreCase("manage")) {
                            manage(p);
                            p.sendMessage(Utils.c("&6Openening StrixReport management GUI."));
                        } else if (args[0].equalsIgnoreCase("reload")) {
                            SReport.instance.reloadConfig();
                            SReport.instance.saveConfig();
                            p.sendMessage(Utils.c("&cStrixReport &7Version: " + SReport.instance.getDescription().getVersion() + " &awas successfully reloaded."));
                        } else if (args[0].equalsIgnoreCase("about") || args[0].equalsIgnoreCase("help")) {
                            p.sendMessage("");
                            p.sendMessage(Utils.c("&cStrixReport &7Version: " + SReport.instance.getDescription().getVersion()));
                            p.sendMessage("");
                            p.sendMessage(Utils.c(" &cPermissions&7:"));
                            p.sendMessage(Utils.c("  &cMain&7: " + SReport.instance.getConfig().getString("permissions.report.main")));
                            p.sendMessage(Utils.c("  &cReceive&7: " + SReport.instance.getConfig().getString("permissions.report.receive")));
                            p.sendMessage(Utils.c("  &cAdmin&7: " + SReport.instance.getConfig().getString("permissions.report.admin")));
                            p.sendMessage("");
                        } else if (args[0].equalsIgnoreCase("changelog")) {
                            String path = SReport.instance.getDataFolder() + File.separator + "Changelog" + File.separator + "StrixReport_" + SReport.instance.getDescription().getVersion() + ".txt";
                            File file = new File(path);
                            if (args.length == 1 || args.length > 2) {
                                p.sendMessage(Utils.c("&cUsage: /" + label + " changelog <page>"));
                            }
                            if (args.length == 2) {
                                int page = Integer.parseInt(args[1]);
                                if (args[1].equalsIgnoreCase("0")) {
                                    p.sendMessage(Utils.c("&cPage can't be \"0\""));
                                } else {
                                    try {
                                        List<String> lines = FileUtils.readLines(file);
                                        sender.sendMessage("");
                                        sender.sendMessage(Utils.c("&c   " + SReport.instance.getName() + " &7ChangeLog. &fPage (" + page + ")"));
                                        sender.sendMessage("");
                                        for (int i = (page - 1) * 15; i < page * 15; i++) {
                                            if (i < lines.size()) {
                                                sender.sendMessage(lines.get(i));
                                            }
                                        }
                                        sender.sendMessage("");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {
                            p.sendMessage(Utils.c("&cStrixReport sub-command '" + args[0] + "' is not recognized."));
                            p.sendMessage(Utils.c("  &cAvaiable sub-command(s) for '" + label + "' are " + sb));
                        }
                    } else {
                        Bukkit.getLogger().warning("[" + p.getName() + ": Tried to use the command /" + label + args[0] + "but was denied]");
                    }
                }
            } else {
                if (args.length == 0) {
                    sender.sendMessage(" StrixReport Console commands.");
                    sender.sendMessage("Usage: /" + label + " <sub-command>");
                    sender.sendMessage("  Avaiable sub-command(s) for '" + label + "' are reload.");
                } else if (args[0].equalsIgnoreCase("reload")) {
                    SReport.instance.saveConfig();
                    SReport.instance.reloadConfig();
                    sender.sendMessage("StrixReport Version: " + SReport.instance.getDescription().getVersion() + " was successfully reloaded.");
                } else {
                    sender.sendMessage(" StrixReport Console commands.");
                    sender.sendMessage("StrixReport sub-command '" + args[0] + "' not found for console.");
                    sender.sendMessage("  Avaiable sub-command(s) for '" + label + "' are reload.");
                }
            }
        }
        return false;
    }

    private void cVersion() {
        String configVersion = SReport.instance.getConfig().getString("version");
        String randomid = UUID.randomUUID().toString().substring(0, 8);
        File fichero = new File(SReport.instance.getDataFolder() + "/config.yml");
        if (!SReport.instance.getDescription().getVersion().equals(configVersion) || !fichero.exists()) {
            File ficheroAntiguo = new File(SReport.instance.getDataFolder() + "/config_" + configVersion + "_" + randomid + ".yml");
            fichero.renameTo(ficheroAntiguo);
            fichero.delete();
            SReport.instance.getConfig().options().header("###########################################\n" +
                    "#    _____     _           _              #\n" +
                    "#   /  ___| __| |__       (_)             #\n" +
                    "#   \\ `--. |__| |__| _ __  _   __  __     #\n" +
                    "#    `--. \\   | |   | '_/ | |  \\ \\/ /     #\n" +
                    "#   /\\__/ /   | |_  | |   | |  |>  <|     #\n" +
                    "#   \\____/    \\___| |_|   |_|  /_/\\_\\     #\n" +
                    "#                                         " +
                    "#\n" +
                    "# Developer: Sliide_                      #\n" +
                    "###########################################\n" +
                    "\n" +
                    "StrixReport " + SReport.instance.getDescription().getVersion() + " configuration file" +
                    "\n" +
                    "\ncheck_updates: <true/false>" +
                    "\n  Should StrixReport check if there are any updates available on Spigot and inform ops there is an update available." +
                    "\n" +
                    "\n" +
                    "\n Cooldown" +
                    "\n   If you want to disable cooldown write \"0\" in cooldown !" +
                    "\n" +
                    "\n Toggle" +
                    "\n   Here you can toggle every setting for the config." +
                    "\n" +
                    "\n Permissions" +
                    "\n  All permissions are customizable but there can't be empty permissions."+
                    "\n" +
                    "\n Click Teleport" +
                    "\n  This is a clickable message for the staff," +
                    "\n  this message can be used with placeholders also \"\\n\"." +
                    "\n  Avaiable placeholders for click teleport:" +
                    "\n    %player% who used the command." +
                    "\n    %target% who was reported." +
                    "\n    %reason% the report message." +
                    "\n" +
                    "\n Report GUI configuration layout:" +
                    "\n  GUI:" +
                    "\n    name: '&cStrix&4Report &7GUI'" +
                    "\n    rows: 1" +
                    "\n    items:" +
                    "\n      example:" +
                    "\n        name: '&eExample' // *Necessary" +
                    "\n        id: 35 // The id can be numbers and text. *Necessary" +
                    "\n        slot: 1 // *Necessary" +
                    "\n        amount: 1" +
                    "\n        data: 14 // If the item need use data this can be added." +
                    "\n        lore:" +
                    "\n        - ''" +
                    "\n        - '&6Example lore'" +
                    "\n        - ''" +
                    "\n" +
                    "\n" +
                    "###########################################\n" +
                    "\n Sup the config is done." +
                    "\n");
            SReport.instance.getConfig().options().copyDefaults(true);
            SReport.instance.saveDefaultConfig();
        }
    }

    @EventHandler
    public void onChanges(AsyncPlayerChatEvent e) {
        String msg = e.getMessage();
        if (newCooldown.contains(e.getPlayer())) {
            e.setCancelled(true);
            if (StringUtils.isNumeric(msg)) {
                SReport.instance.getConfig().set("cooldown", Integer.parseInt(msg));
                e.getPlayer().sendMessage(Utils.c("&cCooldown changed to&7: " + msg));
                SReport.instance.saveConfig();
                SReport.instance.reloadConfig();
                newCooldown.remove(e.getPlayer());

                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            } else if (msg.equalsIgnoreCase("cancel")) {
                newCooldown.remove(e.getPlayer());
                e.getPlayer().sendMessage(Utils.c("&cThe new cooldown has been canceled."));
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            } else {
                e.getPlayer().sendMessage(Utils.c("&cError while changing report cooldown. Only numbers are allowed."));
            }
        }
        if (newCooldown2.contains(e.getPlayer())) {
            e.setCancelled(true);
            if (msg.equalsIgnoreCase("cancel")) {
                newCooldown2.remove(e.getPlayer());
                e.getPlayer().sendMessage(Utils.c("&cThe new cooldown message has been canceled."));
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            } else {
                SReport.instance.getConfig().set("on-cooldown", msg);
                e.getPlayer().sendMessage(Utils.c("&cCooldown changed to&7: " + msg));
                SReport.instance.saveConfig();
                SReport.instance.reloadConfig();
                newCooldown2.remove(e.getPlayer());
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            }
        }
        if (newCommand.contains(e.getPlayer())) {
            e.setCancelled(true);
            if (msg.equalsIgnoreCase("cancel")) {
                newCooldown.remove(e.getPlayer());
                e.getPlayer().sendMessage(Utils.c("&cThe new command has been canceled."));
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            } else {
                SReport.instance.getConfig().set("click-teleport.command", "/" + msg);
                e.getPlayer().sendMessage(Utils.c("&cClick teleport command changed to&7: " + SReport.instance.getConfig().getString("click-teleport.command")));
                SReport.instance.saveConfig();
                SReport.instance.reloadConfig();
                newCommand.remove(e.getPlayer());
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            }
        }
        if (newSelfReport.contains(e.getPlayer())) {
            e.setCancelled(true);
            if (msg.equalsIgnoreCase("cancel")) {
                newCooldown.remove(e.getPlayer());
                e.getPlayer().sendMessage(Utils.c("&cThe new Self Report message has been canceled."));
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            } else {
                SReport.instance.getConfig().set("self-report", msg);
                e.getPlayer().sendMessage(Utils.c("&cSelf Report message changed to&7: " + msg));
                SReport.instance.saveConfig();
                SReport.instance.reloadConfig();
                newSelfReport.remove(e.getPlayer());
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            }
        }
        if (newReminder.contains(e.getPlayer())) {
            e.setCancelled(true);
            if (msg.equalsIgnoreCase("cancel")) {
                newCooldown.remove(e.getPlayer());
                e.getPlayer().sendMessage(Utils.c("&cThe new Reminder message has been canceled."));
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            } else {
                SReport.instance.getConfig().set("reminder", msg);
                SReport.instance.saveConfig();
                SReport.instance.reloadConfig();
                e.getPlayer().sendMessage(Utils.c("&cReminder message changed to&7: " + msg).replace("%prefix%", CommandReport.prefix("")));
                newReminder.remove(e.getPlayer());
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            }
        }
        if (newPrefix.contains(e.getPlayer())) {
            e.setCancelled(true);
            if (msg.equalsIgnoreCase("cancel")) {
                newCooldown.remove(e.getPlayer());
                e.getPlayer().sendMessage(Utils.c("&cThe new prefix has been canceled."));
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            } else {
                String prefix = e.getMessage();
                SReport.instance.getConfig().set("prefix", prefix);
                SReport.instance.saveConfig();
                SReport.instance.reloadConfig();
                e.getPlayer().sendMessage(Utils.c("&cPrefix changed to&7: " + prefix));
                newPrefix.remove(e.getPlayer());
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            }
        }
        if (newReported.contains(e.getPlayer())) {
            e.setCancelled(true);
            if (msg.equalsIgnoreCase("cancel")) {
                newReported.remove(e.getPlayer());
                e.getPlayer().sendMessage(Utils.c("&cThe new reported message has been canceled."));
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            } else {
                SReport.instance.getConfig().set("reported", msg);
                e.getPlayer().sendMessage(Utils.c("&cReported message changed to:" + msg));
                newReported.remove(e.getPlayer());
                SReport.instance.saveConfig();
                SReport.instance.reloadConfig();
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            }
        }
        /*/
        if (newUsage.contains(e.getPlayer())){
            e.setCancelled(true);
            if (msg.equalsIgnoreCase("cancel")) {
                newReported.remove(e.getPlayer());
                e.getPlayer().sendMessage(Utils.c("&cThe new reported message has been canceled."));
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            } else {
                SReport.instance.getConfig().set("usage", Arrays.asList(e.getMessage()));
                newUsage.remove(e.getPlayer());
                e.getPlayer().sendMessage(e.getMessage());
                SReport.instance.saveConfig();
                SReport.instance.reloadConfig();
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    messages(e.getPlayer());
                }, 0);
            }
          }
        /*/
    }

    @EventHandler
    private void onManage(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        Player p = (Player) e.getWhoClicked();
        ItemStack i = e.getCurrentItem();

        if (inv != null && i != null && e.getInventory().getName().equalsIgnoreCase(Utils.c("&cStrix&4Report &7Management"))) {
            e.setCancelled(true);
            if (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) {
                String c = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                if (c.equalsIgnoreCase("Prefix")) {
                    if (e.getClick().isLeftClick()) {
                        if (SReport.instance.getConfig().getBoolean("toggle.prefix.enabled")) {
                            SReport.instance.getConfig().set("toggle.prefix.enabled", Boolean.FALSE);
                            p.sendMessage(Utils.c("&7Module &cprefix &7has been &cdisabled."));
                        } else {
                            SReport.instance.getConfig().set("toggle.prefix.enabled", Boolean.TRUE);
                            p.sendMessage(Utils.c("&7Module &cprefix &7was &aenabled."));
                            p.getInventory().setHeldItemSlot(c.length());
                        }
                        SReport.instance.saveConfig();
                        SReport.instance.reloadConfig();
                        p.closeInventory();
                        Bukkit.getScheduler().runTaskLater(SReport.instance, () -> manage(p), 2);
                    }
                    if (e.getClick().isRightClick()) {
                        p.sendMessage("");
                        p.sendMessage(Utils.c("&7&m------------------------------"));
                        p.sendMessage(Utils.c("&6Write in chat the new Prefix."));
                        p.sendMessage(Utils.c("&cWrite cancel to cancel the action."));
                        p.sendMessage(Utils.c("&7&m------------------------------"));
                        p.sendMessage("");
                        newPrefix.add(p);
                        p.closeInventory();
                    }
                } else if (c.equalsIgnoreCase("Reminder")) {
                    if (e.getClick().isLeftClick()) {
                        if (SReport.instance.getConfig().getBoolean("toggle.reminder.enabled")) {
                            SReport.instance.getConfig().set("toggle.reminder.enabled", Boolean.FALSE);
                            p.sendMessage(Utils.c("&7Module &creminder &7was &cdisabled."));
                        } else {
                            SReport.instance.getConfig().set("toggle.reminder.enabled", Boolean.TRUE);
                            p.sendMessage(Utils.c("&7Module &creminder &7was &aenabled."));
                        }
                        SReport.instance.saveConfig();
                        SReport.instance.reloadConfig();
                        Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                            manage(p);
                        }, 2);
                    }
                    if (e.getClick().isRightClick()) {
                        p.sendMessage("");
                        p.sendMessage(Utils.c("&7&m------------------------------"));
                        p.sendMessage(Utils.c("&6Write in chat the new Reminder message."));
                        p.sendMessage(Utils.c("&cWrite cancel to cancel the action."));
                        p.sendMessage(Utils.c("&eAvaiable placeholder&7: %prefix%"));
                        p.sendMessage(Utils.c("&7&m------------------------------"));
                        p.sendMessage("");
                        newReminder.add(p);
                        p.closeInventory();
                    }
                    p.closeInventory();
                } else if (c.equalsIgnoreCase("Self Report")) {
                    if (e.isLeftClick()) {
                        if (SReport.instance.getConfig().getBoolean("toggle.self-report.enabled")) {
                            SReport.instance.getConfig().set("toggle.self-report.enabled", Boolean.FALSE);
                            p.sendMessage(Utils.c("&7Module &cself report &7was &cdisabled."));
                        } else {
                            SReport.instance.getConfig().set("toggle.self-report.enabled", Boolean.TRUE);
                            p.sendMessage(Utils.c("&7Module &cself report &7was &aenabled."));
                        }
                        SReport.instance.saveConfig();
                        SReport.instance.reloadConfig();
                        p.closeInventory();
                        Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                            manage(p);
                        }, 2);
                    }
                    if (e.isRightClick()) {
                        newSelfReport.add(p);
                        p.sendMessage("");
                        p.sendMessage(Utils.c("&7&m------------------------------"));
                        p.sendMessage(Utils.c("&6Write in chat the new Self Report message."));
                        p.sendMessage(Utils.c("&cWrite cancel to cancel the action."));
                        p.sendMessage(Utils.c("&eAvaiable placeholder&7: %prefix%"));
                        p.sendMessage(Utils.c("&7&m------------------------------"));
                        p.sendMessage("");
                        p.closeInventory();
                    }
                } else if (c.equalsIgnoreCase("Cooldown")) {
                    if (e.isLeftClick()) {
                        p.sendMessage("");
                        p.sendMessage(Utils.c("&7&m------------------------------"));
                        p.sendMessage(Utils.c("&6Write in chat the new Cooldown."));
                        p.sendMessage(Utils.c("&cWrite cancel to cancel the action."));
                        p.sendMessage(Utils.c("&7&m------------------------------"));
                        p.sendMessage("");
                        newCooldown.add(p);
                    }
                    if (e.isRightClick()) {
                        p.sendMessage("");
                        p.sendMessage(Utils.c("&7&m------------------------------"));
                        p.sendMessage(Utils.c("&6Write in chat the new Cooldown message."));
                        p.sendMessage(Utils.c("&cWrite cancel to cancel the action."));
                        p.sendMessage(Utils.c("&eAvaiable placeholder&7: %seconds%"));
                        p.sendMessage(Utils.c("&7&m------------------------------"));
                        p.sendMessage("");
                        newCooldown2.add(p);
                    }
                    p.closeInventory();
                } else if (c.equalsIgnoreCase("Click Teleport")) {
                    if (e.isLeftClick()) {
                        if (SReport.instance.getConfig().getBoolean("toggle.click-teleport.enabled")) {
                            SReport.instance.getConfig().set("toggle.click-teleport.enabled", Boolean.FALSE);
                            p.sendMessage(Utils.c("&7Module &cclick teleport &7was &cdisabled."));
                        } else {
                            SReport.instance.getConfig().set("toggle.click-teleport.enabled", Boolean.TRUE);
                            p.sendMessage(Utils.c("&7Module &cclick teleport &7was &aenabled."));
                        }
                        SReport.instance.saveConfig();
                        SReport.instance.reloadConfig();
                        p.closeInventory();
                        Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                            manage(p);
                        }, 2);
                    }
                    if (e.isRightClick()) {
                        newCommand.add(p);
                        p.sendMessage("");
                        p.sendMessage(Utils.c("&7&m------------------------------"));
                        p.sendMessage(Utils.c("&6Write in chat the new Click teleport command."));
                        p.sendMessage(Utils.c("&dInfo &c/ is not necessary."));
                        p.sendMessage(Utils.c("&cWrite cancel to cancel the action."));
                        p.sendMessage(Utils.c("&eAvaiable placeholder&7: %target%"));
                        p.sendMessage(Utils.c("&cExample: teleport %target%"));
                        p.sendMessage(Utils.c("&7&m------------------------------"));
                        p.sendMessage("");
                        p.closeInventory();
                    }
                } else if (c.equalsIgnoreCase("Reported")) {
                    if (e.isLeftClick()) {
                        if (SReport.instance.getConfig().getBoolean("toggle.reported.enabled")) {
                            SReport.instance.getConfig().set("toggle.reported.enabled", Boolean.FALSE);
                            p.sendMessage(Utils.c("&7Module &cReported &7was &cdisabled."));
                        } else {
                            SReport.instance.getConfig().set("toggle.reported.enabled", Boolean.TRUE);
                            p.sendMessage(Utils.c("&7Module &cReported &7was &aenabled."));
                        }
                        SReport.instance.saveConfig();
                        SReport.instance.reloadConfig();
                        p.closeInventory();
                        Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                            manage(p);
                        }, 1);
                    }
                    if (e.isRightClick()) {
                        newReported.add(p);
                        p.sendMessage("");
                        p.sendMessage(Utils.c("&7&m------------------------------"));
                        p.sendMessage(Utils.c("&6Write in chat the new Reported message."));
                        p.sendMessage(Utils.c("&cWrite cancel to cancel the action."));
                        p.sendMessage(Utils.c("&eAvaiable placeholder&7: %prefix%, %target%, %reason%"));
                        p.sendMessage(Utils.c("&7&m------------------------------"));
                        p.sendMessage("");
                        p.closeInventory();
                    }
                } else if (c.equalsIgnoreCase("Report GUI")) {
                    if (e.isLeftClick()) {

                        if (SReport.instance.getConfig().getBoolean("toggle.report-gui.enabled")) {
                            SReport.instance.getConfig().set("toggle.report-gui.enabled", Boolean.FALSE);
                            p.sendMessage(Utils.c("&7Module &cReport GUI &7was &cdisabled."));
                        } else {
                            SReport.instance.getConfig().set("toggle.report-gui.enabled", Boolean.TRUE);
                            p.sendMessage(Utils.c("&7Module &cReport GUI &7was &aenabled."));
                        }
                        SReport.instance.saveConfig();
                        SReport.instance.reloadConfig();
                        Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                            manage(p);
                        }, 0);
                    }
                    if (e.isRightClick()) {
                        ItemMeta meta = i.getItemMeta();
                        meta.setLore(Arrays.asList("§cStatus§7: " + SReport.instance.getConfig().getString("toggle.report-gui.enabled"), "§cRows§7: " + SReport.instance.getConfig().getInt("GUI.rows"), "", "§7Left Click to Toggle.", "§7Right Click to change rows. §e§l← §bThis option will be added soon."));
                        i.setItemMeta(meta);
                        Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                            manage(p);
                        }, 20L * 2);
                    }

                } else if (c.equalsIgnoreCase("Messages")) {
                    messages(p);
                }
            }
        }
        if (inv != null && i != null && e.getInventory().getName().equalsIgnoreCase(Utils.c("&cMessages GUI"))) {
            e.setCancelled(true);
            if (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) {
                String c = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                if (c.equalsIgnoreCase("Back")) {
                    manage(p);
                } /*/else if (c.equalsIgnoreCase("Usage")) {
                    newUsage.add(p);
                    p.sendMessage("");
                    p.sendMessage(Utils.c("&7&m------------------------------"));
                    p.sendMessage(Utils.c("&6Write in chat the new Reported message."));
                    p.sendMessage(Utils.c("&cWrite cancel to cancel the action."));
                    p.sendMessage(Utils.c("&eAvaiable placeholder&7: %prefix%, %target%, %reason%"));
                    p.sendMessage(Utils.c("&7&m------------------------------"));
                    p.sendMessage("");
                    p.closeInventory();
                }
                /*/
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String Label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("sr") || cmd.getName().equalsIgnoreCase("strixreport")) {
            List l = new ArrayList();
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("")) {
                    l.add("manage");
                    l.add("toggle");
                    l.add("reload");
                    l.add("about");
                    l.add("help");
                    l.add("changelog");
                    return l;
                } else if (args[0].startsWith("m")) {
                    l.add("manage");
                    return l;
                } else if (args[0].startsWith("t")) {
                    l.add("toggle");
                    return l;
                } else if (args[0].startsWith("r")) {
                    l.add("reload");
                    return l;
                } else if (args[0].startsWith("a")) {
                    l.add("about");
                    return l;
                } else if (args[0].startsWith("h")) {
                    l.add("help");
                    return l;
                }else if (args[0].startsWith("c")) {
                    l.add("changelog");
                    return l;
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("toggle")) {
                    if (args[1].equalsIgnoreCase("s")) {
                        l.add("self-report");
                        return l;
                    } else if (args[1].startsWith("p")) {
                        l.add("prefix");
                        return l;
                    }else if (args[1].startsWith("r")) {
                        l.add("reminder");
                        l.add("reported");
                        l.add("report-gui");
                        return l;
                    }else if (args[1].startsWith("c")) {
                        l.add("click-teleport");
                        return l;
                    }
                }
            }
        }
        return null;
    }
}
