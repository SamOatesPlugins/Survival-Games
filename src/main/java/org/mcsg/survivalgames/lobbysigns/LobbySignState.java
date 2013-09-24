package org.mcsg.survivalgames.lobbysigns;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class LobbySignState extends LobbySign {

	public LobbySignState(Sign sign, int gameId) {
		super(sign.getLocation(), gameId, LobbySignType.State);
		
		sign.setLine(1, "Arena State");
		sign.setLine(2, getGame().getGameMode().toString());
	}

	@Override
	public void execute(Player player) {
		// output some help about the state and what it means?
	}

	@Override
	public void update() {
		Sign sign = getSign();		
		sign.setLine(2, getGame().getGameMode().toString());
		sign.update();
	}
	
}
