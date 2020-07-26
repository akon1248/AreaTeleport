package com.akon.areateleport.command;

import com.akon.areateleport.AreaTeleport;
import com.akon.areateleport.MessageUtil;
import com.akon.areateleport.TeleportAreaManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.List;

@Cmd(value = "unregister", aliases = {"delete", "remove", "del", "rm"}, params = {"<name>"}, description = "テレポートエリアを削除します")
public class CommandUnregister extends CommandAbstract {

	@Override
	public boolean execute(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1) {
			return false;
		}
		TeleportAreaManager teleportAreaManager;
		if ((teleportAreaManager = AreaTeleport.getTeleportAreaManager()).getTeleportArea(args[0]) != null) {
			teleportAreaManager.unregisterTeleportArea(teleportAreaManager.getTeleportArea(args[0]));
			MessageUtil.sendMessage(sender, "&6テレポートエリア{0}を削除しました", args[0].toLowerCase());
			try {
				teleportAreaManager.updateYaml(args[0]);
			} catch (IOException ex) {
				MessageUtil.sendMessage(sender, "テレポートエリアをファイルから削除できませんでした");
				ex.printStackTrace();
			}
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
