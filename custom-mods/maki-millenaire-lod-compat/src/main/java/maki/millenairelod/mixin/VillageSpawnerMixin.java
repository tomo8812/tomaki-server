package maki.millenairelod.mixin;

import maki.millenairelod.Compat;
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
    @Inject(method = "spawnVillage(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lorg/millenaire/culture/VillageType;ILjava/lang/String;Lorg/millenaire/village/VillageId;Lnet/minecraft/server/level/ServerPlayer;)Lnet/minecraft/network/chat/Component;",
        at = @At(value = "INVOKE", ordinal = 0, target = "Lorg/millenaire/world/TerrainPreparer;checkForSnow(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;IILnet/minecraft/world/level/block/Rotation;Lorg/millenaire/building/ClearMargins;)[[Z"))
    private static void compat$reserve(ServerLevel level, BlockPos center, VillageType type,
                                       int completion, String parent, VillageId id, ServerPlayer controller,
                                       CallbackInfoReturnable<Component> cir) {
        Compat.reserve(level, center, type.radius());
    }
}
