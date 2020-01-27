package us.strixmc.GUI;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.strixmc.SReport;
import us.strixmc.Utils.cooldowns;
import us.strixmc.Utils.utils;

import java.util.List;

public class reportGUI implements Listener {

    public static void guiReport(Player p, Player target) {
        Inventory inv = Bukkit.createInventory(null, 9 * SReport.instance.getConfig().getInt("GUI.rows"), utils.c(SReport.instance.getConfig().getString("GUI.name")));

        utils.checkGUI();

        for (String key : SReport.instance.getConfig().getConfigurationSection("GUI.items.").getKeys(false)) {

            String name = ChatColor.translateAlternateColorCodes('&', SReport.instance.getConfig().getString("GUI.items." + key + ".name"));
            String item = SReport.instance.getConfig().getString("GUI.items." + key + ".id");
            int slot = SReport.instance.getConfig().getInt("GUI.items." + key + ".slot");
            int amount = SReport.instance.getConfig().getInt("GUI.items." + key + ".amount");
            int data = SReport.instance.getConfig().getInt("GUI.items." + key + ".data");
            if (SReport.instance.getConfig().getInt("GUI.rows") == 0 || (SReport.instance.getConfig().getInt("GUI.rows") > 6)) {
                p.sendMessage(utils.c("&c[StrixReport] &e[Error] &cGUI.rows can't be 0 or more then 6."));
            }
            if (!SReport.instance.getConfig().isSet("GUI.items." + key + ".id")) {
                p.sendMessage(utils.c("&c[StrixReport] &e[Error] &c" + key + ".id is empty or null."));
            }
            if (!SReport.instance.getConfig().isSet("GUI.items." + key + ".slot")) {
                p.sendMessage(utils.c("&c[StrixReport] &e[Error] &c" + key + ".slot is empty or null."));
            }
            ItemStack c = new ItemStack(Material.matchMaterial(item));
            ItemMeta m = c.getItemMeta();

            m.setDisplayName(name);

            if (SReport.instance.getConfig().isSet("GUI.items." + key + ".amount")) {
                c.setAmount(amount);
            }

            if (SReport.instance.getConfig().isSet("GUI.items." + key + ".data")) {
                c.setDurability((short) data);
            }

            List<String> lore = SReport.instance.getConfig().getStringList("GUI.items." + key + ".lore");
            if (SReport.instance.getConfig().isSet("GUI.items." + key + ".lore")) {
                m.setLore(utils.cList(lore));
            }

            c.setItemMeta(m);

            inv.setItem(slot, c);
        }

        p.openInventory(inv);
    }

    @EventHandler
    public void onManage(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        Player p = (Player) e.getWhoClicked();
        ItemStack i = e.getCurrentItem();

        if (inv != null && i != null && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && e.getInventory().getName().equalsIgnoreCase(utils.c(SReport.instance.getConfig().getString("GUI.name")))) {
            e.setCancelled(true);

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.hasPermission(SReport.instance.getConfig().getString("permissions.report.receive")) && !utils.toggledReports.contains(online)) {
                    Player target = Bukkit.getPlayer(utils.test.get(p.getName()));
                    utils.updateAmount(target);
                    for (String s : SReport.instance.getConfig().getStringList("report")) {
                        online.sendMessage(utils.c(s).replace("%reason%", ChatColor.stripColor(i.getItemMeta().getDisplayName())).replace("%player%", p.getName()).replace("%target%", target.getName()).replace("%amount%", utils.Ramount.get(target).toString()));
                    }
                    if (SReport.instance.getConfig().getBoolean("toggle.click-teleport.enabled")) {
                        final TextComponent textComponent = new TextComponent();
                        textComponent.setText(utils.c(SReport.instance.getConfig().getString("click-teleport.message")));
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(utils.c(SReport.instance.getConfig().getString("click-teleport.hover").replace("%target%", utils.test.get(p.getName())))).create()));
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, SReport.instance.getConfig().getString("click-teleport.command").replace("%target%", utils.test.get(p.getName()))));
                        online.spigot().sendMessage(textComponent);
                    }
                    if (SReport.instance.getConfig().getBoolean("toggle.reported.enabled")) {
                        p.sendMessage(utils.c(SReport.instance.getConfig().getString("reported").replace("%target%", utils.test.get(p.getName())).replace("%reason%", ChatColor.stripColor(i.getItemMeta().getDisplayName())).replace("%prefix%", utils.Prefix(""))));
                    }
                    if (SReport.instance.getConfig().getBoolean("toggle.reminder.enabled")) {
                        p.sendMessage(utils.c(SReport.instance.getConfig().getString("reminder").replace("%prefix%", utils.Prefix(""))));
                    }
                }
            }

            utils.test.remove(p.getName());
            p.closeInventory();
            cooldowns.addCooldown("report", p, SReport.instance.getConfig().getInt("cooldown"));

        }
    }
}

