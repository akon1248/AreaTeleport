package com.akon.areateleport;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class TeleportAreaEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList HANDLER_LIST = new HandlerList();

	private boolean cancelled;
	private final TeleportArea teleportArea;
	private final Location from;
	private Location to;

	public TeleportAreaEvent(@NotNull Player who, TeleportArea teleportArea, Location from, Location to) {
		super(who);
		this.teleportArea = teleportArea;
		this.from = from;
		this.to = to;
	}


	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
