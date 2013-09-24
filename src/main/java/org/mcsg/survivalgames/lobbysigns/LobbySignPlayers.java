package org.mcsg.survivalgames.lobbysigns;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.mcsg.survivalgames.SettingsManager;

public class LobbySignPlayers extends LobbySign {

	public LobbySignPlayers(Sign sign, int gameId) {
		super(sign.getLocation(), gameId, LobbySignType.Players);
		
		sign.setLine(1, "Active Players");
		sign.setLine(2, getGame().getActivePlayers() + "/" + SettingsManager.getInstance().getSpawnCount(gameId));
		
	}

	public LobbySignPlayers(int gameId) {
		super(gameId, LobbySignType.Players);
	}

	@Override
	public void execute(Player player) {

	}

	@Override
	public void update() {
		Sign sign = getSign();		
		sign.setLine(2, getGame().getActivePlayers() + "/" + SettingsManager.getInstance().getSpawnCount(gameId));
		sign.update();
	}

}
