package us.strixmc.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.strixmc.SReport;
import us.strixmc.Utils.Utils;

public class CommandToggle implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("togglereport")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (p.hasPermission(SReport.instance.getConfig().getString("permissions.report.toggle"))) {
                    if (args.length == 0) {
                        if (!Utils.toggledReports.contains(p)) {
                            Utils.toggledReports.add(p);
                            p.sendMessage(Utils.c(SReport.instance.getConfig().getString("toggle-report.enabled").replace("%prefix%", Utils.Prefix(""))));
                        } else {
                            Utils.toggledReports.remove(p);
                            p.sendMessage(Utils.c(SReport.instance.getConfig().getString("toggle-report.disabled").replace("%prefix%", Utils.Prefix(""))));
                        }
                    }
                    if (args.length > 0) {
                        p.sendMessage(Utils.c(SReport.instance.getConfig().getString("toggle-report.usage").replace("%command%", label).replace("%prefix%", Utils.Prefix(""))));
                    }
                } else {
                    p.sendMessage(Utils.c(SReport.instance.getConfig().getString("no-permissions").replace("%prefix%", Utils.Prefix(""))));
                }
            } else {
                sender.sendMessage("This command can't be perform on Console.");
            }
        }
        return false;
    }
}
