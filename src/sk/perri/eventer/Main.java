package sk.perri.eventer;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import sk.perri.eventer.arenas.Arena;
import sk.perri.eventer.arenas.Setup;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main extends JavaPlugin
{
    private YamlConfiguration arenasConf;
    private Map<String, Arena> arenas = new HashMap<>();
    Map<String, Arena> players = new HashMap<>();
    Map<String, Inventory> invs = new HashMap<>();
    Map<String, Location> plLocation = new HashMap<>();
    List<String> flags;
    private Map<String, Setup> setups = new HashMap<>();
    Commander commander;

    @Override
    public void onEnable()
    {
        // Flags - preinit
        flags = new Vector<String>(Arrays.asList("fly", "dmg", "break", "build", "gm0", "leave"));

        // INIT
        arenas.clear();
        players.clear();
        invs.clear();
        plLocation.clear();

        // Files
        if(!getDataFolder().exists())
        {
            if(getDataFolder().mkdirs())
                info("Data folder has been created!");
            else
                warning("Unable to create data folder!");
        }

        saveDefaultConfig();
        if(!(new File(getDataFolder().getPath()+"\\arenas.yml").exists()))
            saveResource("arenas.yml", false);

        arenasConf = YamlConfiguration.loadConfiguration(new File(getDataFolder().getPath()+"\\arenas.yml"));

        // Load arenas
        for(String s : arenasConf.getKeys(false))
        {
            Arena a = new Arena(s);
            if(a.load(arenasConf.getConfigurationSection(s)))
            {
                arenas.put(s, a);
                //getLogger().info("Arena '"+s+"' has been loaded!"); // Spam
            }
            else
                warning("Unable to load arena '"+s+"'");
        }
        getLogger().info(arenas.size()+" arenas has been loaded!");

        // Commands
        getCommand("eventer").setExecutor(new EventerCommandExecutor(this));

        // Events
        getServer().getPluginManager().registerEvents(new EventerEventListener(this), this);

        // Commander
        commander = new Commander(this);

        getLogger().info("Plugin has started!");
    }

    @Override
    public void onDisable()
    {
        getLogger().info("Plugin has disabled!");
    }

    void info(String msg)
    {
        getLogger().info("[Eventer] "+msg);
    }

    void warning(String msg)
    {
        getLogger().warning("[Eventer] WARN: "+msg);
    }

    public void pinfo(Player p, String msg)
    {
        p.sendMessage("§8[§aEventer§8] §7"+msg);
    }

    void perror(Player p, String msg)
    {
        p.sendMessage("§8[§aEventer§8] §c"+msg);
    }

    String getArenaNames()
    {
        StringBuilder s = new StringBuilder();
        arenas.values().forEach(a -> {if(!a.isHidden()) s.append(a.isOpen() ? "§a" : "§c").append(a.getName()).append(" ");});
        return s.toString();
    }

    String getHiddenArenaNames()
    {
        StringBuilder s = new StringBuilder();
        arenas.values().forEach(a -> {if(a.isHidden()) s.append(a.getName()).append(" ");});
        return s.toString();
    }

    void reload()
    {
        getServer().getPluginManager().disablePlugin(this);
        getServer().getPluginManager().enablePlugin(this);
    }

    Map<String, Arena> getArenas()
    {
        return arenas;
    }

    public void saveArenasConfig()
    {
        try
        {
            arenasConf.save(getDataFolder().getPath()+"\\arenas.yml");
        }
        catch (IOException e)
        {
            warning("Unable to save arenas.yml");
        }
    }

    public YamlConfiguration getArenasConf()
    {
        return arenasConf;
    }

    Map<String, Setup> getSetups()
    {
        return setups;
    }
}
