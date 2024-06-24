package dev.jab125.modmenutweaks.util;

import com.terraformersmc.modmenu.util.mod.Mod;
import net.minecraft.text.TextColor;

public class BadgeUtil {
	public static String translationKey(Mod.Badge badge) {
		return switch (badge) {
			case LIBRARY -> "modmenu.searchTerms.library";
			case CLIENT -> "modmenu.searchTerms.clientside";
			case DEPRECATED -> "modmenu.searchTerms.deprecated";
			case PATCHWORK_FORGE -> "modmenu.searchTerms.patchwork";
			case MODPACK -> "modmenu.searchTerms.modpack";
			case MINECRAFT -> null;
			//noinspection UnnecessaryDefault // they might add more badges
			default -> null;
		};
	}

	public static TextColor textColor(Mod.Badge badge) {
		int color = switch (badge) {
			case LIBRARY -> 0xff4ce6b5;
			case CLIENT -> 0xff3484fe;
			case DEPRECATED -> 0xffe44e66;
			case PATCHWORK_FORGE -> 0xff7a93b8;
			case MODPACK -> 0xffc868ca;
			case MINECRAFT -> 0xff9b9997;
			default -> -1;
		};
		if (color != -1) return TextColor.fromRgb(color);
		return TextColor.fromRgb(0xff0000);
	}
}
