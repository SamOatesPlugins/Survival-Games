package org.mcsg.survivalgames.lobbysigns;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class LobbySignPlayerList extends LobbySign {

	public LobbySignPlayerList(Sign sign, int gameId) {
		super(sign.getLocation(), gameId, LobbySignType.PlayerList);
		
		sign.setLine(0, "");
		sign.setLine(1, "");
		sign.setLine(2, "");
		sign.setLine(3, "");
	}

	@Override
	public void execute(Player player) {

	}

	@Override
	public void update() {

	}

}
