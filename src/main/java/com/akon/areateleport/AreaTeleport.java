package com.akon.areateleport;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.DependsOn;
import org.bukkit.plugin.java.annotation.permission.ChildPermission;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.permission.Permissions;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import org.bukkit.plugin.java.annotation.plugin.author.Authors;

import java.io.File;

@Plugin(name = "AreaTeleport", version = "1.0")
@DependsOn(@Dependency("WorldEdit"))
@Authors(@Author("akon"))
@Commands(@Command(name = "areateleport", aliases = "at", permission = "areateleport.command.areateleport"))
@Permissions({
	@Permission(name = "areateleport.*", children = @ChildPermission(name = "areateleport.command.*")),
	@Permission(name = "areateleport.command.*", children = @ChildPermission(name = "areateleport.command.areateleport")),
	@Permission(name = "areateleport.command.areateleport")
})
public final class AreaTeleport extends JavaPlugin {

	private static AreaTeleport instance;
	private static TeleportAreaManager teleportAreaManager;
	private static final WorldEditPlugin WORLD_EDIT = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");

	@Override
	public void onEnable() {
		instance = this;
		Bukkit.getPluginManager().registerEvents(new TeleportListener(), this);
		AreaTeleportCommand areaTeleportCommand = new AreaTeleportCommand();
		PluginCommand command = this.getCommand("areateleport");
		command.setExecutor(areaTeleportCommand);
		command.setTabCompleter(areaTeleportCommand);
		areaTeleportCommand.loadCommands("com.akon.areateleport.command");
		ConfigurationSerialization.registerClass(TeleportArea.class);
		File file = new File(this.getDataFolder(), "data.yml");
		if (!file.exists()) {
			this.saveResource("data.yml", false);
		}
		teleportAreaManager = new TeleportAreaManager(file);
	}

	public static AreaTeleport getInstance() {
		return instance;
	}

	public static WorldEditPlugin getWEInstance() {
		return WORLD_EDIT;
	}

	public static TeleportAreaManager getTeleportAreaManager() {
		return teleportAreaManager;
	}

}
