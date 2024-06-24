package dev.jab125.modmenutweaks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.function.Consumer;

public class MmiConfig {
	public static boolean REWORKED_SEARCH = true;
	public static Mode SEARCH_MODE = Mode.AND;
	public static boolean PROVIDE_MIXIN_EXTRAS_ICON = true;
	public static boolean PROVIDE_FABRIC_API_RANDOM_COLORS = true;
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("modmenu-tweaks.json");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static void loadConfig() throws IOException {
		if (!CONFIG_PATH.toFile().exists()) saveConfig();
		String s = Files.readString(CONFIG_PATH);
		JsonObject object = GSON.fromJson(s, JsonObject.class);
		setIfAvailable(object, "schemaVersion", MmiConfig::assert1, () -> assert1(0));
		setIfAvailable(object, "reworkedSearch", (Boolean a) -> REWORKED_SEARCH = a, () -> REWORKED_SEARCH = true);
		setIfAvailable(object, "searchMode", (String a) -> SEARCH_MODE = Mode.valueOf(a.toUpperCase(Locale.ROOT)), () -> SEARCH_MODE = Mode.AND);
		setIfAvailable(object, "provides", (JsonObject a) -> {
			setIfAvailable(a, "mixinextras", (Boolean b) -> PROVIDE_MIXIN_EXTRAS_ICON = b, () -> PROVIDE_MIXIN_EXTRAS_ICON = false);
			setIfAvailable(a, "random-colors!fabric-api", (Boolean b) -> PROVIDE_FABRIC_API_RANDOM_COLORS = b, () -> PROVIDE_FABRIC_API_RANDOM_COLORS = false);
		}, () -> {});
	}

	private static void assert1(Integer i) {
		if (i != 1) throw new AssertionError("schemaVersion must be 1!");
	}

	private static <T> void setIfAvailable(JsonObject object, String name, Consumer<T> o, Runnable el) {
		if (object.has(name)) {
			if (object.get(name).isJsonPrimitive()) {
				JsonPrimitive p = object.getAsJsonPrimitive(name);
				if (p.isBoolean()) {
					((Consumer<Boolean>) o).accept(p.getAsBoolean());
					return;
				}
				if (p.isNumber()) {
					((Consumer<Integer>) o).accept(p.getAsInt());
					return;
				}
				if (p.isString()) {
					((Consumer<String>) o).accept(p.getAsString());
					return;
				}
			}
			if (object.get(name).isJsonObject()) {
				((Consumer<JsonObject>) o).accept(object.getAsJsonObject(name));
				return;
			}
		}
		System.out.println("RUNNING " + name);
		el.run();
	}

	public static void saveConfig() throws IOException {
		JsonObject object = new JsonObject();
		object.addProperty("schemaVersion", 1);
		object.addProperty("reworkedSearch", REWORKED_SEARCH);
		object.addProperty("searchMode", SEARCH_MODE.name().toLowerCase(Locale.ROOT));
		JsonObject provides = new JsonObject();
		if (PROVIDE_MIXIN_EXTRAS_ICON) provides.addProperty("mixinextras", PROVIDE_MIXIN_EXTRAS_ICON);
		if (PROVIDE_FABRIC_API_RANDOM_COLORS) provides.addProperty("random-colors!fabric-api", PROVIDE_FABRIC_API_RANDOM_COLORS);
		object.add("provides", provides);
		Files.writeString(CONFIG_PATH, GSON.toJson(object));
	}

	public enum Mode {
		AND,
		OR
	}
}
