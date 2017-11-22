package fr.reizam.mlockets.utils;

import com.massivecraft.factions.FPlayer;

public class FactionUtils {

	public static String getFaction(FPlayer fp) {
		return "faction-"+ fp.getFactionId();
	}
	
}
