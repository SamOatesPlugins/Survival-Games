package org.mcsg.survivalgames;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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
    private Objective sidebarObjective = null;

    private final HashMap<String, Scoreboard> originalScoreboard = new HashMap<String, Scoreboard>();
    private final ArrayList<String> activePlayers = new ArrayList<String>();

    /**
     * Class constructor
     *
     * @param gameID	The game id this scoreboard is used within
     */
    public GameScoreboard(int gameID) {

        ScoreboardManager manager = Bukkit.getScoreboardManager();

        this.gameID = gameID;
        this.scoreboard = cloneScoreBoard(manager, manager.getMainScoreboard());

        reset();
    }

    /**
     * Reset the scoreboard back to its original empty state
     */
    public final void reset() {

        // Remove any players still on the scoreboard
        if (!this.activePlayers.isEmpty()) {
            ArrayList<String> players = new ArrayList<String>();
            for (String playerName : this.activePlayers) {
                players.add(playerName);
            }
            for (String playerName : players) {
                Player player = Bukkit.getPlayer(playerName);
                if (player != null) {
                    removePlayer(player);
                }
            }
        }

        // Unregister the objective
        if (this.sidebarObjective != null) {
            this.sidebarObjective.unregister();
            this.sidebarObjective = null;
        }

        // Create the objective
        this.sidebarObjective = this.scoreboard.registerNewObjective("survivalGames-" + this.gameID, "dummy");
        this.sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

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
        
        // Get the team the player belongs to on the main scoreboard
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        for (Team team : manager.getMainScoreboard().getTeams()) {
            Team sgTeam = this.scoreboard.getTeam(team.getName());
            if (sgTeam == null) {
                continue;   // Should be impossible, but better to be safe
            }
            for (OfflinePlayer teamPlayer : team.getPlayers()) {
                sgTeam.addPlayer(teamPlayer);
            }
        }

        // Set the players scoreboard and andd them too the team
        player.setScoreboard(this.scoreboard);

        // Set the players score to zero, then increase it
        final Score score = this.sidebarObjective.getScore(player);
        score.setScore(1);

        Bukkit.getScheduler().scheduleSyncDelayedTask(GameManager.getInstance().getPlugin(), new Runnable() {
            @Override
            public void run() {
                score.setScore(0);
            }
        }, 1L);

        updateSidebarTitle();
    }

    /**
     * Remove a player from the scoreboard
     *
     * @param player	The player to remove from the scoreboard
     */
    public void removePlayer(Player player) {

        // remove the player from the team
        this.scoreboard.resetScores(player);

        // Restore the players scoreboard
        Scoreboard original = this.originalScoreboard.get(player.getName());
        if (original != null) {
            player.setScoreboard(original);
            this.originalScoreboard.remove(player.getName());
        }
        this.activePlayers.remove(player.getName());

        updateSidebarTitle();
    }

    /**
     * Increase a given players score
     *
     * @param player The player whose score to increase.
     */
    public void increaseScore(Player player) {
        final Score score = this.sidebarObjective.getScore(player);
        final int currentScore = score.getScore();        
        score.setScore(currentScore + 1);        
    }

    /**
     * Update the title of the sidebar objective
     */
    private void updateSidebarTitle() {
        final int noofPlayers = this.activePlayers.size();
        final int maxPlayers = SettingsManager.getInstance().getSpawnCount(gameID);
        final String gameName = GameManager.getInstance().getGame(gameID).getName();

        this.sidebarObjective.setDisplayName(ChatColor.GOLD + gameName + " (" + noofPlayers + "/" + maxPlayers + ")");
    }

    /**
     * Clone a given scoreboard
     * @param manager The manager of the scoreboards
     * @param from The scoreboard to copy from
     * @return 
     */
    private Scoreboard cloneScoreBoard(ScoreboardManager manager, Scoreboard from) {
        
        Scoreboard to = manager.getNewScoreboard();
         
        // Copy teams and players
        for (Team team : from.getTeams()) {
            
            System.out.println("Copying Team: " + team.getName() + ", Prefix: " + team.getPrefix());
            
            Team newTeam = to.registerNewTeam(team.getName());
            newTeam.setDisplayName(team.getDisplayName());
            newTeam.setPrefix(team.getPrefix());
            newTeam.setSuffix(team.getSuffix());
            newTeam.setAllowFriendlyFire(team.allowFriendlyFire());
            newTeam.setCanSeeFriendlyInvisibles(team.canSeeFriendlyInvisibles());
            
            for (OfflinePlayer player : team.getPlayers()) {
                newTeam.addPlayer(player);
                System.out.println("Adding player: " + player.getName());
            }
        }
        
        // Copy objectives
        for (Objective objective : from.getObjectives()) {
            to.registerNewObjective(objective.getName(), objective.getCriteria());
            System.out.println("Copying objective: " + objective.getName() + " - " + objective.getCriteria());
        }
        
        return to;
    }

}
