package net.satisfy.herbalbrews.core.blocks;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.satisfy.herbalbrews.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class RooibosCropBlock extends CropBlock {
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);

   public RooibosCropBlock(Properties settings) {
      super(settings);
   }

   @NotNull
   protected ItemLike getBaseSeedId() {
      return (ItemLike)ObjectRegistry.ROOIBOS_LEAF.get();
   }

   public int getMaxAge() {
      return 3;
   }

   @NotNull
   public IntegerProperty getAgeProperty() {
      return AGE;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AGE});
   }
}
