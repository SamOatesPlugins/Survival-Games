package org.mcsg.survivalgames;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class GameScoreboard {
	
	private final int gameID;
	private final Scoreboard scoreboard;
	private Objective objective = null;
	private Team livingTeam = null;
	
	private HashMap<String, Scoreboard> originalScoreboard = new HashMap<String, Scoreboard>();
	private ArrayList<String> activePlayers = new ArrayList<String>();
	
	/**
	 * Class constructor
	 * 
	 * @param gameID	The game id this scoreboard is used within
	 */
	public GameScoreboard(int gameID) {
		
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		
		this.gameID = gameID;
		this.scoreboard = manager.getNewScoreboard();
			
		reset();
	}
	
	/**
	 * Reset the scoreboard back to its original empty state
	 */
	public void reset() {
		
		// Remove any players still on the scoreboard
		if (!this.activePlayers.isEmpty()) {
			for (String playerName : activePlayers) {
				Player player = Bukkit.getPlayer(playerName);
				if (player != null) {
					removePlayer(player);
				}
			}
		}
		
		// Unregister the objective
		if (this.objective != null) {
			this.objective.unregister();
			this.objective = null;
		}
		
		// Reset the living team
		if (this.livingTeam != null) {
			this.livingTeam.unregister();
			this.livingTeam = null;
		}
		
		// Create the objective
		this.objective = this.scoreboard.registerNewObjective("survivalGames-" + this.gameID, "dummy");
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.objective.setDisplayName("SG Arena (0/24)");
		
		// Create the living team
		this.livingTeam = this.scoreboard.registerNewTeam("Living");
		this.livingTeam.setAllowFriendlyFire(true);
		this.livingTeam.setCanSeeFriendlyInvisibles(false);
		
	}
	
	/**
	 * Add a player to the scoreboard
	 * 
	 * @param player	The player to add to the scoreboard
	 */
	public void addPlayer(final Player player) {
		
		// Store the current scoreboard for the player
		Scoreboard original = player.getScoreboard();
		if (original != null) {
			this.originalScoreboard.put(player.getName(), original);
		}
		
		this.activePlayers.add(player.getName());
		
		// Set the players scoreboard and andd them too the team
		player.setScoreboard(this.scoreboard);
		this.livingTeam.addPlayer(player);
		
		// Set the players score to zero, then increase it
		Score score = this.objective.getScore(player);
		score.setScore(1);
		
		final Objective gameObjective = this.objective;
		final int noofPlayers = this.activePlayers.size();
		Bukkit.getScheduler().runTaskLater(GameManager.getInstance().getPlugin(), new Runnable() {
            public void run() {
            	gameObjective.getScore(player).setScore(0);
            	gameObjective.setDisplayName(GameManager.getInstance().getGame(gameID).getName() + " (" + noofPlayers + "/24)");
            }
        }, 1L);
		
	}
	
	/**
	 * Remove a player from the scoreboard
	 * 
	 * @param player	The player to remove from the scoreboard
	 */
	public void removePlayer(Player player) {
		
		// remove the player from the team
		this.livingTeam.removePlayer(player);
		this.scoreboard.resetScores(player);
		
		// Restore the players scoreboard
		Scoreboard original = this.originalScoreboard.get(player.getName());
		if (original != null) {
			player.setScoreboard(original);
			this.originalScoreboard.remove(player.getName());
		}
		this.activePlayers.remove(player.getName());
				
		// Update the objective title
		final Objective gameObjective = this.objective;
		final int noofPlayers = this.activePlayers.size();
		Bukkit.getScheduler().runTaskLater(GameManager.getInstance().getPlugin(), new Runnable() {
            public void run() {
            	gameObjective.setDisplayName(GameManager.getInstance().getGame(gameID).getName() + " (" + noofPlayers + "/24)");
            }
        }, 1L);
		
	}

}
