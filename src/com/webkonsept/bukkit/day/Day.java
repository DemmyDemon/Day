package com.webkonsept.bukkit.day;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Day extends JavaPlugin {
	private Logger log = Logger.getLogger("Minecraft");
	public PermissionHandler Permissions;
	private DayPlayerListener playerListener = new DayPlayerListener(this);
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
		PluginManager pm =getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal, this);
		this.out("Enabled");
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