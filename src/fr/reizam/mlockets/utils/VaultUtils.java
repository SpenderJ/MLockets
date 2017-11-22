package fr.reizam.mlockets.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultUtils {
	
private static Economy economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
private static @Getter Permission permission = Bukkit.getServer().getServicesManager().getRegistration(Permission.class).getProvider();
	
	@SuppressWarnings("deprecation")
	public static String getGroup(String playerName) {
		OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(playerName);
		if (offPlayer == null) {
			return null;
		}
		return permission.getPrimaryGroup(Bukkit.getWorlds().get(0).getName(), offPlayer);
	}
	
	public static String getGroup(Player player) {
		return permission.getPrimaryGroup(player);
	}
	
	public static Economy getEconomy() {
		return economy;
	}
	
	@SuppressWarnings("deprecation")
	public void setGroup(String player, String group) {
		OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(player);
		if (offPlayer == null) {
			return;
		}
		permission.playerRemoveGroup(Bukkit.getWorlds().get(0).getName(), offPlayer, getGroup(player));
		permission.playerAddGroup(Bukkit.getWorlds().get(0).getName(), offPlayer, group);
	}
	
	public void setGroup(Player player, String group) {
		permission.playerRemoveGroup(player, getGroup(player));
		permission.playerAddGroup(player, group);
	}
	
	

}
