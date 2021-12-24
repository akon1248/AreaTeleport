package com.akon.areateleport;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.BoundingBox;

public class TeleportListener implements Listener {

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		AreaTeleport plugin = AreaTeleport.getInstance();
		Player player = e.getPlayer();
		Location from = e.getFrom();
		Location to = e.getTo();
		if (player.getGameMode() != GameMode.SPECTATOR && !e.isCancelled() && to != null && e.getFrom().getWorld() == to.getWorld()) {
			BoundingBox boundingBox = e.getPlayer().getBoundingBox().shift(to.getX()-from.getX(), to.getY()-from.getY(), to.getZ()-from.getZ());
			Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
				TeleportArea tpArea = null;
				for (TeleportArea teleportArea : AreaTeleport.getTeleportAreaManager().getTeleportAreas(player.getWorld().getChunkAt(player.getLocation()))) {
					if (teleportArea.getArea().overlaps(boundingBox)) {
						tpArea = teleportArea;
						break;
					}
				}
				if (tpArea != null) {
					TeleportArea finalTPArea = tpArea;
					Bukkit.getScheduler().runTask(plugin, () -> {
						boolean flag = player.getMetadata("Teleported").stream()
							.filter(value -> value.getOwningPlugin() == plugin)
							.findAny()
							.map(MetadataValue::asBoolean)
							.orElse(true);
						if (flag) {
							player.setMetadata("Teleported", new FixedMetadataValue(plugin, true));
							TeleportAreaEvent event = new TeleportAreaEvent(player, finalTPArea, to, finalTPArea.getTpLocation());
							Bukkit.getPluginManager().callEvent(event);
							if (!event.isCancelled()) {
								teleportEffect(to);
								player.setFallDistance(0);
								player.eject();
								player.teleport(event.getTo());
								teleportEffect(event.getTo());
								Bukkit.getScheduler().runTaskLater(plugin, () -> player.removeMetadata("Teleported", plugin), 1);
							}
						}
					});
				}
			});
		}
	}

	private static void teleportEffect(Location loc) {
		loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0);
		loc.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 1);
	}

}
