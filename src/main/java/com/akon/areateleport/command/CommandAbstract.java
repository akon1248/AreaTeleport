package com.akon.areateleport.command;

import com.akon.areateleport.AreaTeleport;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public abstract class CommandAbstract {

	public abstract boolean execute(CommandSender sender, Command command, String label, String[] args);

	public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return null;
	}

	protected List<String> nameComplete(String arg) {
		return AreaTeleport.getTeleportAreaManager().getTeleportAreaNames().stream().filter(name -> name.toLowerCase().startsWith(arg.toLowerCase())).collect(Collectors.toList());
	}

	public final String name() {
		Cmd cmd;
		return (cmd = this.getClass().getAnnotation(Cmd.class)) != null ? cmd.value() : null;
	}

	public final String[] aliases() {
		Cmd cmd;
		return (cmd = this.getClass().getAnnotation(Cmd.class)) != null ? cmd.aliases() : null;
	}

	public final String[] params() {
		Cmd cmd;
		return (cmd = this.getClass().getAnnotation(Cmd.class)) != null ? cmd.params() : null;
	}

	public final String description()  {
		Cmd cmd;
		return (cmd = this.getClass().getAnnotation(Cmd.class)) != null ? cmd.description() : null;
	}


}
