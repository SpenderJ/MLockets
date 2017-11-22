package fr.reizam.mlockets.utils.json;

import java.io.File;

import fr.reizam.mlockets.MLockets;
import net.minecraft.util.com.google.gson.Gson;

public interface JsonPersist {
	
	public Gson gson = (MLockets.getInstance().getGson());
	
	public File getFile();
	
	public void loadData();
	
	public void saveData(boolean sync);
	

}
