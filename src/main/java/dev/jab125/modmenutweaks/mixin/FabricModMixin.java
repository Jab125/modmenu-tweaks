package dev.jab125.modmenutweaks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.terraformersmc.modmenu.util.mod.fabric.FabricIconHandler;
import com.terraformersmc.modmenu.util.mod.fabric.FabricMod;
import dev.jab125.modmenutweaks.ModMenuImprovements;
import dev.jab125.modmenutweaks.config.MmiConfig;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FabricMod.class)
public abstract class FabricModMixin {
	@Inject(method = "getIcon", at = @At(value = "INVOKE", ordinal = 0, target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"))
	void getIconIn(FabricIconHandler iconHandler, int i, CallbackInfoReturnable<NativeImageBackedTexture> cir, @Local(name = "iconSourceId") LocalRef<String> iconSourceId, @Local(name = "iconPath") LocalRef<String> iconPath) {
		if (MmiConfig.PROVIDE_MIXIN_EXTRAS_ICON) {
			if ("mixinextras".equals(iconSourceId.get())) {
				iconSourceId.set(ModMenuImprovements.MOD_ID);
				iconPath.set("assets/" + ModMenuImprovements.MOD_ID + "/mixinextras_icon2.png");
			}
		}
	}
}
