package com.akon.areateleport;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.BoundingBox;

import java.util.function.Consumer;

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
				for (TeleportArea teleportArea : AreaTeleport.getTeleportAreaManager().getTeleportAreas(player.getWorld())) {
					if (teleportArea.getArea().overlaps(boundingBox)) {
						tpArea = teleportArea;
						break;
					}
				}
				if (tpArea != null) {
					TeleportArea finalTPArea = tpArea;
					Bukkit.getScheduler().runTask(plugin, () -> {
						boolean flag = true;
						for (MetadataValue value: player.getMetadata("Teleported")) {
							if (value.getOwningPlugin() == plugin) {
								flag = !value.asBoolean();
							}
						}
						if (flag) {
							player.setMetadata("Teleported", new FixedMetadataValue(plugin, true));
							TeleportAreaEvent event = new TeleportAreaEvent(player, finalTPArea, to, finalTPArea.getTpLocation());
							Bukkit.getPluginManager().callEvent(event);
							if (!event.isCancelled()) {
								//関数型プログラミングへの憧れ
								//クラスならメソッド内でも宣言できるんだからメソッドもそうしてくれればいいのに
								Consumer<Location> teleportEffect = (loc) -> {
									loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0);
									loc.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 1);
								};
								teleportEffect.accept(to);
								player.setFallDistance(0);
								player.eject();
								player.teleport(event.getTo());
								teleportEffect.accept(event.getTo());
								Bukkit.getScheduler().runTaskLater(plugin, () -> player.removeMetadata("Teleported", plugin), 1);
							}
						}
					});
				}
			});
		}
	}

}
