package com.webkonsept.bukkit.day;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;

public class DayPlayerListener extends PlayerListener {
	private Day plugin;

	DayPlayerListener (Day instance){
		plugin = instance;
	}

	public void onPlayerCommandPreprocess (PlayerCommandPreprocessEvent event){
		
		if (!plugin.isEnabled()) return;
		if (event.isCancelled()) return;
		
		if (event.getMessage().equalsIgnoreCase("/day")){
			event.setCancelled(true);
			Player player = event.getPlayer();
			if (plugin.permit(player, "day.command.day")){
				
				plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE+player.getName()+" summoned the daystar");
				plugin.getServer().getWorld(player.getWorld().getName()).setTime(250);
			}
			else {
				player.sendMessage("Sorry!  Permission to force the sun up denied!");
			}
		}
		else if (event.getMessage().equalsIgnoreCase("/night")){
			event.setCancelled(true);
			Player player = event.getPlayer();
			
			if (plugin.permit(player, "day.command.night")){
				plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE+player.getName()+" plunged the world into darkness");
				plugin.getServer().getWorld(player.getWorld().getName()).setTime(14250);
			}
			else {
				player.sendMessage("Sorry!  Permission to force the sun down denied!");
			}
		}
	}
}
