package org.valkyrienskies.mod.mixin.feature.vs2_alpha_hud;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1132;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.config.VSConfigUpdater;
import org.valkyrienskies.mod.common.config.VSGameConfig;

@Mixin(class_329.class)
public class MixinGui {
   @Shadow
   @Final
   private class_310 field_2035;

   @Inject(method = "renderEffects", at = @At("HEAD"))
   private void preRenderStatusEffectOverlay(class_332 guiGraphics, class_9779 deltaTracker, CallbackInfo ci) {
      ConfigValue<?> value = VSConfigUpdater.getForgeConfigValuesMap().get("renderDebugText");
      boolean enabled = value != null ? Boolean.TRUE.equals(value.get()) : VSGameConfig.CLIENT.getRenderDebugText();
      if (enabled) {
         class_327 fontRenderer = this.field_2035.field_1772;
         List<String> debugText = new ArrayList<>();
         debugText.add("VS 2 Beta Build");
         class_1132 integratedServer = this.field_2035.method_1576();
         if (integratedServer != null) {
            String physicsTPS = "Error";
            String loadedVoxelChunks = "Error";

            try {
               physicsTPS = " " + Math.round(VSGameUtilsKt.getVsPipeline(integratedServer).computePhysTps());
               loadedVoxelChunks = " " + VSGameUtilsKt.getVsPipeline(integratedServer).getLoadedVoxelChunks();
            } catch (Exception e) {
               e.printStackTrace();
            }

            String worldPhysicsDebugText = "VS PhysTPS: " + physicsTPS;
            debugText.add(worldPhysicsDebugText);
            String loadedVoxelChunkDebugText = "VS VoxelChunks: " + loadedVoxelChunks;
            debugText.add(loadedVoxelChunkDebugText);
         }

         debugText.add("Using UDP: " + ValkyrienSkiesMod.getVsCore().getClientUsesUDP());

         for (int i = 0; i < debugText.size(); i++) {
            String string = debugText.get(i);
            if (!Strings.isNullOrEmpty(string)) {
               int textHeight = 9;
               int textLength = fontRenderer.method_1727(string);
               int posY = 20 + i * 9;
               int posX = 1;
               guiGraphics.method_25294(1, posY - 1, 2 + textLength + 1, posY + 9 - 1, -1873784752);
               guiGraphics.method_25303(fontRenderer, string, 2, posY, 14737632);
            }
         }
      }
   }
}
