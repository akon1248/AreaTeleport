package com.akon.areateleport.command;

import com.akon.areateleport.AreaTeleport;
import com.akon.areateleport.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collection;

@Cmd(value ="list", description = "登録されているテレポートエリアの一覧を表示します")
public class CommandList extends CommandAbstract {

	@Override
	public boolean execute(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 0) {
			return false;
		}
		Collection<String> names = AreaTeleport.getTeleportAreaManager().getTeleportAreaNames();
		MessageUtil.sendMessage(sender, "&dテレポートエリア ({0}): {1}", names.size(), MessageUtil.colored("&b" + String.join(MessageUtil.colored("&f, &b"), names.toArray(new String[0]))));
		return true;
	}

}
