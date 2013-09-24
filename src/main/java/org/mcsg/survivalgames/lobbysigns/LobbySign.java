package org.mcsg.survivalgames.lobbysigns;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.mcsg.survivalgames.Game;
import org.mcsg.survivalgames.GameManager;

public abstract class LobbySign {
	
	protected Location location = null;
	protected LobbySignType type = LobbySignType.Unknown;
	protected int gameId = -1;
	
	public LobbySign(Location location, int gameId, LobbySignType type) {
		this.location = location;
		this.type = type;
		this.gameId = gameId;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public Sign getSign() {
		return (Sign)location.getBlock().getState();
	}
	
	public LobbySignType getType() {
		return type;
	}
	
	public Game getGame() {
		return GameManager.getInstance().getGame(gameId);
	}
	
	public abstract void execute(Player player);
	
	public abstract void update();

}
