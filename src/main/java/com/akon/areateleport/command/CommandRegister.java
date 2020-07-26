package com.akon.areateleport.command;

import com.akon.areateleport.AreaTeleport;
import com.akon.areateleport.MessageUtil;
import com.akon.areateleport.TeleportArea;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.io.IOException;

@Cmd(value = "register", aliases = {"add"}, params = {"<name>"}, description = "テレポートエリアを追加します")
public class CommandRegister extends CommandAbstract {

	@Override
	public boolean execute(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1) {
			return false;
		}
		if (sender instanceof Player) {
			if (AreaTeleport.getTeleportAreaManager().getTeleportArea(args[0]) == null) {
				try {
					Region selection = AreaTeleport.getWEInstance().getSession((Player) sender).getSelection(new BukkitWorld(((Player) sender).getWorld()));
					BlockVector3 min = selection.getMinimumPoint();
					BlockVector3 max = selection.getMaximumPoint();
					BoundingBox boundingBox = new BoundingBox(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1);
					if (!boundingBox.overlaps(((Player)sender).getBoundingBox())) {
						Bukkit.getScheduler().runTaskAsynchronously(AreaTeleport.getInstance(), () -> {
							TeleportArea overlappedArea = null;
							for (TeleportArea teleportArea: AreaTeleport.getTeleportAreaManager().getTeleportAreas(((Player)sender).getWorld())) {
								if (teleportArea.getArea().overlaps(boundingBox)) {
									overlappedArea = teleportArea;
									break;
								}
							}
							if (overlappedArea == null) {
								AreaTeleport.getTeleportAreaManager().registerTeleportArea(new TeleportArea(args[0], ((Player)sender).getLocation(), boundingBox));
								MessageUtil.sendMessage(sender, "&6テレポートエリアを登録しました: {0}", args[0].toLowerCase());
								try {
									AreaTeleport.getTeleportAreaManager().updateYaml(args[0]);
								} catch (IOException ex) {
									MessageUtil.sendMessage(sender, "&cテレポートエリアの保存に失敗しました");
									ex.printStackTrace();
								}
							} else {
								MessageUtil.sendMessage(sender, "&c指定された範囲が他のテレポートエリアと重なっています: {0}", overlappedArea.getName());
							}
						});
					} else {
						MessageUtil.sendMessage(sender, "&c選択範囲の外に出てください");
					}
				} catch (IncompleteRegionException e) {
					MessageUtil.sendMessage(sender, "c範囲を選択してください");
				}
			} else {
				MessageUtil.sendMessage(sender, "&c{0}という名前のテレポートエリアは既に登録されています", args[0].toLowerCase());
			}
		} else {
			MessageUtil.sendMessage(sender, "&cこのコマンドはプレイヤーからのみ実行可能です");
		}
		return true;
	}

}
