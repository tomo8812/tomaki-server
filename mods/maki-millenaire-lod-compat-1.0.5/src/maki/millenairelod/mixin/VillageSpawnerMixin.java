package maki.millenairelod.mixin;

import maki.millenairelod.Compat;
import maki.millenairelod.ReservationLifecycle;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.millenaire.culture.VillageType;
import org.millenaire.village.VillageId;
import org.millenaire.world.VillageSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VillageSpawner.class, remap = false)
public abstract class VillageSpawnerMixin {
   @Inject(
      method = "spawnVillage(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lorg/millenaire/culture/VillageType;ILjava/lang/String;Lorg/millenaire/village/VillageId;Lnet/minecraft/server/level/ServerPlayer;)Lnet/minecraft/network/chat/Component;",
      at = @At(
         value = "INVOKE",
         ordinal = 0,
         target = "Lorg/millenaire/world/TerrainPreparer;checkForSnow(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;IILnet/minecraft/world/level/block/Rotation;Lorg/millenaire/building/ClearMargins;)[[Z"
      )
   )
   private static void compat$reserve(
      ServerLevel var0, BlockPos var1, VillageType var2, int var3, String var4, VillageId var5, ServerPlayer var6, CallbackInfoReturnable<Component> var7
   ) {
      Compat.reserve(var0, var1, var2.radius());
   }

   @Inject(
      method = "spawnVillage(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lorg/millenaire/culture/VillageType;ILjava/lang/String;Lorg/millenaire/village/VillageId;Lnet/minecraft/server/level/ServerPlayer;)Lnet/minecraft/network/chat/Component;",
      at = @At("RETURN")
   )
   private static void compat$release(
      ServerLevel var0, BlockPos var1, VillageType var2, int var3, String var4, VillageId var5, ServerPlayer var6, CallbackInfoReturnable var7
   ) {
      ReservationLifecycle.end(var0, var1, var2.radius());
   }

   @Inject(
      method = "spawnVillage(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lorg/millenaire/culture/VillageType;ILjava/lang/String;Lorg/millenaire/village/VillageId;Lnet/minecraft/server/level/ServerPlayer;)Lnet/minecraft/network/chat/Component;",
      at = @At("HEAD")
   )
   private static void compat$enter(
      ServerLevel var0, BlockPos var1, VillageType var2, int var3, String var4, VillageId var5, ServerPlayer var6, CallbackInfoReturnable var7
   ) {
      ReservationLifecycle.enter(var0, var1, var2.radius());
   }
}
