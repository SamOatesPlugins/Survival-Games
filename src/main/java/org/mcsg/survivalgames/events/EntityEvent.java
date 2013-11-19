package org.mcsg.survivalgames.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.mcsg.survivalgames.Game;
import org.mcsg.survivalgames.GameManager;

public class EntityEvent implements Listener {
	
	/**
	 * Stop mobs spawning unless we are in game
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMobSpawn(CreatureSpawnEvent event) {
		
		GameManager gameManager = GameManager.getInstance();
		int gameID = gameManager.getBlockGameId(event.getLocation());
		if (gameID == -1)
			return;
		
		if (gameManager.getGameMode(gameID) != Game.GameMode.INGAME) {
			event.setCancelled(true);
		}
		
	}
	
	

}
