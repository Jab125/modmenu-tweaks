package dev.jab125.modmenutweaks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.util.mod.fabric.FabricIconHandler;
import dev.jab125.modmenutweaks.config.MmiConfig;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.util.Random;

@Mixin(value = FabricIconHandler.class, remap = false)
public class FabricIconHandlerMixin {
	@Inject(method = "createIcon", at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/Validate;validState(ZLjava/lang/String;[Ljava/lang/Object;)V"))
	void createIcon(ModContainer iconSource, String iconPath, CallbackInfoReturnable<NativeImageBackedTexture> cir, @Local(name = "image") NativeImage image) {
		if (MmiConfig.PROVIDE_FABRIC_API_RANDOM_COLORS) {
			String parent = ModMenu.MODS.get(iconSource.getMetadata().getId()).getParent();
			if (parent != null && (parent.equals("fabric-api") || parent.equals("fabric"))) {
				float hue = new Random(iconSource.getMetadata().getId().hashCode()).nextFloat();
				for (int x = 0; x < image.getWidth(); x++) {
					for (int y = 0; y < image.getHeight(); y++) {
						if (image.getOpacity(x, y) == 0) continue;
						int original = image.getColor(x, y);
						int r = (original >> 16) & 0xff;
						int g = (original >> 8) & 0xff;
						int b = (original) & 0xff;
						float[] HSV = new float[3];
						Color.RGBtoHSB(r, g, b, HSV);

						image.setColor(x, y, Color.HSBtoRGB(hue, HSV[1], HSV[2]));
					}
				}
			}
		}
	}
}
