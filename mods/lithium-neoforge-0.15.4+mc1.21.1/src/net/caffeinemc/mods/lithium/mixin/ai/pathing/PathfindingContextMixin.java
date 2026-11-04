package net.caffeinemc.mods.lithium.mixin.ai.pathing;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PathfindingContext.class)
public class PathfindingContextMixin {
   @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;level()Lnet/minecraft/world/level/Level;"))
   private Level getWorldIfNonNull(Mob instance, Operation<Level> original) {
      return instance == null ? null : (Level)original.call(new Object[]{instance});
   }

   @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;blockPosition()Lnet/minecraft/core/BlockPos;"))
   private BlockPos getBlockPosIfNonNull(Mob instance, Operation<BlockPos> original) {
      return instance == null ? null : (BlockPos)original.call(new Object[]{instance});
   }
}
