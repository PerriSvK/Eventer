package sk.perri.eventer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventerCommandExecutor implements CommandExecutor
{
    private Main plugin;

    EventerCommandExecutor(Main plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(args.length == 0 || args[0].equalsIgnoreCase("help"))
        {
            if(sender instanceof Player)
                plugin.pinfo((Player) sender, getHelp((Player) sender));
            else
                plugin.info("eventer reload - reload the plugin\n eventer arenas - show arenas");

            return true;
        }

        if(args[0].equalsIgnoreCase("arenas"))
        {
            if(sender instanceof Player)
            {
                plugin.pinfo((Player) sender, "Available arenas: §a" + plugin.getArenaNames());
                if(sender.hasPermission("eventer.admin"))
                    plugin.pinfo((Player) sender, "Hidden arenas: " + plugin.getHiddenArenaNames());
            }
            else
            {
                plugin.info("Available arenas: " + plugin.getArenaNames());
                plugin.info("Hidden arenas: " + plugin.getHiddenArenaNames());
            }

            return true;
        }

        if(!(sender instanceof Player))
            return true;

        // Norml users commands

        if(args[0].equalsIgnoreCase("join"))
        {
            if(args.length == 2)
            {
                int result = plugin.commander.playerArenaJoin((Player) sender, args[1]);

                switch (result)
                {
                    case 0: plugin.pinfo((Player) sender, "Teleporting to the arena §6" +
                                plugin.players.get(((Player) sender).getDisplayName()).getTitle() + "§7!"); break;
                    case 1: plugin.perror((Player) sender, "This arena doesn't exist!"); break;
                    case 2: plugin.perror((Player) sender, "You have already joined the arena!"); break;
                    case 3: plugin.perror((Player) sender, "The arena is not open!"); break;
                }
            }
            else
                plugin.pinfo((Player) sender, "Use: /ev join <arena>");
        }

        if(args[0].equalsIgnoreCase("leave"))
        {
            int result = plugin.commander.playerArenaLeave((Player) sender);

            if(result == 0)
                plugin.pinfo((Player) sender, "Teleporting out of the arena!");
            else if(result == 1)
                plugin.perror((Player) sender, "You are not in any arena!");
        }

        if(args[0].equalsIgnoreCase("restart"))
        {
            int result = plugin.commander.playerArenaRestart((Player) sender);

            if(result == 0)
                plugin.pinfo((Player) sender, "Teleporting to the start!");
            else if(result == 1)
                plugin.perror((Player) sender, "You are not in any arena!");
        }

        if(args[0].equalsIgnoreCase("cp"))
        {
            int result = plugin.commander.playerArenaCheckpoint((Player) sender);

            if(result == 2)
                plugin.perror((Player) sender, "Checkpoint system is not enabled!");
            else if(result == 1)
                plugin.perror((Player) sender, "You are not in any arena!");
        }

        // Moderator
        if(!sender.hasPermission("eventer.moderator") && !sender.hasPermission("eventer.admin"))
            return true;

        if(args[0].equalsIgnoreCase("open"))
        {
            if(args.length == 2)
            {
                int result = plugin.commander.openArena(args[1]);

                switch (result)
                {
                    case 0: plugin.pinfo((Player) sender, "Arena §6"+
                            plugin.getArenas().get(args[1]).getTitle()+"§7 has been opened!"); break;
                    case 1: plugin.perror((Player) sender, "This arena doesn't exist!"); break;
                    case 2: plugin.perror((Player) sender, "This arena is already opened!"); break;
                    case 3: plugin.perror((Player) sender, "This arena is not loaded!"); break;
                }
            }
            else
                plugin.pinfo((Player) sender, "Use: /ev open <arena>");
        }

        if(args[0].equalsIgnoreCase("close"))
        {
            if(args.length == 2)
            {
                int result = plugin.commander.closeArena(args[1]);

                switch (result)
                {
                    case 0: plugin.pinfo((Player) sender, "Arena §6"+
                            plugin.players.get(((Player) sender).getDisplayName()).getTitle()+"§7 has been closed!"); break;
                    case 1: plugin.perror((Player) sender, "This arena doesn't exist!"); break;
                    case 2: plugin.perror((Player) sender, "This arena is not open!"); break;
                }
            }
            else
                plugin.pinfo((Player) sender, "Use: /ev close <arena>");
        }

        if(args[0].equalsIgnoreCase("kick"))
        {
            if(args.length == 2)
            {
                int result = plugin.commander.kickPlayer(args[1]);

                switch (result)
                {
                    case 0: plugin.pinfo((Player) sender, "Player has been kicked!"); break;
                    case 1: plugin.perror((Player) sender, "Player isn't at any arena!"); break;
                }
            }
            else
                plugin.pinfo((Player) sender, "Use: /ev kick <player>");
        }

        if(args[0].equalsIgnoreCase("kickall"))
        {
            if(args.length == 2)
            {
                int result = plugin.commander.kickAll(args[1]);

                if(result == -1)
                    plugin.perror((Player) sender, "This arena doesn't exist!");
                else
                    plugin.pinfo((Player) sender, result+" has been kicked!");
            }
            else
                plugin.pinfo((Player) sender, "Use: /ev kickall <arena>");
        }

        // Admin
        if(!sender.hasPermission("eventer.admin"))
            return true;

        if(args[0].equalsIgnoreCase("reload"))
        {
            plugin.reload();
            plugin.pinfo((Player) sender, "§ePlugin has been reloaded!");
        }

        if(args[0].equalsIgnoreCase("hide"))
        {
            if(args.length == 2)
            {
                int result = plugin.commander.hideArena(args[1]);

                switch(result)
                {
                    case 0: plugin.pinfo((Player) sender, "Arena §6"+
                            plugin.getArenas().get(args[1]).getTitle()+"§7 has been hidden!"); break;
                    case 1: plugin.perror((Player) sender, "This arena doesn't exist!"); break;
                    case 2: plugin.perror((Player) sender, "This arena is already hidden!"); break;
                }
            }
            else
                plugin.pinfo((Player) sender, "Use: /ev hide <arena>");
        }

        if(args[0].equalsIgnoreCase("show"))
        {
            if(args.length == 2)
            {
                int result = plugin.commander.showArena(args[1]);

                switch (result)
                {
                    case 0: plugin.pinfo((Player) sender, "Arena §6"+
                            plugin.getArenas().get(args[1]).getTitle()+"§7 has been shown!"); break;
                    case 1: plugin.perror((Player) sender, "This arena doesn't exist!"); break;
                    case 2: plugin.perror((Player) sender, "This arena is not hidden!"); break;
                }
            }
            else
                plugin.pinfo((Player) sender, "Use: /ev show <arena>");
        }

        if(args[0].equalsIgnoreCase("delete"))
        {
            if(args.length == 2)
            {
                int result = plugin.commander.deleteArena(args[1]);

                switch (result)
                {
                    case 0: plugin.pinfo((Player) sender, "§cArena §l"+args[1]+"§r§c has been deleted!"); break;
                    case 1: plugin.perror((Player) sender, "This arena doesn't exist!"); break;
                }
            }
            else
                plugin.pinfo((Player) sender, "Use: /ev delete <arena>");
        }

        if(args[0].equalsIgnoreCase("setup"))
        {
            if(args.length == 2)
            {
                if(args[1].equalsIgnoreCase("cancel"))
                {
                    if(plugin.commander.cancelSetup((Player) sender))
                        plugin.pinfo((Player) sender, "Setup has been canceled!");
                    else
                        plugin.perror((Player) sender, "You are running no setup!");
                }
                else if(args[1].equalsIgnoreCase("save"))
                {
                    int res = plugin.commander.saveSetup((Player) sender);
                    switch(res)
                    {
                        case 0: plugin.pinfo((Player) sender, "§aArena has been saved!"); break;
                        case 1: plugin.perror((Player) sender, "Not all required positions are set!"); break;
                        case 2: plugin.perror((Player) sender, "You are running no setup!"); break;
                        case 3: plugin.perror((Player) sender, "Failed to load new arena"); break;
                    }
                }
                else
                {
                    if (!plugin.commander.startSetup((Player) sender, args[1]))
                        plugin.perror((Player) sender, "Another your setup is running!");
                }
            }
            else
                plugin.pinfo((Player) sender, "Use: /ev setup <name|subcommand>");
        }

        if(args[0].equalsIgnoreCase("flags"))
        {
            plugin.pinfo((Player) sender, getFlags());
        }

        if(args[0].equalsIgnoreCase("setflag"))
        {
            if(args.length == 3)
            {
                plugin.commander.setArenaFlag("null", "null");
            }
            else
                plugin.pinfo((Player) sender, "Use: /ev setup <name|subcommand>");
        }

        return true;
    }

    private String getHelp(Player p)
    {
        String s = "Help for plugin §aEventer v"+plugin.getDescription().getVersion()+" §7by §2§lPerri\n";
        s += "§a/ev arenas §7- list of available arenas\n";
        s += "§a/ev join <arena> §7- join to the <arena>\n";
        s += "§a/ev leave §7- leave current arena\n";
        s += "§a/ev restart §7- teleport back to the start\n";
        s += "§a/ev cp §7- teleport to the checkpoint\n";

        if(p.hasPermission("eventer.moderator"))
        {
            s += "§6/ev open <arena> §7- Open arena for players (tp)\n";
            s += "§6/ev close <arena> §7- Close arena for players (tp)\n";
            s += "§6/ev kick <player> §7- Kick player from the <arena>\n";
            s += "§6/ev kickall <arena> §7- Kick all players from the <arena>\n";
        }

        if(p.hasPermission("eventer.admin") || p.hasPermission("eventer.*"))
        {
            s += "§c/ev setup <name|subcommand> §7- Start arena setup | Manage area setup\n";
            s += "§c/ev hide <arena> §7- Hide <arena> in list\n";
            s += "§c/ev show <arena> §7- Add <arena> to the list\n";
            s += "§c/ev delete <arena> §7- Delete <arena> §c(no undo)\n";
            s += "§c/ev flags §7- List of flags\n";
            s += "§c/ev setflag <arena> <flag> §7- Toggle <flag> in <arena>\n";
            s += "§c/ev reload §7- Reload plugin\n";
        }

        return s;
    }

    private String getFlags()
    {
        StringBuilder s = new StringBuilder("Flags for areas:\n §a");
        plugin.flags.forEach(f -> s.append(f).append(" "));

        return s.toString();
    }
}
