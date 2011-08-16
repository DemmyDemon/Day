package com.webkonsept.bukkit.day;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Day extends JavaPlugin {
	private Logger log = Logger.getLogger("Minecraft");
	private PermissionHandler Permissions;
	private boolean usePermissions = false;
	private boolean useSpout = false; 
	private HashMap<String,Long> lastUsed = new HashMap<String,Long>();
	
	@Override
	public void onDisable() {
		this.out("Disabled");
	}

	@Override
	public void onEnable() {
		usePermissions = setupPermissions();
		PluginManager pm = getServer().getPluginManager();
		if (pm.isPluginEnabled("Spout")){
			useSpout = true;
			this.out("Enabled, with Spout support");
		}
		else {
			this.out("Enabled");
		}
	}
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (!this.isEnabled()) return false;
		
		if (command.getName().equalsIgnoreCase("day")){
			if (sender instanceof Player){
				Player player = (Player) sender;
				
				if (this.permit(player, "day.command.day")){
					broadcast(player.getName(),"summoned the daystar");
					player.getWorld().setTime(250);
				}
				else {
					sender.sendMessage("Sorry!  Permission to force the sun up denied!  Switching too fast?");
				}
			}
			else {
				for (World world : getServer().getWorlds()){
					world.setTime(250);
					sender.sendMessage(world.getName()+" bathed in sunlight");
				}
				getServer().broadcastMessage(ChatColor.DARK_PURPLE+"The Daystar was summoned.");
			}
			return true;
		}
		else if (command.getName().equalsIgnoreCase("night")){
			if (sender instanceof Player){
				Player player = (Player) sender;
				if (this.permit(player, "day.command.night")){
					broadcast(player.getName(),"banished the light");
					player.getWorld().setTime(14250);
				}
				else {
					player.sendMessage("Sorry!  Permission to force the sun down denied!  Switching too fast?");
				}
			}
			else {
				for (World world : getServer().getWorlds()){
					world.setTime(14250); 
					sender.sendMessage(world.getName()+" plunged into darkness");
				}
				this.getServer().broadcastMessage(ChatColor.DARK_PURPLE+"The world was plunged into darkness.");
			}
			return true;
		}
		else {
			return false;
		}
	}
	public void broadcast(String playerName,String whatHeDid){
		for (Player player : getServer().getOnlinePlayers()){
			if (useSpout){
				SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
				if (sPlayer.isSpoutCraftEnabled()){
					sPlayer.sendNotification(playerName, whatHeDid, Material.PORTAL);
				}
				else {
					player.sendMessage(ChatColor.DARK_PURPLE+playerName+" "+whatHeDid);
				}
			}
			else {
				player.sendMessage(ChatColor.DARK_PURPLE+playerName+" "+whatHeDid);
			}
		}
	}
	public boolean permit(Player player,String permission){ 
		boolean allow = false; // Default to GTFO
		boolean timeOK = false;  // Assume it's throttled
		
		long limit = System.currentTimeMillis() - 10000; // Throttle at 10 seconds (10k milliseconds)
		
		if (!lastUsed.containsKey(player.getName()) || lastUsed.get(player.getName()) < limit ){
			timeOK = true;
			lastUsed.put(player.getName(),System.currentTimeMillis());
		}
		
		if ( usePermissions ){
			if (Permissions.has(player,permission)){
				allow = true;
			}
		}
		else if (player.hasPermission(permission)){
			allow = true;
		}
		
		if (timeOK){
			return allow;
		}
		else {
			return false;
		}
	}
	private boolean setupPermissions() {
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
		if (test != null){
			this.Permissions = ((Permissions)test).getHandler();
			return true;
		}
		else {
			return false;
		}
	}
	public void out(String message) {
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName()+ " " + pdfFile.getVersion() + "] " + message);
	}
	public void crap(String message){
		PluginDescriptionFile pdfFile = this.getDescription();
		log.severe("[" + pdfFile.getName()+ " " + pdfFile.getVersion() + " CRAP] " + message);
	}
}