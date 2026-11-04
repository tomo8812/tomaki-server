package com.wodichka.disabler.mixin;

import com.mojang.datafixers.util.Either;
import com.wodichka.disabler.config.DisablerConfig;
import com.wodichka.disabler.world.BiomeRemovalResolver;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.Climate.ParameterList;
import net.minecraft.world.level.biome.Climate.Sampler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MultiNoiseBiomeSourceMixin {
   @Shadow
   @Final
   private Either<ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters;
   @Unique
   private volatile List<BiomeRemovalResolver.Candidate> disabler$allowedBiomes;

   @Inject(method = "getNoiseBiome", at = @At("RETURN"), cancellable = true)
   private void disabler$replaceBlockedBiome(int quartX, int quartY, int quartZ, Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
      if (DisablerConfig.hasBlockedBiomes()) {
         Holder<Biome> selectedBiome = (Holder<Biome>)cir.getReturnValue();
         if (BiomeRemovalResolver.isBlocked(selectedBiome)) {
            List<BiomeRemovalResolver.Candidate> allowedBiomes = this.disabler$getAllowedBiomes();
            if (!allowedBiomes.isEmpty()) {
               cir.setReturnValue(BiomeRemovalResolver.resolveReplacement(selectedBiome, sampler.sample(quartX, quartY, quartZ), allowedBiomes));
            }
         }
      }
   }

   @Unique
   private List<BiomeRemovalResolver.Candidate> disabler$getAllowedBiomes() {
      List<BiomeRemovalResolver.Candidate> cached = this.disabler$allowedBiomes;
      if (cached == null) {
         ParameterList<Holder<Biome>> parameterList = (ParameterList<Holder<Biome>>)this.parameters
            .map(directParameters -> directParameters, presetParameters -> ((MultiNoiseBiomeSourceParameterList)presetParameters.value()).parameters());
         cached = BiomeRemovalResolver.collectAllowedBiomes(parameterList);
         this.disabler$allowedBiomes = cached;
      }

      return cached;
   }
}
