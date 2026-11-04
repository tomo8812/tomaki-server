package net.caffeinemc.mods.lithium.neoforge.mixin.startup;

import net.caffeinemc.mods.lithium.common.LithiumMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
   @Inject(
      method = "<init>",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;loadSelectedResourcePacks(Lnet/minecraft/server/packs/repository/PackRepository;)V")
   )
   private void lithium$loadConfig(GameConfig gameConfig, CallbackInfo ci) {
      LithiumMod.onInitialization(ModList.get().getModContainerById("lithium").map(t -> t.getModInfo().getVersion().toString()).orElse("UNKNOWN"));
   }
}
