package com.akon.areateleport;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {

	public static String colored(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}

	public static String uncolored(String text) {
		return ChatColor.stripColor(colored(text));
	}

	public static String format(String format, Object... args) {
		String text = colored(format);
		Matcher matcher = Pattern.compile("\\{\\d+}").matcher(format);
		while (matcher.find()) {
			String group = matcher.group();
			int index = Integer.parseInt(group.substring(1, group.length()-1));
			if (args.length > index) {
				text = text.replaceFirst(Pattern.quote(group), String.valueOf(args[index]).replace("$", "\\$"));
			} else {
				text = text.replaceFirst(Pattern.quote(group), "null");
			}
		}
		return text;
	}

	public static void sendMessage(CommandSender sender, String format, Object... args) {
		sender.sendMessage(format(format, args));
	}

}
