package fr.reizam.mlockets.manager;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.reizam.mlockets.MLockets;
import fr.reizam.mlockets.utils.Cuboid;
import fr.reizam.mlockets.utils.ItemBuilder;
import fr.reizam.mlockets.utils.VaultUtils;

public class Locket {

	private String name;
	private Location one;
	private Location two;
	private int prixInitial;
	private String faction; 
	private boolean bid = false;
	private int dernierEncheres;
	
	public Locket(String name, Location one,Location two, int prixInitial) {
		this.name = name;
		this.one = one;
		this.two = two;
		this.prixInitial = prixInitial;
		this.faction = "";
		this.dernierEncheres = prixInitial;
	}
	
	public void setDernierEncheres(int dernierEncheres) {
		this.dernierEncheres = dernierEncheres;
	}
	
	public int getDernierEncheres() {
		return dernierEncheres;
	}
	
	public int getPrixInitial() {
		return prixInitial;
	}
	
	public void setFaction(String faction) {
		this.faction = faction;
	}
	
	public String getFaction() {
		return faction;
	}
	
	public String getName() {
		return name;
	}
	
	public Cuboid getCuboid() {
		return new Cuboid(one, two);
	}

	public boolean isBid() {
		return bid;
	}

	public void setBid(boolean bid) {
		this.bid = bid;
		MLockets.getManager().getEncheres().updateLockets();
	}
	
	public ItemStack toItemStack() {
		return new ItemBuilder(Material.SIGN).setName("§c• §e"+name).setLore(Arrays.asList(
				"§e§m--------------------",
				" ",
				"§e• Enchères ouverte : "+(bid == true ? "§aOui" : "§cNon"),
				(bid == true ? "§e• Dernieres enchère : §c"+VaultUtils.getEconomy().format(dernierEncheres)+" ("+(faction.equals("") ? "Personne" : faction)+")" : "§e• Propriétaire : §c"+ (faction.equals("") ? "Personne" : faction)),
				" ",
				"§e§m--------------------")).toItemStack();
	}
	
}
