package net.caffeinemc.mods.lithium.mixin.ai.task.run.long_jump_weighted_choice;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.caffeinemc.mods.lithium.common.util.collections.LongJumpChoiceList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.LongJumpToRandomPos;
import net.minecraft.world.entity.ai.behavior.LongJumpToRandomPos.PossibleJump;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LongJumpToRandomPos.class)
public class LongJumpToRandomPosMixin<E extends Mob> {
   @Shadow
   protected List<PossibleJump> jumpCandidates;
   @Shadow
   @Final
   protected int maxLongJumpWidth;
   @Shadow
   @Final
   protected int maxLongJumpHeight;

   @Inject(
      method = "start(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;J)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;betweenClosedStream(IIIIII)Ljava/util/stream/Stream;"),
      cancellable = true
   )
   private void setTargets(ServerLevel serverWorld, E mobEntity, long l, CallbackInfo ci, @Local BlockPos centerPos) {
      if (this.maxLongJumpWidth < 128 && this.maxLongJumpHeight < 128) {
         this.jumpCandidates = LongJumpChoiceList.forCenter(centerPos, (byte)this.maxLongJumpWidth, (byte)this.maxLongJumpHeight);
         ci.cancel();
      }
   }

   @Redirect(
      method = "getJumpCandidate(Lnet/minecraft/server/level/ServerLevel;)Ljava/util/Optional;",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/random/WeightedRandom;getRandomItem(Lnet/minecraft/util/RandomSource;Ljava/util/List;)Ljava/util/Optional;"
      )
   )
   private Optional<PossibleJump> getRandomFast(RandomSource random, List<PossibleJump> pool) {
      return pool instanceof LongJumpChoiceList longJumpChoiceList
         ? Optional.ofNullable(longJumpChoiceList.removeRandomWeightedByDistanceSq(random))
         : WeightedRandom.getRandomItem(random, pool);
   }

   @Redirect(
      method = "getJumpCandidate(Lnet/minecraft/server/level/ServerLevel;)Ljava/util/Optional;",
      at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V")
   )
   private void skipRemoveIfAlreadyRemoved(Optional<PossibleJump> result, Consumer<? super PossibleJump> removeAction) {
      if (!(this.jumpCandidates instanceof LongJumpChoiceList)) {
         result.ifPresent(removeAction);
      }
   }
}
