package org.mcsg.survivalgames.lobbysigns;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class LobbySignManager {
	
	private HashMap<Vector, LobbySign> signs = new HashMap<Vector, LobbySign>();
	
	public LobbySignManager() {

	}

	public void addSign(LobbySign newLobbySign) {
		Vector location = newLobbySign.getLocation().toVector();
		signs.put(location, newLobbySign);
		saveSigns();
	}
	
	public void removeSign(LobbySign lobbySign) {
		Vector location = lobbySign.getLocation().toVector();
		if (signs.containsKey(location)) {		
			signs.remove(location);
			saveSigns();
		}
	}

	public LobbySign getSign(Location location) {
		return signs.get(location.toVector());
	}
	
	private void saveSigns() {
		
	}
	
	public void loadSigns() {
		signs.clear();
	}
	
	public void updateSigns() {
		for (LobbySign sign : signs.values()) {
			sign.update();
		}
	}
	
	public void updateSigns(int gameId) {
		for (LobbySign sign : signs.values()) {
			if (sign.getGame().getID() == gameId) {
				sign.update();
			}
		}
	}

	public void removeArena(int arena) {
		for (LobbySign sign : signs.values()) {
			if (sign.getGame().getID() == arena) {
				removeSign(sign);
				sign.getLocation().getBlock().breakNaturally();
			}
		}
	}

}
