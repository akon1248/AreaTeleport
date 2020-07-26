package com.akon.areateleport.command;

import com.akon.areateleport.AreaTeleport;
import com.akon.areateleport.MessageUtil;
import com.akon.areateleport.TeleportArea;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

@Cmd(value = "update", aliases = {"resize", "change"}, params = {"<name>"}, description = "テレポートエリアの範囲を変更します")
public class CommandUpdate extends CommandAbstract {

	@Override
	public boolean execute(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1) {
			return false;
		}
		if (sender instanceof Player) {
			TeleportArea teleportArea;
			if ((teleportArea = AreaTeleport.getTeleportAreaManager().getTeleportArea(args[0])) != null) {
				try {
					Region selection = AreaTeleport.getWEInstance().getSession((Player)sender).getSelection(new BukkitWorld(((Player)sender).getWorld()));
					BlockVector3 min = selection.getMinimumPoint();
					BlockVector3 max = selection.getMaximumPoint();
					BoundingBox boundingBox = new BoundingBox(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1);
					if (!boundingBox.overlaps(((Player)sender).getBoundingBox())) {
						Bukkit.getScheduler().runTaskAsynchronously(AreaTeleport.getInstance(), () -> {
							TeleportArea overlappedArea = null;
							for (TeleportArea ta: AreaTeleport.getTeleportAreaManager().getTeleportAreas(((Player)sender).getWorld())) {
								if (!ta.getName().equalsIgnoreCase(args[0]) && ta.getArea().overlaps(boundingBox)) {
									overlappedArea = ta;
									break;
								}
							}
							if (overlappedArea == null) {
								Location oldTP = teleportArea.getTpLocation();
								BoundingBox oldBB = teleportArea.getArea();
								teleportArea.setTpLocation(((Player)sender).getLocation());
								teleportArea.setArea(boundingBox);
								DecimalFormat format = new DecimalFormat("##.##");
								MessageUtil.sendMessage(sender, "&6テレポートエリアが変更されました");
								MessageUtil.sendMessage(sender, "&b変更前: &d({0}, {1}, {2}) -> ({3}, {4}, {5}) &5=>&d ({6}, {7}, {8}) in {9}", format.format(oldBB.getMinX()), format.format(oldBB.getMinY()), format.format(oldBB.getMinZ()), format.format(oldBB.getMaxX()), format.format(oldBB.getMaxY()), format.format(oldBB.getMaxZ()), format.format(oldTP.getX()), format.format(oldTP.getY()), format.format(oldTP.getZ()), oldTP.getWorld().getName());
								MessageUtil.sendMessage(sender, "&b変更後: &d({0}, {1}, {2}) -> ({3}, {4}, {5}) &5=>&d ({6}, {7}, {8}) in {9}", format.format(teleportArea.getArea().getMinX()), format.format(teleportArea.getArea().getMinY()), format.format(teleportArea.getArea().getMinZ()), format.format(teleportArea.getArea().getMaxX()), format.format(teleportArea.getArea().getMaxY()), format.format(teleportArea.getArea().getMaxZ()), format.format(teleportArea.getTpLocation().getX()), format.format(teleportArea.getTpLocation().getY()), format.format(teleportArea.getTpLocation().getZ()), teleportArea.getWorld().getName());
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
				} catch (IncompleteRegionException ignored) {

				}
			} else {
				MessageUtil.sendMessage(sender, "&c{0}という名前のテレポートエリアは登録されていません", args[0].toLowerCase());
			}
		} else {
			MessageUtil.sendMessage(sender, "&cこのコマンドはプレイヤーからのみ実行可能です");
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			return this.nameComplete(args[0]);
		}
		return null;
	}

}
