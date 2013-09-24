package org.mcsg.survivalgames;

import java.util.HashSet;

import org.bukkit.Chunk;
import org.mcsg.survivalgames.lobbysigns.LobbySignManager;

public class LobbyManager {

	private static LobbyManager instance = null;
	public static HashSet < Chunk > lobbychunks = new HashSet < Chunk > ();
	LobbySignManager signManager = null;
	
	private LobbyManager(LobbySignManager signManager) {
		this.signManager = signManager;
		signManager.loadSigns();
		updateAll();
	}
	
	public static void createInstance(LobbySignManager signManager) {
		instance = new LobbyManager(signManager);
	}

	public static LobbyManager getInstance() {
		return instance;
	}

	public void updateAll() {
		signManager.updateSigns();
	}

	public void updateWall(int a) {
		signManager.updateSigns(a);
	}

	public void removeSignsForArena(int arena) {
		signManager.removeArena(arena);
	}
}