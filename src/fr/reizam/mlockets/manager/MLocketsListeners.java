package fr.reizam.mlockets.manager;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.FactionRenameEvent;
import com.massivecraft.factions.struct.Rel;
import com.sk89q.worldguard.bukkit.event.block.UseBlockEvent;

import fr.reizam.mlockets.MLockets;
import fr.reizam.mlockets.utils.FactionUtils;
import fr.reizam.mlockets.utils.VaultUtils;

public class MLocketsListeners implements Listener {

	@EventHandler
	public void onInteract(UseBlockEvent e) {
		for (Block b : e.getBlocks()) {
			if (b.getType().toString().toLowerCase().contains("door")) {
				for (Locket locket : MLockets.getManager().getLockets()) {
					if (locket.getCuboid().getBlocks().contains(b)	&& (locket.getFaction().equalsIgnoreCase(FPlayers.i.get(e.getCause().getFirstPlayer()).getFaction().getTag()))) {
						e.setResult(org.bukkit.event.Event.Result.ALLOW);
					}
				}

			}

		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onClick(InventoryClickEvent e) {
		if (e.getClickedInventory() == null || e.getCurrentItem() == null)
			return;
		if (e.getClickedInventory().getTitle().equalsIgnoreCase("§eEnchères > Lockets")) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			for (Locket locket : MLockets.getManager().getLockets()) {
				if (locket.toItemStack().equals(e.getCurrentItem())) {
					if (locket.isBid()) {
						if(FPlayers.i.get(p).getRole().equals(Rel.LEADER) || FPlayers.i.get(p).getRole().equals(Rel.OFFICER)){
							p.closeInventory();
							p.sendMessage("§e• Veuillez entrer dans le chat le montant que vous voulez enchérir !");
							p.sendMessage("§7§o• Entrez 'cancel' pour annuler l'enchère !");
							MLockets.getManager().getEncs().put(p, locket);
						} else {
							p.sendMessage("§cVous devez être chef ou modérateur de votre faction !");
							p.closeInventory();
						}
					} else {
						p.sendMessage("§cLes enchères sont fermées pour le locket " + locket.getName() + " !");
						p.closeInventory();
					}
				}
			}
		}
	}

	@EventHandler
	public void onDisband(FactionDisbandEvent e) {
		for(Locket locket : MLockets.getManager().getLockets()) {
			if(locket.getFaction().equalsIgnoreCase(e.getFaction().getTag())) {
				locket.setFaction("");
			}
		}
	}
	
	@EventHandler
	public void onRename(FactionRenameEvent e) {
		for(Locket locket : MLockets.getManager().getLockets()) {
			if(locket.getFaction().equalsIgnoreCase(e.getOldFactionTag())) {
				locket.setFaction(e.getFactionTag());
			}
		}
	}
	
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (MLockets.getManager().getEncs().containsKey(p)) {
			e.setCancelled(true);
			if (ChatColor.stripColor(e.getMessage().replace(".", "")).equalsIgnoreCase("cancel")) {
				MLockets.getManager().getEncs().remove(p);
				p.sendMessage("§e• Vous avez annulée votre enchère !");
				return;
			} else {
				try {
					int number = 0;
					number = Integer.parseInt(ChatColor.stripColor(e.getMessage().replace(".", "")));
					if (VaultUtils.getEconomy().has(FactionUtils.getFaction(FPlayers.i.get(p)), number)) {
						if (number > MLockets.getManager().getEncs().get(p).getDernierEncheres()) {
							VaultUtils.getEconomy().withdrawPlayer(FactionUtils.getFaction(FPlayers.i.get(p)), number);
							if (!MLockets.getManager().getEncs().get(p).getFaction().equals("")) {							
								VaultUtils.getEconomy().depositPlayer(Factions.i.get(MLockets.getManager().getEncs().get(p).getFaction()).getAccountId(), MLockets.getManager().getEncs().get(p).getDernierEncheres());
							}
							MLockets.getManager().getEncs().get(p).setDernierEncheres(number);
							p.sendMessage("§e• Vous venez d'enchérir §c" + VaultUtils.getEconomy().format(number)
									+ "§e au dessus de la §c"
									+ (MLockets.getManager().getEncs().get(p).getFaction().equals("")
											? "dernière enchère"
											: MLockets.getManager().getEncs().get(p).getFaction())
									+ " §e!");
							MLockets.getManager().getEncs().get(p).setFaction(FPlayers.i.get(p).getFaction().getTag());
							MLockets.getManager().getEncheres().updateLockets();
							MLockets.getManager().getEncs().remove(p);
							MLockets.getManager().getEncheres().open(p);

						} else {
							p.sendMessage("§cVotre enchère n'a pas assez elevé !");
						}
					} else {
						p.sendMessage("§cVotre faction n'a pas assez d'argent !");
					}
				} catch (NumberFormatException ee) {
					p.sendMessage("§cNombre invalide !");
					return;
				}
			}

		}
	}

}
