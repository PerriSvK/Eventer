package sk.perri.eventer.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class Utils
{
    public static Location parseLocation(ConfigurationSection cs)
    {
        if(!cs.isSet("world") || !cs.isSet("x") || !cs.isSet("y") || !cs.isSet("z"))
            return null;

        World w = Bukkit.getWorld(cs.getString("world"));

        if(cs.isSet("pitch") && cs.isSet("yaw"))
            return new Location(w, cs.getDouble("x"), cs.getDouble("y"), cs.getDouble("z"),
                    (float) cs.getDouble("yaw"), (float) cs.getDouble("pitch"));

        return new Location(w, cs.getDouble("x"), cs.getDouble("y"), cs.getDouble("z"));
    }

    public static void parseLocation(ConfigurationSection cs, Location l)
    {
        cs.set("x", l.getX());
        cs.set("y", l.getY());
        cs.set("z", l.getZ());
        cs.set("world", l.getWorld().getName());
    }
}
