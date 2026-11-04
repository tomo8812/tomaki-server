package net.caffeinemc.mods.lithium.mixin.chunk.palette;

import net.caffeinemc.mods.lithium.common.world.chunk.LithiumHashPalette;
import net.minecraft.core.IdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.Palette.Factory;
import net.minecraft.world.level.chunk.PalettedContainer.Configuration;
import net.minecraft.world.level.chunk.PalettedContainer.Strategy;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Strategy.class)
public abstract class PalettedContainer$StrategyMixin {
   @Mutable
   @Shadow
   @Final
   public static Strategy SECTION_STATES;
   @Unique
   private static final Configuration<?>[] BLOCKSTATE_DATA_PROVIDERS;
   @Unique
   private static final Configuration<?>[] BIOME_DATA_PROVIDERS = new Configuration[]{
      new Configuration(Strategy.SINGLE_VALUE_PALETTE_FACTORY, 0),
      new Configuration(Strategy.LINEAR_PALETTE_FACTORY, 1),
      new Configuration(Strategy.LINEAR_PALETTE_FACTORY, 2),
      new Configuration(PalettedContainer$StrategyMixin.HASH, 3)
   };
   @Unique
   private static final Factory HASH = LithiumHashPalette::create;
   @Mutable
   @Shadow
   @Final
   public static Strategy SECTION_BIOMES;
   @Shadow
   @Final
   static Factory GLOBAL_PALETTE_FACTORY;

   static {
      final Factory idListFactory = GLOBAL_PALETTE_FACTORY;
      Configuration<?> arrayDataProvider4bit = new Configuration(Strategy.LINEAR_PALETTE_FACTORY, 4);
      Configuration<?> hashDataProvider4bit = new Configuration(HASH, 4);
      BLOCKSTATE_DATA_PROVIDERS = new Configuration[]{
         new Configuration(Strategy.SINGLE_VALUE_PALETTE_FACTORY, 0),
         arrayDataProvider4bit,
         arrayDataProvider4bit,
         hashDataProvider4bit,
         hashDataProvider4bit,
         new Configuration(HASH, 5),
         new Configuration(HASH, 6),
         new Configuration(HASH, 7),
         new Configuration(HASH, 8)
      };
      SECTION_STATES = new Strategy(4) {
         public <A> Configuration<A> getConfiguration(IdMap<A> idList, int bits) {
            return (Configuration<A>)(bits >= 0 && bits < PalettedContainer$StrategyMixin.BLOCKSTATE_DATA_PROVIDERS.length
               ? PalettedContainer$StrategyMixin.BLOCKSTATE_DATA_PROVIDERS[bits]
               : new Configuration(idListFactory, Mth.ceillog2(idList.size())));
         }
      };
      SECTION_BIOMES = new Strategy(2) {
         public <A> Configuration<A> getConfiguration(IdMap<A> idList, int bits) {
            return (Configuration<A>)(bits >= 0 && bits < PalettedContainer$StrategyMixin.BIOME_DATA_PROVIDERS.length
               ? PalettedContainer$StrategyMixin.BIOME_DATA_PROVIDERS[bits]
               : new Configuration(idListFactory, Mth.ceillog2(idList.size())));
         }
      };
   }
}
