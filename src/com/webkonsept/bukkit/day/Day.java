package com.webkonsept.bukkit.day;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Day extends JavaPlugin {
	private Logger log = Logger.getLogger("Minecraft");
	public PermissionHandler Permissions;
	private boolean usePermissions = false;
	public HashMap<String,Boolean> fallbackPermissions = new HashMap<String,Boolean>();
	
	@Override
	public void onDisable() {
		fallbackPermissions.clear();
		this.out("Disabled");
	}

	@Override
	public void onEnable() {
		usePermissions = setupPermissions();
		if(!usePermissions){
			fallbackPermissions.put("day.command.day",false);
			fallbackPermissions.put("day.command.night",false);
		}
		this.out("Enabled");
	}
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (!this.isEnabled()) return false;
		
		if (command.getName().equalsIgnoreCase("/day")){
			if (sender instanceof Player){
				Player player = (Player) sender;
				if (this.permit(player, "day.command.day")){
					this.getServer().broadcastMessage(ChatColor.DARK_PURPLE+player.getName()+" summoned the daystar");
					this.getServer().getWorld(player.getWorld().getName()).setTime(250);
				}
				else {
					sender.sendMessage("Sorry!  Permission to force the sun up denied!");
				}
			}
			else {
				Iterator<World> worlds = this.getServer().getWorlds().iterator();
				this.getServer().broadcastMessage(ChatColor.DARK_PURPLE+"Daystar summoned from console");
				while (worlds.hasNext()){
					World world = worlds.next();
					world.setTime(250);
				}
				return true;
			}
		}
		else if (command.getName().equalsIgnoreCase("/night")){
			if (sender instanceof Player){
				Player player = (Player) sender;
				if (this.permit(player, "day.command.night")){
					this.getServer().broadcastMessage(ChatColor.DARK_PURPLE+player.getName()+" plunged the world into darkness");
					this.getServer().getWorld(player.getWorld().getName()).setTime(14250);
				}
				else {
					player.sendMessage("Sorry!  Permission to force the sun down denied!");
				}
				return true;
			}
			else {
				Iterator<World> worlds = this.getServer().getWorlds().iterator();
				this.getServer().broadcastMessage(ChatColor.DARK_PURPLE+"World plunged into darkness from console");
				while (worlds.hasNext()){
					World world = worlds.next();
					world.setTime(14250);
				}
				return true;
			}
		}
		else if (command.getName().equalsIgnoreCase("/reloadserver")){
			if (sender instanceof Player){
				sender.sendMessage("Sorry, please do that from the console!");
				return false;
			}
			else {
				sender.sendMessage("Reloading!");
				this.getServer().reload();
				return true;
			}
		}
		else {
			return false;
		}
		
		return false; // Fuck you, Java.  This will never ever be executed!
	}
	public boolean permit(Player player,String permission){ 
		boolean allow = false; // Default to GTFO
		if ( usePermissions ){
			if (Permissions.has(player,permission)){
				allow = true;
			}
		}
		else if (player.isOp()){
			allow = true;
		}
		else {
			if (fallbackPermissions.get(permission) || false){
				allow = true;
			}
		}
		return allow;
	}
	private boolean setupPermissions() {
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
		if (this.Permissions == null){
			if (test != null){
				this.Permissions = ((Permissions)test).getHandler();
				return true;
			}
			else {
				this.out("Permissions plugin not found, defaulting to OPS CHECK mode");
				return false;
			}
		}
		else {
			this.crap("Urr, this is odd...  Permissions are already set up!");
			return true;
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