package net.satisfy.farm_and_charm.core.block.crops;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class BarleyCropBlock extends CropBlock {
   public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 4);

   public BarleyCropBlock(Properties properties) {
      super(properties);
   }

   @NotNull
   protected ItemLike getBaseSeedId() {
      return (ItemLike)ObjectRegistry.BARLEY_SEEDS.get();
   }

   public int getMaxAge() {
      return 4;
   }

   @NotNull
   public IntegerProperty getAgeProperty() {
      return AGE;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AGE});
   }
}
