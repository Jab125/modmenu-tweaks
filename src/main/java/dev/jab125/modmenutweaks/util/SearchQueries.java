package dev.jab125.modmenutweaks.util;

import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.util.mod.Mod;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SearchQueries {
	public static SearchQuery fromBadge(Mod.Badge badge) {
		String s = BadgeUtil.translationKey(badge);
		TextColor textColor = BadgeUtil.textColor(badge);
		if (s == null) return null;
		return new SearchQuery() {
			@Override
			public boolean passes(Mod mod) {
				return mod.getBadges().contains(badge);
			}

			@Override
			public String[] filterNames() {
				return I18n.translate(s).split(" ");
			}

			@Override
			public String name() {
				return badge.name().toLowerCase(Locale.ROOT);
			}

			@Override
			public TextColor textColor() {
				return textColor;
			}
		};
	}

	public static @Nullable SearchQuery of(String string) {
		return Arrays.stream(SEARCH_QUERIES).filter(a -> a.name().equals(string)).findFirst().orElse(null);
	}

	public static @Nullable SearchQuery from(String string) {
		Optional<SearchQuery> first = Arrays.stream(SEARCH_QUERIES).filter(a -> Arrays.asList(a.filterNames()).contains(string)).findFirst();
		return first.orElse(null);
	}

	public static final SearchQuery[] SEARCH_QUERIES;
	static {
		final ArrayList<SearchQuery> queries = new ArrayList<>();
		for (Mod.Badge value : Mod.Badge.values()) {
			SearchQuery query = fromBadge(value);
			if (query != null) queries.add(query);
		}
		// String configurable = I18n.translate("modmenu.searchTerms.configurable");
		// String hasUpdate = I18n.translate("modmenu.searchTerms.hasUpdate");
		queries.add(new SearchQuery() {
			@Override
			public boolean passes(Mod mod) {
				return false;
			}

			@Override
			public boolean passes(Mod mod, ModsScreen screen) {
				return screen.getModHasConfigScreen().get(mod.getId());
			}

			@Override
			public String[] filterNames() {
				return I18n.translate("modmenu.searchTerms.configurable").split(" ");
			}

			@Override
			public String name() {
				return "configurable";
			}

			@Override
			public TextColor textColor() {
				return TextColor.fromRgb(0x65bbd8);
			}
		});
		queries.add(new SearchQuery() {
			@Override
			public boolean passes(Mod mod) {
				return mod.hasUpdate();
			}

			@Override
			public String[] filterNames() {
				return I18n.translate("modmenu.searchTerms.hasUpdate").split(" ");
			}

			@Override
			public String name() {
				return "hasUpdate";
			}

			@Override
			public TextColor textColor() {
				return TextColor.fromRgb(0xaad865);
			}
		});
		SEARCH_QUERIES = queries.toArray(SearchQuery[]::new);
	}
}
