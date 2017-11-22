package fr.reizam.mlockets.cmd;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Rel;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import fr.reizam.mlockets.MLockets;
import fr.reizam.mlockets.manager.Locket;
import fr.reizam.mlockets.utils.cmd.Args;
import fr.reizam.mlockets.utils.cmd.Command;

public class MLocketsCmd {

	@Command(name = { "mlockets" })
	public void onCommand(Args info) {
		if (info.isPlayer()) {
			Player p = info.getPlayer();
			String[] args = info.getArgs();
			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("create") && p.isOp()) {
					String name = args[1];
					String price = args[2];
					WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager()
							.getPlugin("WorldEdit");
					Selection selection = worldEdit.getSelection(info.getPlayer());
					if (selection == null) {
						p.sendMessage("§cVeuillez faire une selection WorldEdit !");
						return;
					}
					Location one = selection.getMaximumPoint();
					Location two = selection.getMinimumPoint();
					for (Locket locket : MLockets.getManager().getLockets()) {
						if (locket.getName().equalsIgnoreCase(name)) {
							p.sendMessage("§cUn locket s'apelle déjà '" + name + "' !");
							return;
						}
					}
					p.sendMessage("§e• Vous venez de créer le locket §c'" + name + "' §e!");
					try {
						MLockets.getManager().getLockets().add(new Locket(name, one, two, Integer.parseInt(price)));
						MLockets.getManager().getEncheres().updateLockets();
					} catch (NumberFormatException e) {
						p.sendMessage("§cLe format du nombre est incorrect !");
						return;
					}

				} else {
					this.sendHelp(info.getSender());
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("remove") && p.isOp()) {
					String name = args[1];
					for (Locket locket : MLockets.getManager().getLockets()) {
						if (locket.getName().equalsIgnoreCase(name)) {
							p.sendMessage("§e• Vous avez bien suprimée le locket §c'" + name + "' !");
							MLockets.getManager().getLockets().remove(locket);
							MLockets.getManager().getEncheres().updateLockets();
							return;
						}
					}
					p.sendMessage("§cLe locket '" + name + "' est inconnu !");
				} else if (args[0].equalsIgnoreCase("kickfaction") && p.isOp()) {
					String idstr = args[1];
					try {
						int id = Integer.parseInt(idstr);
						for (Locket locket : MLockets.getManager().getLockets()) {
							if (MLockets.getManager().getLockets().indexOf(locket) == id) {
								p.sendMessage("§e• Vous venez de kick la faction §c'" + locket.getFaction()
										+ "' §edu locket §c'" + locket.getName() + "' §e!");
								locket.setFaction("");
								MLockets.getManager().getEncheres().updateLockets();
								return;
							}
						}
						p.sendMessage("§c ID inconnu /mlockets list pour en savoir plus !");
					} catch (NumberFormatException e) {
						p.sendMessage("§cLe format du nombre est incorrect !");
						return;
					}
				} else if (args[0].equalsIgnoreCase("stopbid") && p.isOp()) {
					String idstr = args[1];
					try {
						int id = Integer.parseInt(idstr);
						for (Locket locket : MLockets.getManager().getLockets()) {
							if (MLockets.getManager().getLockets().indexOf(locket) == id) {
								p.sendMessage("§e• Vous venez d'arrêter l'enchére du locket §c'" + locket.getName()
										+ "' §e!");
								locket.setBid(false);
								return;
							}
						}
						p.sendMessage("§c ID inconnu /mlockets list pour en savoir plus !");
					} catch (NumberFormatException e) {
						p.sendMessage("§cLe format du nombre est incorrect !");
						return;
					}
				} else if (args[0].equalsIgnoreCase("bid") && p.isOp()) {
					String idstr = args[1];
					try {
						int id = Integer.parseInt(idstr);
						for (Locket locket : MLockets.getManager().getLockets()) {
							if (MLockets.getManager().getLockets().indexOf(locket) == id) {
								p.sendMessage(
										"§e• Vous venez d'ouvrir l'enchére du locket §c'" + locket.getName() + "' §e!");
								locket.setBid(true);
								return;
							}
						}
						p.sendMessage("§c ID inconnu /mlockets list pour en savoir plus !");
					} catch (NumberFormatException e) {
						p.sendMessage("§cLe format du nombre est incorrect !");
						return;
					}
				} else {
					this.sendHelp(info.getSender());
				}
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) {
					p.sendMessage("§e§m----->§r §cListe des lockets §e§m<-----");
					p.sendMessage(" ");
					for (Locket locket : MLockets.getManager().getLockets()) {
						p.sendMessage("§e► §l" + locket.getName() + " §e◄");
						p.sendMessage("§e> ID : §c" + MLockets.getManager().getLockets().indexOf(locket));
						p.sendMessage("§e> Faction : §c" + locket.getFaction());
						p.sendMessage(" ");
					}
					p.sendMessage("§e§m--------------------------");
				} else if (args[0].equalsIgnoreCase("encheres")) {
					if (FPlayers.i.get(p).getRole().equals(Rel.LEADER)
							|| FPlayers.i.get(p).getRole().equals(Rel.OFFICER)) {
						MLockets.getManager().getEncheres().open(p);
						MLockets.getManager().getEncheres().updateLockets();
					} else {
						p.sendMessage(
								"§cVous devez être modérateur ou chef de votre faction pour accéder aux enchères !");
					}
				}
			} else {
				this.sendHelp(info.getSender());
			}
		} else {
			info.getSender().sendMessage("Vous devez être un joueur pour effectuer cette commande !");
		}
	}

	private void sendHelp(CommandSender sender) {
		sender.sendMessage("§e§m--------------------");
		sender.sendMessage(" ");
		sender.sendMessage("§e• /mlockets create <nom> <prixInitial>");
		sender.sendMessage("§e• /mlockets remove <nom>");
		sender.sendMessage("§e• /mlockets encheres");
		sender.sendMessage("§e• /mlockets list");
		sender.sendMessage("§e• /mlockets bid <id>");
		sender.sendMessage("§e• /mlockets stopbid <id>");
		sender.sendMessage("§e• /mlockets kickfaction <id>");
		sender.sendMessage(" ");
		sender.sendMessage("§e§m--------------------");
	}

}
