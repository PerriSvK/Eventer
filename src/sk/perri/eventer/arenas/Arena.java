package sk.perri.eventer.arenas;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import sk.perri.eventer.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Arena
{
    private String name, title;
    private Map<String, Location> loc = new HashMap<>();
    private Map<String, Integer> flags = new HashMap<>();
    private ConfigurationSection cs;
    private boolean loaded = false;
    private boolean open = false;
    private boolean hidden = false;

    public Arena(String name)
    {
        this.name = name;
    }

    public boolean load(ConfigurationSection cs)
    {
        this.cs = cs;

        if(cs.isSet("title"))
            title = cs.getString("title");
        else
            title = name;

        if(cs.isSet("hidden") && cs.getBoolean("hidden"))
            hidden = true;

        if(!cs.isSet("loc1") || !cs.isSet("loc2") || !cs.isSet("start"))
            return false;

        loc.put("l1", Utils.parseLocation(cs.getConfigurationSection("loc1")));
        loc.put("l2", Utils.parseLocation(cs.getConfigurationSection("loc1")));
        loc.put("start", Utils.parseLocation(cs.getConfigurationSection("start")));

        if(cs.isSet("end"))
            loc.put("end", Utils.parseLocation(cs.getConfigurationSection("end")));
        else
            loc.put("end", loc.get("start"));

        if(loc.values().contains(null))
            return false;

        loaded = true;
        return true;
    }

    public String getTitle()
    {
        return title;
    }

    public String getName()
    {
        return name;
    }

    public Map<String, Location> getLoc()
    {
        return loc;
    }

    public boolean open()
    {
        if(loaded)
            open = true;
        else
            return false;

        return true;
    }

    public boolean close()
    {
        if(open)
            open = false;
        else
            return false;

        return true;
    }

    public boolean hide()
    {
        if(hidden)
            return false;

        cs.set("hidden", true);

        return true;
    }

    public boolean show()
    {
        if(!hidden)
            return false;

        cs.set("hidden", false);

        return true;
    }

    public boolean isOpen()
    {
        return open;
    }

    public boolean isHidden()
    {
        return hidden;
    }
}
