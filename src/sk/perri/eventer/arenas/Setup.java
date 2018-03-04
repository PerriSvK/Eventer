package sk.perri.eventer.arenas;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sk.perri.eventer.Main;
import sk.perri.eventer.utils.Utils;

import java.util.Vector;

public class Setup implements Listener
{
    private Main plugin;
    private Player player;
    private Inventory inv;
    private String name;
    private int step = 0;
    private Vector<Location> loc = new Vector<>();

    public Setup(Main plugin, Player player, String name)
    {
        this.player = player;
        this.plugin = plugin;
        this.name = name;
    }

    public void init()
    {
        inv = Bukkit.createInventory(null, InventoryType.PLAYER);
        for(int i = 0; i < player.getInventory().getSize(); i++)
        {
            inv.setItem(i, player.getInventory().getItem(i));
        }

        player.getInventory().clear();

        ItemStack is = new ItemStack(Material.STICK, 1);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName("§5§lSelector");
        is.setItemMeta(im);

        player.getInventory().setItem(0, is);
        player.getInventory().setHeldItemSlot(0);

        plugin.pinfo(player, "§6Select first position of area or write §c/ev setup cancel §6 to stop");
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event)
    {
        if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            loc.add(event.getClickedBlock().getLocation());

            switch(step)
            {
                case 0: plugin.pinfo(player, "§6First position set. Select second position"); break;
                case 1: plugin.pinfo(player, "§6Second position set. Select start position (where players will teleport)"); break;
                case 2: plugin.pinfo(player, "§6Starting position set. Use command §c/ev setup save §6to save"); break;
            }

            step++;

            event.setCancelled(true);
        }
    }

    public int save()
    {
        if(loc.size() < 3)
            return 1;

        ConfigurationSection cs = plugin.getArenasConf().createSection(name);
        Utils.parseLocation(cs.createSection("loc1"), loc.get(0));
        Utils.parseLocation(cs.createSection("loc2"), loc.get(1));
        Utils.parseLocation(cs.createSection("start"), loc.get(2));
        plugin.saveArenasConfig();

        for(int i = 0; i < inv.getSize(); i++)
        {
            player.getInventory().setItem(i, inv.getItem(i));
        }

        return 0;
    }

    public String getName()
    {
        return name;
    }
}
