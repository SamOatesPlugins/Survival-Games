package org.mcsg.survivalgames.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mcsg.survivalgames.Game;
import org.mcsg.survivalgames.GameManager;
import org.mcsg.survivalgames.Game.GameMode;

public class MoveEvent implements Listener{

	/**
	 * Stop the player from moving at the start of a game
	 * @param event The player move event
	 */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void frozenSpawnHandler(PlayerMoveEvent event) {
    	
    	final Player player = event.getPlayer();
    	GameManager manager = GameManager.getInstance();
    	final GameMode gameMode = manager.getGameMode(manager.getPlayerGameId(player));
    	
        if (manager.isPlayerActive(player) && gameMode != Game.GameMode.INGAME) {
        	final Location from = event.getFrom();
        	final Location to = event.getTo();
        	final int xDiff = from.getBlockX() - to.getBlockX();
        	final int zDiff = from.getBlockZ() - to.getBlockZ();
            
            if (xDiff + zDiff != 0) {
            	event.setCancelled(true);
            }
        }
    }
}
