package com.akon.areateleport;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import lombok.Value;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.BoundingBox;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class TeleportAreaManager {

	TeleportAreaManager(File dataFile) {
		this.dataFile = dataFile;
		this.reload();
	}

	private final File dataFile;
	private FileConfiguration dataFileYaml;
	private final HashMap<String, TeleportArea> teleportAreas = Maps.newHashMap();
	private final HashMultimap<ChunkKey, TeleportArea> fromChunk = HashMultimap.create();
	
	public void reload() {
		this.teleportAreas.clear();
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
			World world = area.getWorld();
			BoundingBox bb = area.getArea();
			int minChunkX = Location.locToBlock(bb.getMinX()) >> 4;
			int minChunkZ = Location.locToBlock(bb.getMinZ()) >> 4;
			int maxChunkX = Location.locToBlock(bb.getMaxX()) >> 4;
			int maxChunkZ = Location.locToBlock(bb.getMaxZ()) >> 4;
			for (int x = minChunkX; x <= maxChunkX; x++) {
				for (int z = minChunkZ; z <= maxChunkZ; z++) {
					this.fromChunk.put(new ChunkKey(world, x, z), area);
				}
			}
			return true;
		}
		return false;
	}

	public boolean unregisterTeleportArea(TeleportArea area) {
		if (this.teleportAreas.get(area.getName()) == area) {
			this.teleportAreas.remove(area.getName());
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

	public Collection<TeleportArea> getTeleportAreas(org.bukkit.Chunk chunk) {
		return Collections.unmodifiableCollection(this.fromChunk.get(new ChunkKey(chunk.getWorld(), chunk.getX(), chunk.getZ())));
	}

	public Collection<String> getTeleportAreaNames() {
		return Collections.unmodifiableCollection(this.teleportAreas.keySet());
	}

	@Value
	private static class ChunkKey {

		World world;
		int x;
		int y;

	}

}
