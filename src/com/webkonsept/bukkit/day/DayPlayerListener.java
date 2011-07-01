package com.webkonsept.bukkit.day;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class DayPlayerListener extends PlayerListener {
	Day plugin;
	DayPlayerListener(Day instance){
		plugin = instance;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event){
		if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			Player player = event.getPlayer();
			if (player.getItemInHand().getType().equals(Material.WATCH)){
				World world = player.getWorld();
				if ((world.getTime() > 250 && world.getTime() < 14250) && plugin.permit(player, "day.item.night")){
					plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE+player.getName()+" plunged the world into darkness");
					plugin.getServer().getWorld(player.getWorld().getName()).setTime(14250);
				}
				else if (plugin.permit(player, "day.item.day")){
					plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE+player.getName()+" summoned the daystar");
					plugin.getServer().getWorld(player.getWorld().getName()).setTime(250);
				}
				else {
					player.sendMessage(ChatColor.DARK_PURPLE+"Permission denied.  Time setting too fast?");
				}
			}
		}
	}

}
