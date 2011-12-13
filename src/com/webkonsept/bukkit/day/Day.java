package com.webkonsept.bukkit.day;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Day extends JavaPlugin {
	private Logger log = Logger.getLogger("Minecraft");
	private HashMap<String,Long> lastUsed = new HashMap<String,Long>();
	
	@Override
	public void onDisable() {
		this.out("Disabled");
	}

	@Override
	public void onEnable() {
		this.out("Enabled");
	}
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (!this.isEnabled()) return false;
		
		if (command.getName().equalsIgnoreCase("day")){
			if (sender instanceof Player){
				Player player = (Player) sender;
				
				if (this.permit(player, "day.command.day")){
					broadcast(player,"summoned the daystar");
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
					broadcast(player,"banished the light");
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
	public void broadcast(Player player,String whatHeDid){
		for (Player playerOnline : player.getWorld().getPlayers()){
			playerOnline.sendMessage(ChatColor.DARK_PURPLE+player.getName()+" "+whatHeDid);
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
		
		if (player.hasPermission(permission)){
			allow = true;
		}
		
		if (timeOK){
			return allow;
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