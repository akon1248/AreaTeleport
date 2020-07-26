package com.akon.areateleport;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@SerializableAs("TeleportArea")
public class TeleportArea implements ConfigurationSerializable {

	private final String name;
	private Location tpLocation;
	private BoundingBox area;

	public TeleportArea(String name, Location tpLocation, BoundingBox area) {
		this.name = name.toLowerCase();
		this.tpLocation = tpLocation;
		this.area = area;
	}

	public World getWorld() {
		return this.tpLocation.getWorld();
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		MessageUtil.sendMessage(Bukkit.getConsoleSender(), "StackTrace: ");
		for (StackTraceElement ste: new Throwable().getStackTrace()) {
			MessageUtil.sendMessage(Bukkit.getConsoleSender(), "    {0}", ste);
		}
		LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();
		map.put("name", this.name);
		map.put("tp-location", this.tpLocation);
		map.put("area", this.area);
		return map;
	}

	public static @NotNull TeleportArea deserialize(@NotNull Map<String, Object> args) {
		return new TeleportArea((String)args.get("name"), (Location)args.get("tp-location"), (BoundingBox)args.get("area"));
	}

}
