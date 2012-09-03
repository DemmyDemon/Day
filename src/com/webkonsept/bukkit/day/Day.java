package com.webkonsept.bukkit.day;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.webkonsept.KonseptConfig;


public class Day extends JavaPlugin implements Listener {
    private final String pluginName = "Day";
    private String pluginVersion = "???";
	private Logger log = Logger.getLogger("Minecraft");
	private HashMap<String,Long> lastUsed = new HashMap<String,Long>();
	private KonseptConfig cfg;
    private HashSet<Player> azizPlayers = new HashSet<Player>();
    private HashSet<Player> zorgPlayers = new HashSet<Player>();
	
	@Override
	public void onDisable() {
        for (Player player : azizPlayers){
            player.resetPlayerTime();
            player.sendMessage(ChatColor.GOLD+"Time cycle returned to normal.");
        }
        azizPlayers.clear();
		getServer().getScheduler().cancelTasks(this);
	}

	@Override
	public void onEnable() {
		pluginVersion = getDescription().getVersion();
		cfg = new KonseptConfig(this);
		cfg.refresh();
	    getServer().getPluginManager().registerEvents(this,this);
	}

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        azizPlayers.remove(event.getPlayer());
        zorgPlayers.remove(event.getPlayer());
    }
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (!this.isEnabled()) return false;

        boolean broadcastMessages = cfg.get().getBoolean("broadcastMessages",true);
        boolean stfu = cfg.get().getBoolean("stfu",false);

		if (command.getName().equalsIgnoreCase("day")){
		    int morning = cfg.get().getInt("morning",250);
			if (sender instanceof Player){
				Player player = (Player) sender;
				
				if (player.hasPermission("day.command.day")){
				    if (throttle(player)){
				        broadcast(player,cfg.tr("playerSummon"));
				        player.getWorld().setTime(morning);
                        azizPlayers.remove(player);
                        zorgPlayers.remove(player);
                        player.resetPlayerTime();
				    }
				    else {
				        player.sendMessage(ChatColor.RED+cfg.tr("playerThrottle"));
				    }
				}
				else {
					sender.sendMessage(ChatColor.RED+cfg.tr("commandDenied"));
				}
			}
			else {
				for (World world : getServer().getWorlds()){
					world.setTime(morning);
				}
                if (stfu){
                     verbose("Console summoned the light, but I'm not telling anyone!");
                }
                else if (broadcastMessages){
				    getServer().broadcastMessage(ChatColor.DARK_PURPLE+cfg.tr("consoleSummon"));
                }
                else {
                    sender.sendMessage(ChatColor.DARK_PURPLE+cfg.tr("consoleSummon"));
                }
			}
			return true;
		}
		else if (command.getName().equalsIgnoreCase("night")){
		    int evening = cfg.get().getInt("evening",14250);
			if (sender instanceof Player){
				Player player = (Player) sender;
				if (player.hasPermission("day.command.night")){
				    if (throttle(player)){
				        broadcast(player,cfg.tr("playerBanish"));
				        player.getWorld().setTime(evening);
                        azizPlayers.remove(player);
                        zorgPlayers.remove(player);
                        player.resetPlayerTime();

				    }
				    else {
				        player.sendMessage(ChatColor.RED+cfg.tr("playerThrottle"));
				    }
				}
				else {
					player.sendMessage(cfg.tr("commandDenied"));
				}
			}
			else {
				for (World world : getServer().getWorlds()){
					world.setTime(evening); 
				}
                if (stfu){
                    verbose("Console banished the daystar, but I'm keeping quiet about it.");
                }
                else if (broadcastMessages){
				    this.getServer().broadcastMessage(ChatColor.DARK_PURPLE+cfg.tr("consoleBanish"));
                }
                else {
                    sender.sendMessage(ChatColor.DARK_PURPLE+cfg.tr("consoleBanish"));
                }
			}
			return true;
		}
        else if (command.getName().equalsIgnoreCase("zorg")){
            Player player = null;
            if (sender instanceof Player){
                player = (Player) sender;
                if (player.hasPermission("day.command.zorg")){
                    zorg(player);
                    return true;
                }
            }
            else if (args.length == 1){
                player = getServer().getPlayer(args[0]);
            }
            else {
                error("You cant use the zorg command from console without supplying a player name!");
            }

            if (player != null){
                zorg(player);
                return true;
            }
            else {
                error("Player not found!");
            }
            return false;
        }
        else if (command.getName().equalsIgnoreCase("aziz")){
            Player player = null;
            if (sender instanceof Player){
                player = (Player) sender;
                if (player.hasPermission("day.command.aziz")){
                    aziz(player);
                    return true;
                }
            }
            else if (args.length == 1){
                player = getServer().getPlayer(args[0]);
            }
            else {
                error("You cant use the aziz command from console without supplying a player name!");
            }

            if (player != null){
                aziz(player);
                return true;
            }
            else {
                error("Player not found!");
            }
            return false;
        }
		else if (command.getName().equalsIgnoreCase("dayreload")){
		    if (sender.hasPermission("day.command.reload")){
		        cfg.refresh();
		        sender.sendMessage(ChatColor.GOLD+cfg.tr("commandReloaded"));
		    }
		    else{
		        sender.sendMessage(ChatColor.RED+cfg.tr("commandDenied"));
		    }
		    return true;
		        
		}
		else {
			return false;
		}
	}
    private void aziz (Player player){
        if (azizPlayers.contains(player)){
            player.sendMessage(ChatColor.GOLD+cfg.tr("normalTime"));
            azizPlayers.remove(player);
            player.resetPlayerTime();
        }
        else {
            player.sendMessage(ChatColor.GOLD+cfg.tr("azizTime"));
            zorgPlayers.remove(player);
            azizPlayers.add(player);
            player.setPlayerTime(6000,false);
        }
    }
    public void zorg (Player player){
        if (zorgPlayers.contains(player)){
            player.sendMessage(ChatColor.GOLD+cfg.tr("normalTime"));
            zorgPlayers.remove(player);
            player.resetPlayerTime();
        }
        else {
            player.sendMessage(ChatColor.GOLD+cfg.tr("zorgTime"));
            zorgPlayers.add(player);
            azizPlayers.remove(player);
            player.setPlayerTime(18000,false);
        }
    }
	public void broadcast(Player player,String whatHeDid){
        boolean broadcastMessages = cfg.get().getBoolean("broadcastMessages",true);
        boolean stfu = cfg.get().getBoolean("stfu",false);
        if (stfu){
            verbose(player.getName()+" "+whatHeDid+" (But I stfu about it!)");
        }
        else if (broadcastMessages){
		    for (Player playerOnline : player.getWorld().getPlayers()){
			    playerOnline.sendMessage(ChatColor.DARK_PURPLE+player.getName()+" "+whatHeDid);
		    }
        }
        else {
            player.sendMessage(ChatColor.DARK_PURPLE+player.getName()+" "+whatHeDid);
        }
	}
	private boolean throttle(Player player) {
	    boolean timeOK = false;
	    long limit = System.currentTimeMillis() - (cfg.get().getInt("throttleSeconds",10) * 1000); // *1000 because currentTime[[MILLIS]], yeah?
	    if (!lastUsed.containsKey(player.getName()) || lastUsed.get(player.getName()) < limit ){
	        timeOK = true;
	        lastUsed.put(player.getName(),System.currentTimeMillis());
        }
        return timeOK;
	}
	public void out(String message) {
	    log.info("["+pluginName+" "+pluginVersion+"] "+message);
	}
	public void verbose(String message){
	    if (cfg.get().getBoolean("verbose",false)){
	        log.info("["+pluginName+" "+pluginVersion+" VERBOSE] "+message);
	    }
	}
	public void error(String message){
		log.severe("["+pluginName+" "+pluginVersion+" ERROR] "+message);
	}
}