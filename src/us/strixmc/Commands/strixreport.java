package us.strixmc.Commands;

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
import us.strixmc.Utils.utils;

import java.io.File;
import java.util.*;

public class strixreport implements CommandExecutor, TabCompleter, Listener {

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
        Inventory inv = Bukkit.createInventory(null, 45, utils.c("&cMessages GUI"));

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
        lore.add(utils.c("&cMessages&7:"));
        for (String s : SReport.instance.getConfig().getStringList("usage")) {
            lore.add(utils.c(s));
        }
        usageM.setLore(lore);
        usageM.setDisplayName(utils.c("&cUsage"));
        usage.setItemMeta(usageM);
        ItemStack report = new ItemStack(Material.PAINTING);
        ItemMeta reportM = report.getItemMeta();
        ArrayList rlore = new ArrayList();
        rlore.add(utils.c("&cMessages&7:"));
        for (String s : SReport.instance.getConfig().getStringList("report")) {
            rlore.add(utils.c(s));
        }
        reportM.setLore(rlore);
        reportM.setDisplayName(utils.c("&cReport"));
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
        Inventory inv = Bukkit.createInventory(null, 9, utils.c("&cStrix&4Report &7Management"));

        inv.setItem(0, item(Material.BOOK, "&cPrefix", Arrays.asList("&cStatus&7: " + SReport.instance.getConfig().getString("toggle.prefix.enabled"), "&cMessage&7: " + SReport.instance.getConfig().getString("prefix"), " ", "&7Left Click to toggle.", "&7Right Click to change message."), 1, 0));
        inv.setItem(1, item(Material.ANVIL, "&cSelf Report", Arrays.asList("&cStatus&7: " + SReport.instance.getConfig().getString("toggle.self-report.enabled"), "&cMessage&7: " + SReport.instance.getConfig().getString("self-report"), " ", "&7Left Click to toggle.", "&7Right Click to change message."), 1, 0));
        inv.setItem(2, item(Material.REDSTONE_TORCH_ON, "&cReminder", Arrays.asList("&cStatus&7: " + SReport.instance.getConfig().getString("toggle.reminder.enabled"), "&cMessage&7: " + SReport.instance.getConfig().getString("reminder"), " ", "&7Left Click to toggle.", "&7Right Click to change message."), 1, 0));
        inv.setItem(3, item(Material.CHEST, "&cReport GUI", Arrays.asList("&cStatus&7: " + SReport.instance.getConfig().getString("toggle.report-gui.enabled"), "&cRows&7: " + SReport.instance.getConfig().getInt("GUI.rows"), "", "&7Left Click to Toggle.", "&7Right Click to change rows."), 1, 0));
        inv.setItem(4, item(Material.IRON_FENCE, "&cReported", Arrays.asList("&cStatus&7: " + SReport.instance.getConfig().getString("toggle.reported.enabled"), "&cMessage&7: " + SReport.instance.getConfig().getString("reported"), "", "&7Left Click to Toggle.", "&7Right Click to change message."), 1, 0));
        inv.setItem(5, item(Material.WATCH, "&cCooldown", Arrays.asList("&cStatus&7: " + SReport.instance.getConfig().getInt("cooldown"), "&cMessage&7: " + SReport.instance.getConfig().getString("on-cooldown"), " ", "&7Left Click to change cooldown.", "&7Right Click to change message."), 1, 0));
        inv.setItem(6, item(Material.EYE_OF_ENDER, "&cClick Teleport", Arrays.asList("&cStatus&7: " + SReport.instance.getConfig().getString("toggle.click-teleport.enabled"), "&cCommand&7: " + SReport.instance.getConfig().getString("click-teleport.command"), "", "&7Left Click to Toggle.", "&7Right Click to change command."), 1, 0));
        inv.setItem(7, item(Material.BOOK_AND_QUILL, "&cMessages", Arrays.asList("&7Click to open messages GUI."), 1, 0));

        p.openInventory(inv);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        StringBuilder sb = new StringBuilder();
        if (sender instanceof Player) {
            String[] subcommands = new String[]{"manage", "reload", "help"};
            for (String subcommand : subcommands) {
                if (sb.length() > 0) {
                    sb.append("§c, §7");
                }
                sb.append("§7").append(subcommand);
            }
            Player p = (Player) sender;
            if (SReport.instance.getConfig().getString("permissions.report.admin") == null || SReport.instance.getConfig().getString("permissions.report.admin").equals("")) {
                p.sendMessage(utils.c("&cpermissions.report.admin &7can't be empty or null."));
            } else {
                if (!p.hasPermission(SReport.instance.getConfig().getString("permissions.report.admin"))) {
                    return true;
                }
                if (args.length == 0) {
                    p.sendMessage(utils.c("&cUsage: &7/" + label + " <sub-command>"));
                    p.sendMessage(utils.c("  &cAvaiable sub-command(s) for '" + label + "' are " + sb.toString().replace("[", "").replace("]", "") + "&c."));
                } else {
                    switch (args[0].toLowerCase()) {
                        case "manage": {
                            manage(p);
                            p.sendMessage(utils.c("&6Openening StrixReport management GUI."));
                            break;
                        }
                        case "about":
                        case "help": {
                            p.sendMessage("");
                            p.sendMessage(utils.c("&cStrixReport &7Version: " + SReport.instance.getDescription().getVersion()));
                            p.sendMessage("");
                            p.sendMessage(utils.c(" &cPermissions&7:"));
                            p.sendMessage(utils.c("  &cMain&7: " + SReport.instance.getConfig().getString("permissions.report.main")));
                            p.sendMessage(utils.c("  &cReceive&7: " + SReport.instance.getConfig().getString("permissions.report.receive")));
                            p.sendMessage(utils.c("  &cAdmin&7: " + SReport.instance.getConfig().getString("permissions.report.admin")));
                            p.sendMessage("");
                            break;
                        }
                        case "reload": {
                            SReport.instance.reloadConfig();
                            SReport.instance.saveConfig();
                            p.sendMessage(utils.c("&cStrixReport &7Version: " + SReport.instance.getDescription().getVersion() + " &awas successfully reloaded."));
                            break;
                        }
                        default: {
                            p.sendMessage(utils.c("&cUsage: &7/" + label + " <sub-command>"));
                            p.sendMessage(utils.c("  &cAvaiable sub-command(s) for '" + label + "' are " + sb.toString().replace("[", "").replace("]", "") + "&c."));

                            break;
                        }
                    }
                }
            }
        } else {
            String[] subcommands = new String[]{"reload"};
            for (String subcommand : subcommands) {
                if (sb.length() > 0) {
                    sb.append("§c, §7");
                }
                sb.append("§7").append(subcommand);
            }
            if (args.length == 0) {
                sender.sendMessage(" StrixReport Console commands.");
                sender.sendMessage("Usage: /" + label + " <sub-command>");
                sender.sendMessage("  Avaiable sub-command(s) for '" + label + "' are " + sb);
            } else {
                switch (args[0].toLowerCase()) {
                    case "reload": {
                        SReport.instance.saveConfig();
                        SReport.instance.reloadConfig();
                        sender.sendMessage("StrixReport Version: " + SReport.instance.getDescription().getVersion() + " was successfully reloaded.");
                        break;
                    }
                    default: {
                        sender.sendMessage(" StrixReport Console commands.");
                        sender.sendMessage("Usage: /" + label + " <sub-command>");
                        sender.sendMessage("  Avaiable sub-command(s) for '" + label + "' are " + sb);
                        break;
                    }
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
            SReport.instance.getConfig().options().header("###########################################\n" + "#    _____     _           _              #\n" + "#   /  ___| __| |__       (_)             #\n" + "#   \\ `--. |__| |__| _ __  _   __  __     #\n" + "#    `--. \\   | |   | '_/ | |  \\ \\/ /     #\n" + "#   /\\__/ /   | |_  | |   | |  |>  <|     #\n" + "#   \\____/    \\___| |_|   |_|  /_/\\_\\     #\n" + "#                                         " + "#\n" + "# Developer: Sliide_                      #\n" + "###########################################\n" + "\n" + "StrixReport " + SReport.instance.getDescription().getVersion() + " configuration file" + "\n" + "\ncheck_updates: <true/false>" + "\n  Should StrixReport check if there are any updates available on Spigot and inform ops there is an update available." + "\n" + "\n" + "\n Cooldown" + "\n   If you want to disable cooldown write \"0\" in cooldown !" + "\n" + "\n Toggle" + "\n   Here you can toggle every setting for the config." + "\n" + "\n Permissions" + "\n  All permissions are customizable but there can't be empty permissions." + "\n" + "\n Click Teleport" + "\n  This is a clickable message for the staff," + "\n  this message can be used with placeholders also \"\\n\"." + "\n  Avaiable placeholders for click teleport:" + "\n    %player% who used the command." + "\n    %target% who was reported." + "\n    %reason% the report message." + "\n" + "\n Report GUI configuration layout:" + "\n  GUI:" + "\n    name: '&cStrix&4Report &7GUI'" + "\n    rows: 1" + "\n    items:" + "\n      example:" + "\n        name: '&eExample' // *Necessary" + "\n        id: 35 // The id can be numbers and text. *Necessary" + "\n        slot: 1 // *Necessary" + "\n        amount: 1" + "\n        data: 14 // If the item need use data this can be added." + "\n        lore:" + "\n        - ''" + "\n        - '&6Example lore'" + "\n        - ''" + "\n" + "\n" + "###########################################\n" + "\n Sup the config is done." + "\n");
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
                e.getPlayer().sendMessage(utils.c("&cCooldown changed to&7: " + msg));
                SReport.instance.saveConfig();
                SReport.instance.reloadConfig();
                newCooldown.remove(e.getPlayer());

                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            } else if (msg.equalsIgnoreCase("cancel")) {
                newCooldown.remove(e.getPlayer());
                e.getPlayer().sendMessage(utils.c("&cThe new cooldown has been canceled."));
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            } else {
                e.getPlayer().sendMessage(utils.c("&cError while changing report cooldown. Only numbers are allowed."));
            }
        }
        if (newCooldown2.contains(e.getPlayer())) {
            e.setCancelled(true);
            if (msg.equalsIgnoreCase("cancel")) {
                newCooldown2.remove(e.getPlayer());
                e.getPlayer().sendMessage(utils.c("&cThe new cooldown message has been canceled."));
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            } else {
                SReport.instance.getConfig().set("on-cooldown", msg);
                e.getPlayer().sendMessage(utils.c("&cCooldown changed to&7: " + msg));
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
                e.getPlayer().sendMessage(utils.c("&cThe new command has been canceled."));
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            } else {
                SReport.instance.getConfig().set("click-teleport.command", "/" + msg);
                e.getPlayer().sendMessage(utils.c("&cClick teleport command changed to&7: " + SReport.instance.getConfig().getString("click-teleport.command")));
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
                e.getPlayer().sendMessage(utils.c("&cThe new Self Report message has been canceled."));
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            } else {
                SReport.instance.getConfig().set("self-report", msg);
                e.getPlayer().sendMessage(utils.c("&cSelf Report message changed to&7: " + msg));
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
                e.getPlayer().sendMessage(utils.c("&cThe new Reminder message has been canceled."));
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            } else {
                SReport.instance.getConfig().set("reminder", msg);
                SReport.instance.saveConfig();
                SReport.instance.reloadConfig();
                e.getPlayer().sendMessage(utils.c("&cReminder message changed to&7: " + msg).replace("%prefix%", commandReport.prefix("")));
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
                e.getPlayer().sendMessage(utils.c("&cThe new prefix has been canceled."));
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            } else {
                String prefix = e.getMessage();
                SReport.instance.getConfig().set("prefix", prefix);
                SReport.instance.saveConfig();
                SReport.instance.reloadConfig();
                e.getPlayer().sendMessage(utils.c("&cPrefix changed to&7: " + prefix));
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
                e.getPlayer().sendMessage(utils.c("&cThe new reported message has been canceled."));
                Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                    manage(e.getPlayer());
                }, 1);
            } else {
                SReport.instance.getConfig().set("reported", msg);
                e.getPlayer().sendMessage(utils.c("&cReported message changed to:" + msg));
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
    public void onManage(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        Player p = (Player) e.getWhoClicked();
        ItemStack i = e.getCurrentItem();

        if (inv != null && i != null && e.getInventory().getName().equalsIgnoreCase(utils.c("&cStrix&4Report &7Management"))) {
            e.setCancelled(true);
            if (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) {
                String c = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                if (c.equalsIgnoreCase("Prefix")) {
                    if (e.getClick().isLeftClick()) {
                        if (SReport.instance.getConfig().getBoolean("toggle.prefix.enabled")) {
                            SReport.instance.getConfig().set("toggle.prefix.enabled", Boolean.FALSE);
                            p.sendMessage(utils.c("&7Module &cprefix &7has been &cdisabled."));
                        } else {
                            SReport.instance.getConfig().set("toggle.prefix.enabled", Boolean.TRUE);
                            p.sendMessage(utils.c("&7Module &cprefix &7was &aenabled."));
                            p.getInventory().setHeldItemSlot(c.length());
                        }
                        SReport.instance.saveConfig();
                        SReport.instance.reloadConfig();
                        Bukkit.getScheduler().runTaskLater(SReport.instance, () -> manage(p), 5);
                    }
                    if (e.getClick().isRightClick()) {
                        p.sendMessage("");
                        p.sendMessage(utils.c("&7&m------------------------------"));
                        p.sendMessage(utils.c("&6Write in chat the new Prefix."));
                        p.sendMessage(utils.c("&cWrite cancel to cancel the action."));
                        p.sendMessage(utils.c("&7&m------------------------------"));
                        p.sendMessage("");
                        newPrefix.add(p);
                        p.closeInventory();
                    }
                } else if (c.equalsIgnoreCase("Reminder")) {
                    if (e.getClick().isLeftClick()) {
                        if (SReport.instance.getConfig().getBoolean("toggle.reminder.enabled")) {
                            SReport.instance.getConfig().set("toggle.reminder.enabled", Boolean.FALSE);
                            p.sendMessage(utils.c("&7Module &creminder &7was &cdisabled."));
                        } else {
                            SReport.instance.getConfig().set("toggle.reminder.enabled", Boolean.TRUE);
                            p.sendMessage(utils.c("&7Module &creminder &7was &aenabled."));
                        }
                        SReport.instance.saveConfig();
                        SReport.instance.reloadConfig();
                        Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                            manage(p);
                        }, 2);
                    }
                    if (e.getClick().isRightClick()) {
                        p.sendMessage("");
                        p.sendMessage(utils.c("&7&m------------------------------"));
                        p.sendMessage(utils.c("&6Write in chat the new Reminder message."));
                        p.sendMessage(utils.c("&cWrite cancel to cancel the action."));
                        p.sendMessage(utils.c("&eAvaiable placeholder&7: %prefix%"));
                        p.sendMessage(utils.c("&7&m------------------------------"));
                        p.sendMessage("");
                        newReminder.add(p);
                        p.closeInventory();
                    }
                } else if (c.equalsIgnoreCase("Self Report")) {
                    if (e.isLeftClick()) {
                        if (SReport.instance.getConfig().getBoolean("toggle.self-report.enabled")) {
                            SReport.instance.getConfig().set("toggle.self-report.enabled", Boolean.FALSE);
                            p.sendMessage(utils.c("&7Module &cself report &7was &cdisabled."));
                        } else {
                            SReport.instance.getConfig().set("toggle.self-report.enabled", Boolean.TRUE);
                            p.sendMessage(utils.c("&7Module &cself report &7was &aenabled."));
                        }
                        SReport.instance.saveConfig();
                        SReport.instance.reloadConfig();
                        Bukkit.getScheduler().runTaskLater(SReport.instance, () -> manage(p), 5);
                    }
                    if (e.isRightClick()) {
                        newSelfReport.add(p);
                        p.sendMessage("");
                        p.sendMessage(utils.c("&7&m------------------------------"));
                        p.sendMessage(utils.c("&6Write in chat the new Self Report message."));
                        p.sendMessage(utils.c("&cWrite cancel to cancel the action."));
                        p.sendMessage(utils.c("&eAvaiable placeholder&7: %prefix%"));
                        p.sendMessage(utils.c("&7&m------------------------------"));
                        p.sendMessage("");
                        p.closeInventory();
                    }
                } else if (c.equalsIgnoreCase("Cooldown")) {
                    if (e.isLeftClick()) {
                        p.sendMessage("");
                        p.sendMessage(utils.c("&7&m------------------------------"));
                        p.sendMessage(utils.c("&6Write in chat the new Cooldown."));
                        p.sendMessage(utils.c("&cWrite cancel to cancel the action."));
                        p.sendMessage(utils.c("&7&m------------------------------"));
                        p.sendMessage("");
                        newCooldown.add(p);
                    }
                    if (e.isRightClick()) {
                        p.sendMessage("");
                        p.sendMessage(utils.c("&7&m------------------------------"));
                        p.sendMessage(utils.c("&6Write in chat the new Cooldown message."));
                        p.sendMessage(utils.c("&cWrite cancel to cancel the action."));
                        p.sendMessage(utils.c("&eAvaiable placeholder&7: %seconds%"));
                        p.sendMessage(utils.c("&7&m------------------------------"));
                        p.sendMessage("");
                        newCooldown2.add(p);
                    }
                    p.closeInventory();
                } else if (c.equalsIgnoreCase("Click Teleport")) {
                    if (e.isLeftClick()) {
                        if (SReport.instance.getConfig().getBoolean("toggle.click-teleport.enabled")) {
                            SReport.instance.getConfig().set("toggle.click-teleport.enabled", Boolean.FALSE);
                            p.sendMessage(utils.c("&7Module &cclick teleport &7was &cdisabled."));
                        } else {
                            SReport.instance.getConfig().set("toggle.click-teleport.enabled", Boolean.TRUE);
                            p.sendMessage(utils.c("&7Module &cclick teleport &7was &aenabled."));
                        }
                        SReport.instance.saveConfig();
                        SReport.instance.reloadConfig();
                        Bukkit.getScheduler().runTaskLater(SReport.instance, () -> {
                            manage(p);
                        }, 2);
                    }
                    if (e.isRightClick()) {
                        newCommand.add(p);
                        p.sendMessage("");
                        p.sendMessage(utils.c("&7&m------------------------------"));
                        p.sendMessage(utils.c("&6Write in chat the new Click teleport command."));
                        p.sendMessage(utils.c("&dInfo &c/ is not necessary."));
                        p.sendMessage(utils.c("&cWrite cancel to cancel the action."));
                        p.sendMessage(utils.c("&eAvaiable placeholder&7: %target%"));
                        p.sendMessage(utils.c("&cExample: teleport %target%"));
                        p.sendMessage(utils.c("&7&m------------------------------"));
                        p.sendMessage("");
                        p.closeInventory();
                    }
                } else if (c.equalsIgnoreCase("Reported")) {
                    if (e.isLeftClick()) {
                        if (SReport.instance.getConfig().getBoolean("toggle.reported.enabled")) {
                            SReport.instance.getConfig().set("toggle.reported.enabled", Boolean.FALSE);
                            p.sendMessage(utils.c("&7Module &cReported &7was &cdisabled."));
                        } else {
                            SReport.instance.getConfig().set("toggle.reported.enabled", Boolean.TRUE);
                            p.sendMessage(utils.c("&7Module &cReported &7was &aenabled."));
                        }
                        SReport.instance.saveConfig();
                        SReport.instance.reloadConfig();
                        Bukkit.getScheduler().runTaskLater(SReport.instance, () -> manage(p), 5);
                    }
                    if (e.isRightClick()) {
                        newReported.add(p);
                        p.sendMessage("");
                        p.sendMessage(utils.c("&7&m------------------------------"));
                        p.sendMessage(utils.c("&6Write in chat the new Reported message."));
                        p.sendMessage(utils.c("&cWrite cancel to cancel the action."));
                        p.sendMessage(utils.c("&eAvaiable placeholder&7: %prefix%, %target%, %reason%"));
                        p.sendMessage(utils.c("&7&m------------------------------"));
                        p.sendMessage("");
                        p.closeInventory();
                    }
                } else if (c.equalsIgnoreCase("Report GUI")) {
                    if (e.isLeftClick()) {

                        if (SReport.instance.getConfig().getBoolean("toggle.report-gui.enabled")) {
                            SReport.instance.getConfig().set("toggle.report-gui.enabled", Boolean.FALSE);
                            p.sendMessage(utils.c("&7Module &cReport GUI &7was &cdisabled."));
                        } else {
                            SReport.instance.getConfig().set("toggle.report-gui.enabled", Boolean.TRUE);
                            p.sendMessage(utils.c("&7Module &cReport GUI &7was &aenabled."));
                        }
                        SReport.instance.saveConfig();
                        SReport.instance.reloadConfig();
                        Bukkit.getScheduler().runTaskLater(SReport.instance, () -> manage(p), 5);
                    }
                    if (e.isRightClick()) {
                        ItemMeta meta = i.getItemMeta();
                        meta.setLore(Arrays.asList("§cStatus§7: " + SReport.instance.getConfig().getString("toggle.report-gui.enabled"), "§cRows§7: " + SReport.instance.getConfig().getInt("GUI.rows"), "", "§7Left Click to Toggle.", "§7Right Click to change rows. §e§l← §bThis option will be added soon."));
                        i.setItemMeta(meta);
                        Bukkit.getScheduler().runTaskLater(SReport.instance, () -> manage(p), 20L * 2);
                    }

                } else if (c.equalsIgnoreCase("Messages")) {
                    messages(p);
                }
            }
        }
        if (inv != null && i != null && e.getInventory().getName().equalsIgnoreCase(utils.c("&cMessages GUI"))) {
            e.setCancelled(true);
            if (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) {
                String c = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                if (c.equalsIgnoreCase("Back")) {
                    manage(p);
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (!sender.hasPermission(SReport.instance.getConfig().getString("permissions.report.admin"))) {
            return null;
        }
        if (cmd.getName().equalsIgnoreCase("strixreport")) {
            List<String> l = new ArrayList<>();
            if (args.length == 1) {
                String search = args[0].toLowerCase();
                String[] subcommands = new String[]{"manage", "reload", "help"};
                if (args[0].equalsIgnoreCase("")) {
                    Collections.addAll(l, subcommands);
                    return l;
                } else {
                    for (String subcommand : subcommands) {
                        if (subcommand.startsWith(search)) {
                            l.add(subcommand);
                            return l;
                        }
                    }
                }
            }
        }
        return null;
    }
}
