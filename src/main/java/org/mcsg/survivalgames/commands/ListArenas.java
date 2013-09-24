package org.mcsg.survivalgames.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.mcsg.survivalgames.Game;
import org.mcsg.survivalgames.GameManager;
import org.mcsg.survivalgames.Game.GameMode;
import org.mcsg.survivalgames.SettingsManager;

public class ListArenas implements SubCommand{
	
    public boolean onCommand(Player player, String[] args) {
    	
    	GameManager gameManager = GameManager.getInstance();
    	
    	// list all arenas
		ArrayList<Game> games = gameManager.getGames();
		if (games.isEmpty()) {
    		player.sendMessage(SettingsManager.getInstance().getMessageConfig().getString("messages.words.noarenas", "No arenas exist"));
        	return false;
    	}
		
		for (Game game : games) {
			player.sendMessage(GetColorPrefix(game.getGameMode()) + game.getName() + " - " + game.getGameMode() + " - Players (" + game.getActivePlayers() + "/" + SettingsManager.getInstance().getSpawnCount(game.getID()) + ")");
		}
    	
        return false;
    }
    
    private ChatColor GetColorPrefix(GameMode gameMode) {
		
		if (gameMode == GameMode.DISABLED) return ChatColor.RED;
		if (gameMode == GameMode.ERROR) return ChatColor.DARK_RED;
		if (gameMode == GameMode.FINISHING) return ChatColor.DARK_PURPLE;
		if (gameMode == GameMode.WAITING) return ChatColor.GOLD;
		if (gameMode == GameMode.INGAME) return ChatColor.DARK_GREEN;
		if (gameMode == GameMode.STARTING) return ChatColor.GREEN;
		if (gameMode == GameMode.RESETING) return ChatColor.DARK_AQUA;
		if (gameMode == GameMode.LOADING) return ChatColor.BLUE;
		if (gameMode == GameMode.INACTIVE) return ChatColor.DARK_GRAY;
		
		return ChatColor.WHITE;
	}
    
    @Override
    public String help(Player p) {
        return "/sg listarenas - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.listarenas", "List all available arenas");
    }

	@Override
	public String permission() {
		return "";
	}
}