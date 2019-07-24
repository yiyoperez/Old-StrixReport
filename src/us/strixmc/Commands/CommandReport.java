package us.strixmc.Commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.strixmc.GUI.ReportGUI;
import us.strixmc.SReport;
import us.strixmc.Utils.Cooldowns;
import us.strixmc.Utils.Utils;

public class CommandReport implements CommandExecutor {

    public static String prefix(String s) {
        if (SReport.instance.getConfig().getBoolean("toggle.prefix.enabled")) {
            return Utils.c(SReport.instance.getConfig().getString("prefix" + s));
        }
        return Utils.c(s);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("report")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (SReport.instance.getConfig().getBoolean("toggle.report-gui.enabled")) {
                    if (p.hasPermission(SReport.instance.getConfig().getString("permissions.report.main"))) {
                        if (Cooldowns.isOnCooldown("report", p)) {
                            p.sendMessage(Utils.c(SReport.instance.getConfig().getString("on-cooldown").replace("%seconds%", String.valueOf(Cooldowns.getCooldownForPlayerInt("report", p)))));
                        } else if (args.length == 0) {
                            for (String s : SReport.instance.getConfig().getStringList("report-gui.usage")) {
                                p.sendMessage(Utils.c(s).replace("%command%", label).replace("%prefix%", prefix("")));
                            }
                        } else {
                            Player target = Bukkit.getPlayer(args[0]);
                            if (target == null) {
                                p.sendMessage(Utils.c(SReport.instance.getConfig().getString("not-found").replace("%player%", args[0]).replace("%prefix%", prefix(""))));
                            } else if (target == sender) {
                                if (SReport.instance.getConfig().getBoolean("toggle.self-report.enabled")) {
                                    ReportGUI.guiReport(p, target);
                                    Utils.test.put(p.getName(), target.getName());
                                } else {
                                    p.sendMessage(Utils.c(SReport.instance.getConfig().getString("self-report").replace("%prefix%", prefix(""))));
                                }
                            } else {
                                ReportGUI.guiReport(p, target);
                                Utils.test.put(p.getName(), target.getName());
                            }
                        }
                    } else {
                        p.sendMessage(Utils.c(SReport.instance.getConfig().getString("no-permissions").replace("%prefix%", prefix(""))));
                        Bukkit.getLogger().warning("[" + p.getName() + ": Tried to use the command /" + label + " but was denied]");
                    }
                } else {
                    if (p.hasPermission(SReport.instance.getConfig().getString("permissions.report.main"))) {
                        if (Cooldowns.isOnCooldown("report", p)) {
                            p.sendMessage(Utils.c(SReport.instance.getConfig().getString("on-cooldown").replace("%seconds%", String.valueOf(Cooldowns.getCooldownForPlayerInt("report", p)))));
                        } else if (args.length == 0 || args.length == 1) {
                            for (String s : SReport.instance.getConfig().getStringList("usage")) {
                                p.sendMessage(Utils.c(s).replace("%command%", label).replace("%prefix%", prefix("")));
                            }
                        } else {
                            Player target = Bukkit.getPlayer(args[0]);
                            String msg = "";
                            for (int i = 1; i < args.length; i++) {
                                msg = msg + args[i] + " ";
                            }
                            Player[] arrayOfPlayer;
                            int j = (arrayOfPlayer = Bukkit.getOnlinePlayers().toArray(new Player[0])).length;
                            Player staff;
                            if (!Utils.Ramount.containsKey(target)) {
                                Utils.Ramount.put(target, 0);
                            }
                            if (Utils.Ramount.containsKey(target)) {
                                int amount = Utils.Ramount.get(target);
                                if (amount != 1 || Utils.Ramount.get(target) != null) {
                                    Utils.Ramount.put(target, amount + 1);
                                }
                            }
                            final StringBuilder str = new StringBuilder();
                            for (int i = 1; i < args.length; ++i) {
                                str.append(String.valueOf(args[i]) + " ");
                            }
                            if (target == null) {
                                p.sendMessage(Utils.c(SReport.instance.getConfig().getString("not-found").replace("%player%", args[0]).replace("%prefix%", prefix(""))));
                            } else if (target == sender) {
                                if (SReport.instance.getConfig().getBoolean("toggle.self-report.enabled")) {
                                    for (int i = 0; i < j; i++) {
                                        staff = arrayOfPlayer[i];
                                        if (staff.hasPermission(SReport.instance.getConfig().getString("permissions.report.receive"))) {
                                            if (!Utils.toggledReports.contains(staff)) {
                                                for (String s : SReport.instance.getConfig().getStringList("report")) {
                                                    staff.sendMessage(Utils.c(s).replace("%target%", target.getName()).replace("%reason%", msg).replace("%player%", p.getName()).replace("%amount%", Utils.Ramount.get(target).toString()));
                                                }
                                                if (SReport.instance.getConfig().getBoolean("toggle.click-teleport.enabled")) {
                                                    final TextComponent textComponent = new TextComponent();
                                                    textComponent.setText(Utils.c(SReport.instance.getConfig().getString("click-teleport.message")));
                                                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.c(SReport.instance.getConfig().getString("click-teleport.hover").replace("%target%", target.getName()))).create()));
                                                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, SReport.instance.getConfig().getString("click-teleport.command").replace("%target%", target.getName())));
                                                    staff.spigot().sendMessage(textComponent);
                                                }
                                            }
                                        }
                                    }
                                    Cooldowns.addCooldown("report", p, SReport.instance.getConfig().getInt("cooldown"));
                                    if (SReport.instance.getConfig().getBoolean("toggle.reported.enabled")) {
                                        p.sendMessage(Utils.c(SReport.instance.getConfig().getString("reported").replace("%target%", target.getName()).replace("%reason%", msg).replace("%prefix%", prefix(""))));
                                    }
                                    if (SReport.instance.getConfig().getBoolean("toggle.reminder.enabled")) {
                                        p.sendMessage(Utils.c(SReport.instance.getConfig().getString("reminder").replace("%prefix%", prefix(""))));
                                    }
                                } else {
                                    p.sendMessage(Utils.c(SReport.instance.getConfig().getString("self-report").replace("%prefix%", prefix(""))));
                                    return true;
                                }
                            } else {
                                for (int i = 0; i < j; i++) {
                                    staff = arrayOfPlayer[i];
                                    if (staff.hasPermission(SReport.instance.getConfig().getString("permissions.report.receive"))) {
                                        if (!Utils.toggledReports.contains(staff)) {
                                            for (String s : SReport.instance.getConfig().getStringList("report")) {
                                                staff.sendMessage(Utils.c(s).replace("%target%", target.getName()).replace("%reason%", msg).replace("%player%", p.getName()));
                                            }
                                            if (SReport.instance.getConfig().getBoolean("toggle.click-teleport.enabled")) {
                                                final TextComponent textComponent = new TextComponent();
                                                textComponent.setText(Utils.c(SReport.instance.getConfig().getString("click-teleport.message")));
                                                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.c(SReport.instance.getConfig().getString("click-teleport.hover").replace("%target%", target.getPlayer().getName()))).create()));
                                                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, SReport.instance.getConfig().getString("click-teleport.command").replace("%target%", target.getName())));
                                                staff.spigot().sendMessage(textComponent);
                                            }
                                        }
                                    }
                                }
                                Cooldowns.addCooldown("report", p, SReport.instance.getConfig().getInt("cooldown"));
                                if (SReport.instance.getConfig().getBoolean("toggle.reported.enabled")) {
                                    p.sendMessage(Utils.c(SReport.instance.getConfig().getString("reported").replace("%target%", target.getName()).replace("%reason%", msg).replace("%prefix%", prefix(""))));
                                }
                                if (SReport.instance.getConfig().getBoolean("toggle.reminder.enabled")) {
                                    p.sendMessage(Utils.c(SReport.instance.getConfig().getString("reminder").replace("%prefix%", prefix(""))));
                                }
                            }
                        }
                    } else {
                        p.sendMessage(Utils.c(SReport.instance.getConfig().getString("no-permissions").replace("%prefix%", prefix(""))));
                        Bukkit.getLogger().warning("[" + p.getName() + ": Tried to use the command /" + label + " but was denied]");
                    }
                }
            } else {
                sender.sendMessage("This command can't be perform on Console.");
            }
        }
        return false;
    }
}
