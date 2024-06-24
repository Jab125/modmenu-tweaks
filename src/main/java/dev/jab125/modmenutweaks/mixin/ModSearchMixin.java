package dev.jab125.modmenutweaks.mixin;

import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.util.mod.Mod;
import com.terraformersmc.modmenu.util.mod.ModSearch;
import dev.jab125.modmenutweaks.config.MmiConfig;
import dev.jab125.modmenutweaks.util.ContextAwareSearchQueryWrapper;
import dev.jab125.modmenutweaks.util.ListUtils;
import dev.jab125.modmenutweaks.util.SearchQuery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Locale;

@Mixin(value = ModSearch.class, remap = false)
public abstract class ModSearchMixin {
	@Shadow private static boolean authorMatches(Mod mod, String query) { return false; }

	@Shadow private static int passesFilters(ModsScreen screen, Mod mod, String query) { return 0; }

	@Inject(method = "search", at = @At("HEAD"))
	private static void search(ModsScreen screen, String query, List<Mod> candidates, CallbackInfoReturnable<List<Mod>> cir) {
		// New search, should be better
		//System.out.println("Minecraft passes: " + passesFilters(screen, ModMenu.MODS.get("minecraft"), "@clientside"));
	}

	@Inject(method = "passesFilters", at = @At("HEAD"), cancellable = true)
	private static void passesFiltersM(ModsScreen screen, Mod mod, String query, CallbackInfoReturnable<Integer> cir) {
		if (MmiConfig.REWORKED_SEARCH) {
			cir.setReturnValue(passesFiltersReimpl(screen, mod, query));
		}
	}

	@Unique
	private static int passesFiltersReimpl(ModsScreen screen, Mod mod, String query) {
		final String originalQuery = query;
		String modId = mod.getId();
		String modName = mod.getName();
		String modTranslatedName = mod.getTranslatedName();
		String modDescription = mod.getDescription();
		String modSummary = mod.getSummary();

		List<ContextAwareSearchQueryWrapper> queries = SearchQuery.find(query);
		boolean filterPasses = filterPasses(screen, mod, queries);
		StringBuilder b = new StringBuilder(query);
		for (ContextAwareSearchQueryWrapper wrapper : ListUtils.reversed(queries)) {
			b.replace(wrapper.start(), wrapper.end(), "");
		}
		query = b.toString().trim();
		//System.out.println(query);
		//queries.stream().map(a -> a.query().name()).forEach(a -> System.out.println(a));

		// Libraries are currently hidden, ignore them entirely
		if (mod.isHidden() || !ModMenuConfig.SHOW_LIBRARIES.getValue() && mod.getBadges().contains(Mod.Badge.LIBRARY)) {
			return 0;
		}

		if (filterPasses) {
			if (query.isBlank()) {
				return 1;
			}
			// Some basic search, could do with something more advanced but this will do for now
			if (modName.toLowerCase(Locale.ROOT).contains(query) // Search default mod name
				|| modTranslatedName.toLowerCase(Locale.ROOT).contains(query) // Search localized mod name
				|| modId.toLowerCase(Locale.ROOT).contains(query) // Search mod ID
			) {
				return query.length() >= 3 ? 2 : 1;
			}

			if (modDescription.toLowerCase(Locale.ROOT).contains(query) // Search default mod description
				|| modSummary.toLowerCase(Locale.ROOT).contains(query) // Search mod summary
				|| authorMatches(mod, query) // Search via author
			) {
				return 1;
			}
		}

		// Allow parent to pass filter if a child passes
		if (ModMenu.PARENT_MAP.keySet().contains(mod)) {
			for (Mod child : ModMenu.PARENT_MAP.get(mod)) {
				int result = passesFilters(screen, child, originalQuery);

				if (result > 0) {
					return result;
				}
			}
		}
		return 0;
	}

	@Unique
	private static boolean filterPasses(ModsScreen screen, Mod mod, List<ContextAwareSearchQueryWrapper> from) {
		boolean filterPasses = false;
		if (MmiConfig.SEARCH_MODE == MmiConfig.Mode.OR) {
			if (from.isEmpty()) return true;
			for (ContextAwareSearchQueryWrapper contextAwareSearchQueryWrapper : from) {
				if (contextAwareSearchQueryWrapper.query().passes(mod, screen)) {
					filterPasses = true;
					break;
				}
			}
		} else if (MmiConfig.SEARCH_MODE == MmiConfig.Mode.AND) {
			r:
			{
				for (ContextAwareSearchQueryWrapper contextAwareSearchQueryWrapper : from) {
					if (!contextAwareSearchQueryWrapper.query().passes(mod, screen)) {
						filterPasses = false;
						break r;
					}
				}
				filterPasses = true;
			}
		}
		return filterPasses;
	}
}
