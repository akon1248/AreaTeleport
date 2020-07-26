package com.akon.areateleport.command;

import com.akon.areateleport.AreaTeleport;
import com.akon.areateleport.MessageUtil;
import com.akon.areateleport.TeleportArea;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.BoundingBox;

import java.text.DecimalFormat;
import java.util.List;

@Cmd(value = "info", params = {"<name>"}, description = "テレポートエリアの詳細を表示します")
public class CommandInfo extends CommandAbstract {

	@Override
	public boolean execute(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1) {
			return false;
		}
		TeleportArea teleportArea;
		if ((teleportArea = AreaTeleport.getTeleportAreaManager().getTeleportArea(args[0])) != null) {
			MessageUtil.sendMessage(sender, "&6----------&5&lTeleportArea Info&6----------");
			MessageUtil.sendMessage(sender, "&bName: &d{0}", teleportArea.getName());
			MessageUtil.sendMessage(sender, "&bWorld: &d{0}", teleportArea.getName());
			DecimalFormat format = new DecimalFormat("##.##");
			Location tpLoc = teleportArea.getTpLocation();
			MessageUtil.sendMessage(sender, "&bTP Location: &d({0}, {1}, {2})", format.format(tpLoc.getX()), format.format(tpLoc.getY()), format.format(tpLoc.getZ()));
			BoundingBox area = teleportArea.getArea();
			MessageUtil.sendMessage(sender, "&bArea: &d({0}, {1}, {2}) -> ({3}, {4}, {5})", format.format(area.getMinX()), format.format(area.getMinY()), format.format(area.getMinZ()), format.format(area.getMaxX()), format.format(area.getMaxY()), format.format(area.getMaxZ()));
		} else {
			MessageUtil.sendMessage(sender, "&c{0}という名前のテレポートエリアは登録されていません", args[0].toLowerCase());
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
