package net.caffeinemc.mods.lithium.mixin.block_pattern_matching;

import net.caffeinemc.mods.lithium.common.world.block_pattern_matching.BlockPatternExtended;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.dimension.end.EndDragonFight.Data;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndDragonFight.class)
public class EndDragonFightMixin {
   @Shadow
   @Final
   private BlockPattern exitPortalPattern;

   @Inject(
      method = "<init>(Lnet/minecraft/server/level/ServerLevel;JLnet/minecraft/world/level/dimension/end/EndDragonFight$Data;Lnet/minecraft/core/BlockPos;)V",
      at = @At("RETURN")
   )
   private void setPatternToDragonPattern(ServerLevel serverLevel, long l, Data data, BlockPos blockPos, CallbackInfo ci) {
      ((BlockPatternExtended)this.exitPortalPattern).lithium$setRequiredBlock(Blocks.BEDROCK, 41);
   }
}
