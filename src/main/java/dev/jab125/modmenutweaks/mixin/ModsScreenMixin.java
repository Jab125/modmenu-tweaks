package dev.jab125.modmenutweaks.mixin;

import com.terraformersmc.modmenu.gui.ModsScreen;
import dev.jab125.modmenutweaks.config.MmiConfig;
import dev.jab125.modmenutweaks.util.SearchQuery;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiFunction;

@Mixin(ModsScreen.class)
public class ModsScreenMixin {
	@Shadow private TextFieldWidget searchBox;

	@Inject(method = "init", at = @At("RETURN"))
	void init(CallbackInfo ci) {
		if (MmiConfig.REWORKED_SEARCH) {
			this.searchBox.setRenderTextProvider(new BiFunction<String, @NotNull Integer, OrderedText>() {
				@Override
				public OrderedText apply(String string, @NotNull /* I hope */ Integer strStart) {
					return SearchQuery.highlight(SearchQuery.find(searchBox.getText()), string, strStart);
				}
			});
		}
	}

	private String substring(int start, int end, String string) {
		try {
			return string.substring(Math.max(0, start), Math.min(string.length(), end));
		} catch (Throwable t) {
			return "";
		}
	}
}
