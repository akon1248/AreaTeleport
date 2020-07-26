package com.akon.areateleport.command;

import com.akon.areateleport.AreaTeleport;
import com.akon.areateleport.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@Cmd(value = "reload", aliases = {"rl"}, description = "テレポートエリアを読み込み直します")
public class CommandReload extends CommandAbstract {

	@Override
	public boolean execute(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 0) {
			return false;
		}
		AreaTeleport.getTeleportAreaManager().reload();
		MessageUtil.sendMessage(sender,  "&6テレポートエリアをリロードしました");
		return true;
	}

}
