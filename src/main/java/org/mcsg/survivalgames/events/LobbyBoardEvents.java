package org.mcsg.survivalgames.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.mcsg.survivalgames.GameManager;
import org.mcsg.survivalgames.SurvivalGames;
import org.mcsg.survivalgames.lobbysigns.LobbySign;
import org.mcsg.survivalgames.lobbysigns.LobbySignJoin;
import org.mcsg.survivalgames.lobbysigns.LobbySignPlayerList;
import org.mcsg.survivalgames.lobbysigns.LobbySignPlayers;
import org.mcsg.survivalgames.lobbysigns.LobbySignState;

public class LobbyBoardEvents implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
		
		// We only care about clicking blocks
		final Action action = event.getAction();
		if (!(action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK))
			return;
		
		Block block = event.getClickedBlock();
		
		// We only care about signs
		final Material blockType = block.getType();
		if (!(blockType == Material.SIGN || blockType == Material.SIGN_POST || blockType == Material.WALL_SIGN))
			return;
		
		// See if a lobby sign at the blocks location exists
		LobbySign sign = ((SurvivalGames)GameManager.getInstance().getPlugin()).getLobbySignManager().getSign(block.getLocation());
		if (sign == null)
			return;
		
		sign.execute(event.getPlayer());		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignChange(SignChangeEvent event) {
		final Player player = event.getPlayer();

		if (!player.hasPermission("sg.lobby.createsign")) {
			return;
		}		

		Sign sign = (Sign)event.getBlock().getState();
		String createLine = event.getLine(1);
		
		int gameId = -1;
		try {
			gameId = Integer.parseInt(event.getLine(2));
		} catch(Exception ex) {
			gameId = -1;
		}
		
		if (gameId == -1 || GameManager.getInstance().getGame(gameId) == null) {
			return;
		}

		LobbySign newLobbySign = null;
		
		// Join Sign
		if (createLine.equalsIgnoreCase("[sg-join]")) {
			newLobbySign = new LobbySignJoin(sign, gameId);
		}
		// State sign
		else if (createLine.equalsIgnoreCase("[sg-state]")) {
			newLobbySign = new LobbySignState(sign, gameId);
		}
		// Player sign
		else if (createLine.equalsIgnoreCase("[sg-players]")) {
			newLobbySign = new LobbySignPlayers(sign, gameId);
		}
		// Player list sign
		else if (createLine.equalsIgnoreCase("[sg-playerlist]")) {
			newLobbySign = new LobbySignPlayerList(sign, gameId);
		}
		
		if (newLobbySign == null) {
			return;
		}
		
		for (int line = 0; line < 4; ++line) {
			event.setLine(line, sign.getLine(line));
		}
		
		((SurvivalGames)GameManager.getInstance().getPlugin()).getLobbySignManager().addSign(newLobbySign);
		player.sendMessage("New " + newLobbySign.getType() + " for " + newLobbySign.getGame().getName() + " created.");
	}

}
