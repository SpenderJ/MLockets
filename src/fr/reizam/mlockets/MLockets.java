package fr.reizam.mlockets;

import java.lang.reflect.Modifier;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import com.google.common.collect.Lists;

import fr.reizam.mlockets.cmd.MLocketsCmd;
import fr.reizam.mlockets.manager.MLocketsListeners;
import fr.reizam.mlockets.manager.MLocketsManager;
import fr.reizam.mlockets.utils.cmd.CommandFramework;
import fr.reizam.mlockets.utils.json.EnumTypeAdapter;
import fr.reizam.mlockets.utils.json.ItemStackAdapter;
import fr.reizam.mlockets.utils.json.JsonPersist;
import fr.reizam.mlockets.utils.json.LocationAdapter;
import fr.reizam.mlockets.utils.json.PotionEffectAdapter;
import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.GsonBuilder;

public class MLockets extends JavaPlugin{
	
	private CommandFramework cmdframework;
	private Gson gson;
	private List<JsonPersist> persistances = Lists.newArrayList();
	
	static MLockets instance;
	static MLocketsManager manager;
	
	public static MLockets getInstance() {
		return instance;
	}
	
	public static MLocketsManager getManager() {
		return manager;
	}
	
	public void onEnable() {
		instance = this;
		manager = new MLocketsManager(instance);

		getDataFolder().mkdir();
		
		this.gson = this.getGsonBuilder().create();
		
		this.persistances.add(manager);
		this.persistances.forEach(p -> p.loadData());
		manager.init();

		this.cmdframework = new CommandFramework(this);
		this.cmdframework.registerCommands(new MLocketsCmd());
		
		
		Bukkit.getPluginManager().registerEvents(new MLocketsListeners(), this);
	}
	
	public Gson getGson() {
		return gson;
	}
	
	public CommandFramework getCmdFramework() {
		return cmdframework;
	}
	
	public void onDisable() {
		this.persistances.forEach(p -> p.saveData(true));
	}
	
	private GsonBuilder getGsonBuilder() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE).registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY).registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter()).registerTypeAdapter(PotionEffect.class, new PotionEffectAdapter()).registerTypeAdapter(Location.class, new LocationAdapter());
    }

}
