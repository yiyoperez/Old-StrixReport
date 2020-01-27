package us.strixmc.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.strixmc.SReport;
import us.strixmc.Utils.utils;

public class commandToggle implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!p.hasPermission(SReport.instance.getConfig().getString("permissions.report.toggle"))) {
                p.sendMessage(utils.c(SReport.instance.getConfig().getString("no-permissions").replace("%prefix%", utils.Prefix(""))));
                return true;
            }
            if (!utils.toggledReports.contains(p)) {
                utils.toggledReports.add(p);
                p.sendMessage(utils.c(SReport.instance.getConfig().getString("toggle-report.enabled").replace("%prefix%", utils.Prefix(""))));
            } else {
                utils.toggledReports.remove(p);
                p.sendMessage(utils.c(SReport.instance.getConfig().getString("toggle-report.disabled").replace("%prefix%", utils.Prefix(""))));
            }
        } else {
            sender.sendMessage("This command can't be perform on Console.");
        }
        return false;
    }
}
