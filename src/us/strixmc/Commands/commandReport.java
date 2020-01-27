package us.strixmc.Commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.strixmc.GUI.reportGUI;
import us.strixmc.SReport;
import us.strixmc.Utils.cooldowns;
import us.strixmc.Utils.utils;

public class commandReport implements CommandExecutor {

    private String prefix() {
        if (SReport.instance.getConfig().getBoolean("toggle.prefix.enabled")) {
            return utils.c(SReport.instance.getConfig().getString("prefix" ));
        }
        return "";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!p.hasPermission(SReport.instance.getConfig().getString("permissions.report.main"))) {
                p.sendMessage(utils.c(SReport.instance.getConfig().getString("no-permissions").replace("%prefix%", prefix())));
                return true;
            }
            if (args.length == 0) {
                for (String s : SReport.instance.getConfig().getStringList("usage")) {
                    p.sendMessage(utils.c(s).replace("%command%", label).replace("%prefix%", prefix()));
                }
            } else {
                if (cooldowns.isOnCooldown("report", p)) {
                    p.sendMessage(utils.c(SReport.instance.getConfig().getString("on-cooldown").replace("%seconds%", String.valueOf(cooldowns.getCooldownForPlayerInt("report", p)))));
                } else {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        p.sendMessage(utils.c(SReport.instance.getConfig().getString("not-found").replace("%player%", args[0]).replace("%prefix%", prefix())));
                        return true;
                    }
                    if (target.equals(sender) && !SReport.instance.getConfig().getBoolean("toggle.self-report.enabled")) {
                        p.sendMessage(utils.c(SReport.instance.getConfig().getString("self-report").replace("%prefix%", prefix())));
                        return true;
                    }
                    if (SReport.instance.getConfig().getBoolean("toggle.report-gui.enabled")) {
                        reportGUI.guiReport(p, target);
                        utils.test.put(p.getName(), target.getName());
                    } else {
                        if (args.length == 1) {
                            for (String s : SReport.instance.getConfig().getStringList("usage")) {
                                p.sendMessage(utils.c(s).replace("%command%", label).replace("%prefix%", prefix()));
                            }
                        }
                        if (args.length > 1) {
                            String msg = "";
                            for (int i = 1; i < args.length; ++i) {
                                msg = String.valueOf(msg) + args[i] + " ";
                            }
                            utils.updateAmount(target);
                            for (Player staff : Bukkit.getOnlinePlayers()) {
                                if (staff.hasPermission(SReport.instance.getConfig().getString("permissions.report.receive")) && !utils.toggledReports.contains(staff)) {
                                    for (String s : SReport.instance.getConfig().getStringList("report")) {
                                        staff.sendMessage(utils.c(s).replace("%target%", target.getName()).replace("%reason%", msg).replace("%player%", p.getName()).replace("%amount%", utils.Ramount.get(target).toString()));
                                    }
                                    if (SReport.instance.getConfig().getBoolean("toggle.click-teleport.enabled")) {
                                        final TextComponent textComponent = new TextComponent();
                                        textComponent.setText(utils.c(SReport.instance.getConfig().getString("click-teleport.message")));
                                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(utils.c(SReport.instance.getConfig().getString("click-teleport.hover").replace("%target%", target.getName()))).create()));
                                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, SReport.instance.getConfig().getString("click-teleport.command").replace("%target%", target.getName())));
                                        staff.spigot().sendMessage(textComponent);
                                    }
                                }
                            }
                            if (SReport.instance.getConfig().getBoolean("toggle.reported.enabled")) {
                                p.sendMessage(utils.c(SReport.instance.getConfig().getString("reported").replace("%target%", target.getName()).replace("%reason%", msg).replace("%prefix%", prefix())));
                            }
                            if (SReport.instance.getConfig().getBoolean("toggle.reminder.enabled")) {
                                p.sendMessage(utils.c(SReport.instance.getConfig().getString("reminder").replace("%prefix%", prefix())));
                            }
                        }
                    }
                }
            }
        } else {
            sender.sendMessage("This command can't be perform on Console.");
        }
        return false;
    }
}
