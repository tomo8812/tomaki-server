package dev.diorama.mixin;

import dev.diorama.MapSession;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientChunkCache.class)
public abstract class ClientChunkCacheMixin {
   @Inject(method = "onLightUpdate", at = @At("TAIL"))
   private void diorama$onLightUpdate(LightLayer layer, SectionPos pos, CallbackInfo ci) {
      MapSession.lightSectionChanged((ClientChunkCache)this, pos, layer == LightLayer.BLOCK);
   }
}
