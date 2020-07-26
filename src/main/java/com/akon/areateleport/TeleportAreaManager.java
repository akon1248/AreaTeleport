package com.akon.areateleport;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

public class TeleportAreaManager {

	TeleportAreaManager(File dataFile) {
		this.dataFile = dataFile;
		this.reload();
	}

	private final File dataFile;
	private FileConfiguration dataFileYaml;
	private final HashMap<String, TeleportArea> teleportAreas = Maps.newHashMap();
	private final HashMultimap<World, TeleportArea> fromWorld = HashMultimap.create();
	
	public void reload() {
		this.teleportAreas.clear();
		this.fromWorld.clear();
		if (this.dataFile.exists()) {
			this.dataFileYaml = YamlConfiguration.loadConfiguration(this.dataFile);
			for (String key: this.dataFileYaml.getKeys(false)) {
				Object teleportArea = this.dataFileYaml.get(key);
				if (teleportArea instanceof TeleportArea) {
					this.registerTeleportArea((TeleportArea)teleportArea);
				}
			}
		}
	}

	public boolean registerTeleportArea(TeleportArea area) {
		if (!this.teleportAreas.containsKey(area.getName())) {
			this.teleportAreas.put(area.getName(), area);
			this.fromWorld.put(area.getWorld(), area);
			return true;
		}
		return false;
	}

	public boolean unregisterTeleportArea(TeleportArea area) {
		if (this.teleportAreas.get(area.getName()) == area) {
			this.teleportAreas.remove(area.getName());
			this.fromWorld.remove(area.getWorld(), area);
			return true;
		}
		return false;
	}

	public void updateYaml(String name) throws IOException {
		if (this.teleportAreas.containsKey(name.toLowerCase())) {
			this.dataFileYaml.set(name.toLowerCase(), this.teleportAreas.get(name.toLowerCase()));
			this.dataFileYaml.save(this.dataFile);
		} else if (this.dataFileYaml.get(name.toLowerCase()) instanceof TeleportArea) {
			this.dataFileYaml.set(name.toLowerCase(), null);
			this.dataFileYaml.save(this.dataFile);
		}
	}

	public TeleportArea getTeleportArea(String name) {
		return this.teleportAreas.get(name.toLowerCase());
	}

	public Collection<TeleportArea> getTeleportAreas() {
		return Collections.unmodifiableCollection(this.teleportAreas.values());
	}

	public Collection<TeleportArea> getTeleportAreas(World world) {
		return Collections.unmodifiableCollection(this.fromWorld.get(world));
	}

	public Collection<String> getTeleportAreaNames() {
		return Collections.unmodifiableCollection(this.teleportAreas.keySet());
	}

	public Collection<String> getTeleportAreaNames(World world) {
		return Collections.unmodifiableCollection(this.fromWorld.get(world).stream().map(TeleportArea::getName).collect(Collectors.toList()));
	}

}
