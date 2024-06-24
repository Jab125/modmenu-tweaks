package dev.jab125.modmenutweaks;

import dev.jab125.modmenutweaks.config.MmiConfig;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ModMenuImprovements implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("modmenu-tweaks");
	public static final String MOD_ID = "modmenu-tweaks";

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		try {
			MmiConfig.loadConfig();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		LOGGER.info("Making Mod Menu great again!");
	}
}