package com.akon.areateleport;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;

@UtilityClass
public class MessageUtil {

	public String colored(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}

	public String format(String format, Object... args) {
		return colored(MessageFormat.format(format, args));
	}

	public void sendMessage(CommandSender sender, String format, Object... args) {
		sender.sendMessage(format(format, args));
	}

}
