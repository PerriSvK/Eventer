package sk.perri.eventer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import sk.perri.eventer.arenas.Arena;
import sk.perri.eventer.arenas.Setup;

import java.util.ArrayList;
import java.util.List;

class Commander
{
    private Main plugin;

    Commander(Main plugin)
    {
        this.plugin = plugin;
    }

    int playerArenaJoin(Player p, String aname)
    {
        Arena a = plugin.getArenas().get(aname);

        if(a == null)
            return 1;

        if(plugin.players.containsKey(p.getDisplayName()))
            return 2;

        if(!a.isOpen())
            return 3;

        plugin.players.put(p.getDisplayName(), a);

        Inventory inv = Bukkit.createInventory(null, InventoryType.PLAYER);
        for(int i = 0; i < p.getInventory().getSize(); i++)
            inv.setItem(i, p.getInventory().getItem(i));

        plugin.invs.put(p.getDisplayName(), inv);
        p.getInventory().clear();

        plugin.plLocation.put(p.getDisplayName(), p.getLocation());
        p.teleport(a.getLoc().get("start"));

        return 0;
    }

    int playerArenaLeave(Player p)
    {
        if(!plugin.players.containsKey(p.getDisplayName()))
            return 1;

        plugin.players.remove(p.getDisplayName());
        Inventory inv = plugin.invs.remove(p.getDisplayName());

        for(int i = 0; i < inv.getSize(); i++)
            p.getInventory().setItem(i, inv.getItem(i));

        p.teleport(plugin.plLocation.remove(p.getDisplayName()));

        return 0;
    }

    int playerArenaRestart(Player p)
    {
        if(!plugin.players.containsKey(p.getDisplayName()))
            return 1;

        p.teleport(plugin.players.get(p.getDisplayName()).getLoc().get("start"));
        return 0;
    }

    int playerArenaCheckpoint(Player p)
    {
        if(!plugin.players.containsKey(p.getDisplayName()))
            return 1;

        return 2; // TODO checkpoint system
    }

    int openArena(String arena)
    {
        Arena a = plugin.getArenas().get(arena);

        if(a == null)
            return 1;

        if(a.isOpen())
            return 2;

        return a.open() ? 0 : 3;
    }

    int closeArena(String arena)
    {
        Arena a = plugin.getArenas().get(arena);

        if(a == null)
            return 1;

        return a.close() ? 0 : 2;
    }

    int kickPlayer(String player)
    {
        if(!plugin.players.containsKey(player))
            return 1;

        Player pl = plugin.getServer().getPlayer(player);

        playerArenaLeave(pl);
        plugin.pinfo(pl, "You has been kicked out of the arena!");

        return 0;
    }

    int kickAll(String arena)
    {
        Arena a = plugin.getArenas().get(arena);
        if(a == null)
            return -1;

        List<String> players = new ArrayList<>();
        plugin.players.forEach((p, ar) -> {if(ar == a) players.add(p);});
        players.forEach(this::kickPlayer);

        return players.size();
    }

    int hideArena(String arena)
    {
        Arena a = plugin.getArenas().get(arena);
        if(a == null)
            return 1;

        int res = a.hide() ? 0 : 2;
        plugin.saveArenasConfig();

        return res;
    }

    int showArena(String arena)
    {
        Arena a = plugin.getArenas().get(arena);
        if(a == null)
            return 1;

        int res = a.show() ? 0 : 2;
        plugin.saveArenasConfig();

        return res;
    }

    int deleteArena(String arena)
    {
        Arena a = plugin.getArenas().get(arena);
        if(a == null)
            return 1;

        plugin.getArenasConf().set(a.getName(), null);
        plugin.saveArenasConfig();
        plugin.getArenas().remove(a.getName());
        return 0;
    }

    boolean startSetup(Player p, String name)
    {
        if(plugin.getSetups().containsKey(p.getDisplayName()))
            return false;

        Setup s = new Setup(plugin, p, name);
        plugin.getSetups().put(p.getDisplayName(), s);
        s.init();
        plugin.getServer().getPluginManager().registerEvents(s, plugin);

        return true;
    }

    boolean cancelSetup(Player p)
    {
        if(!plugin.getSetups().containsKey(p.getDisplayName()))
            return false;

        plugin.getSetups().remove(p.getDisplayName());
        return true;
    }

    int saveSetup(Player p)
    {
        if(!plugin.getSetups().containsKey(p.getDisplayName()))
            return 2;

        Setup s = plugin.getSetups().get(p.getDisplayName());
        int res = s.save();

        if(res == 0)
        {
            Arena a = new Arena(s.getName());
            if(!a.load(plugin.getArenasConf().getConfigurationSection(s.getName())))
                return 3;

            plugin.getArenas().put(s.getName(), a);
            cancelSetup(p);
        }

        return res;
    }

    int setArenaFlag(String arena, String flag)
    {
        return 0;
    }
}
