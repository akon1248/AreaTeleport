package com.akon.areateleport;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

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
		ConfigurationSerialization.registerClass(TeleportArea.class, "TeleportArea");
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
