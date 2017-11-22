package fr.reizam.mlockets.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import fr.reizam.mlockets.MLockets;
import fr.reizam.mlockets.gui.GuiEncheres;
import fr.reizam.mlockets.utils.json.DiscUtil;
import fr.reizam.mlockets.utils.json.JsonPersist;
import net.minecraft.util.com.google.common.reflect.TypeToken;

public class MLocketsManager implements JsonPersist,Runnable{

	private ArrayList<Locket> lockets = new ArrayList<>(); 
	private HashMap<Player, Locket> encs = new HashMap<>();
	
	private int lastHours = 0;
	private Date d = new Date();
	
	private GuiEncheres encheres;
	
	private BukkitTask task;
	
	private MLockets instance;
	
	public MLocketsManager(MLockets instance) {
		this.instance = instance;
	}
	
	public HashMap<Player, Locket> getEncs() {
		return encs;
	}

	public void init() {
		this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(instance, this, 20L, 20L);
		
		this.encheres = new GuiEncheres();
	}
	
	public BukkitTask getTask() {
		return task;
	}
	
	public GuiEncheres getEncheres() {
		return encheres;
	}
	
	public MLockets getInstance() {
		return instance;
	}

	public ArrayList<Locket> getLockets() {
		return lockets;
	}
	
	@Override
	public File getFile() {
		return new File(MLockets.getInstance().getDataFolder(), "lockets.json");
	}

	@Override
	public void loadData() {
		String content = DiscUtil.readCatch(getFile());
		if (content == null) return;
		
		@SuppressWarnings("serial")
		ArrayList<Locket> map =  JsonPersist.gson.fromJson(content, new TypeToken<ArrayList<Locket>>(){}.getType());
		
		lockets.clear();
		lockets.addAll(map);
	}

	@Override
	public void saveData(boolean sync) {
		DiscUtil.writeCatch(getFile(), JsonPersist.gson.toJson(lockets), sync);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		if(d.getDay() == 1) {
			if(d.getHours() > 6 && d.getHours() < 21) {
				if(lastHours != d.getHours()) {
					Bukkit.broadcastMessage("§e§lMLockets §e: Les enchères sont §couverte§e, faites /mlockets encheres pour enchérir !");
				}
			}
			if(d.getHours() == 21 && d.getSeconds() == 0) {
				Bukkit.broadcastMessage("§e§lMLockets §e: Les enchères sont désormais fini !");
				Bukkit.broadcastMessage(" ");
				for(Locket locket : MLockets.getManager().getLockets()) {
					locket.setBid(false);
					Bukkit.broadcastMessage("§f> §eLa faction §c"+locket.getFaction()+" §e remporte le locket §c"+locket.getName()+" §e!");
					Bukkit.broadcastMessage(" ");
				}
			}
			if(d.getHours() == 6 && d.getSeconds() == 0) {
				for(Locket locket : MLockets.getManager().getLockets()) {
					locket.setBid(true);
					locket.setDernierEncheres(locket.getPrixInitial());
					locket.setFaction("");
				}
				Bukkit.broadcastMessage("§e§lMLockets §e: Les enchères pour les lockets sont ouvertes !");
			}
			getEncheres().updateLockets();
		} else if(d.getDay() == 0 && d.getHours() == 23 && d.getMinutes() == 30 && d.getSeconds() == 0) {
			for(Locket locket : MLockets.getManager().getLockets()) {
				locket.setFaction("");
			}
		}
		this.lastHours = new Date().getHours();
	}

	
}
