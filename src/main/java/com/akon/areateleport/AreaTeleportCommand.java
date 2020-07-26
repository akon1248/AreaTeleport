package com.akon.areateleport;

import com.akon.areateleport.command.Cmd;
import com.akon.areateleport.command.CommandAbstract;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class AreaTeleportCommand implements CommandExecutor, TabCompleter {

	private final LinkedHashMap<String, CommandAbstract> commands = Maps.newLinkedHashMap();

	public void loadCommands(String pkg) {
		this.addCommand(new CommandHelp());
		ArrayList<CommandAbstract> commandList = Lists.newArrayList();
		try (FileSystem fileSystem = FileSystems.newFileSystem(URI.create("jar:" + AreaTeleportCommand.class.getProtectionDomain().getCodeSource().getLocation().toString()), new HashMap<>())) {
			Files.walkFileTree(fileSystem.getPath("/" + pkg.replace(".", "/") + "/"), new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
				if (file.toString().endsWith(".class")) {
					try {
						Class<?> clazz = Class.forName(file.toString().substring(1, file.toString().length()-6).replace("/", "."));
						if (CommandAbstract.class.isAssignableFrom(clazz)) {
							try {
								commandList.add((CommandAbstract)clazz.newInstance());
							} catch (ReflectiveOperationException ex) {
								if (!(ex instanceof InstantiationException)) {
									ex.printStackTrace();
								}
							}
						}
					} catch (ClassNotFoundException ex) {
						ex.printStackTrace();
					}
				}
				return FileVisitResult.CONTINUE;
				}

			});
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		Collections.reverse(commandList);
		commandList.forEach(this::addCommand);
	}

	public void addCommand(CommandAbstract command) {
		if (command.getClass().getAnnotation(Cmd.class) != null) {
			this.commands.put(command.name().toLowerCase(), command);
			Arrays.stream(command.aliases()).forEach(alias -> this.commands.put(alias, command));
		}
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (sender instanceof Player) {
			if (args.length == 0 || !this.commands.containsKey(args[0]) || !this.commands.get(args[0].toLowerCase()).execute(sender, command, label, removeFirst(args))) {
				this.failMessage(sender, label);
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
		List<String> result = Lists.newArrayList();
		if (args.length == 1) {
			this.commands.keySet().stream().filter(str -> str.toLowerCase().startsWith(args[0].toLowerCase())).forEach(result::add);
		} else if (args.length > 1 && this.commands.containsKey(args[0].toLowerCase())) {
			result = this.commands.get(args[0].toLowerCase()).tabComplete(sender, command, alias, this.removeFirst(args));
		}
		return result;
	}

	private void failMessage(CommandSender sender, String label) {
		MessageUtil.sendMessage(sender, "&c不正なコマンドです /{0} helpを参照してください", label);
	}

	private String[] removeFirst(String[] strArr) {
		if (strArr.length > 0) {
			String[] newArray = new String[strArr.length - 1];
			System.arraycopy(strArr, 1, newArray, 0, newArray.length);
			return newArray;
		}
		return strArr;
	}

	@Cmd(value = "help", description = "このメッセージを表示します")
	public class CommandHelp extends CommandAbstract {

		@Override
		public boolean execute(CommandSender sender, Command command, String label, String[] args) {
			if (args.length == 0) {
				MessageUtil.sendMessage(sender, "&6----------&5&lAreaTeleport Help&6----------");
				for (Map.Entry<String, CommandAbstract> entry: AreaTeleportCommand.this.commands.entrySet()) {
					if (entry.getKey().equalsIgnoreCase(entry.getValue().name())) {
						String[] aliases = new String[entry.getValue().aliases().length + 1];
						aliases[0] = entry.getValue().name();
						for (int i = 1; i < aliases.length; i++) {
							aliases[i] = entry.getValue().aliases()[i - 1];
						}
						String aliasesText = String.join("|", aliases);
						if (entry.getValue().params().length == 0) {
							MessageUtil.sendMessage(sender, "&b/{0} {1} &7- {2}", label, aliasesText, entry.getValue().description());
						} else {
							MessageUtil.sendMessage(sender, "&b/{0} {1} &d{2} &7- {3}", label, aliasesText, String.join(" ", entry.getValue().params()), entry.getValue().description());
						}
					}
				}
				return true;
			}
			return false;
		}

	}

}
