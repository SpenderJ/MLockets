package fr.reizam.mlockets.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.reizam.mlockets.MLockets;
import fr.reizam.mlockets.manager.Locket;
import fr.reizam.mlockets.utils.ItemBuilder;

public class GuiEncheres {

	private Inventory inv;
	
	public GuiEncheres() {
		this.inv = Bukkit.createInventory(null, 45, "§eEnchères > Lockets");
		updateLockets();
		for(int i = 0;i < inv.getSize();i++) {
			if(inv.getItem(i) == null) {
				inv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
			}
		}
	}

	public void updateLockets() {
		int i = 0;
		for(Locket locket : MLockets.getManager().getLockets()) {
			i += 2;
			if(i == 2) {
				i-=1;
			}
			inv.setItem(i, locket.toItemStack());
		}
	}
	
	public void open(Player p) {
		p.openInventory(inv);
	}
}
