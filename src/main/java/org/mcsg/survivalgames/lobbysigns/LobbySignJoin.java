package org.mcsg.survivalgames.lobbysigns;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.mcsg.survivalgames.GameManager;

public class LobbySignJoin extends LobbySign {

	public LobbySignJoin(Sign sign, int gameId) {
		super(sign.getLocation(), gameId, LobbySignType.Join);
		
		sign.setLine(1, ChatColor.BOLD + "Join Arena");
		sign.setLine(2, getGame().getName());
				
	}

	@Override
	public void execute(Player player) {
		GameManager.getInstance().addPlayer(player, this.gameId);
	}

	@Override
	public void update() {
		
	}

}
