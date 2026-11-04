package net.satisfy.farm_and_charm.core.block.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

public class LettuceCropBlock extends CropBlock implements BigCropCapable {
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
   public static final BooleanProperty BIG = BooleanProperty.create("big");
   public static final BooleanProperty GIANT = BooleanProperty.create("giant");

   public LettuceCropBlock(Properties settings) {
      super(settings.randomTicks());
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.getStateDefinition().any()).setValue(AGE, 0)).setValue(BIG, false)).setValue(GIANT, false)
      );
   }

   @NotNull
   protected ItemLike getBaseSeedId() {
      return (ItemLike)ObjectRegistry.LETTUCE_SEEDS.get();
   }

   @Override
   public int getMaxAge() {
      return 3;
   }

   @NotNull
   @Override
   public IntegerProperty getAgeProperty() {
      return AGE;
   }

   @Override
   public BooleanProperty getBigProperty() {
      return BIG;
   }

   @Override
   public BooleanProperty getGiantProperty() {
      return GIANT;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AGE, BIG, GIANT});
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
      return true;
   }

   public boolean isRandomlyTicking(BlockState state) {
      return this.getAge(state) < this.getMaxAge() || !(Boolean)state.getValue(BIG) || !(Boolean)state.getValue(GIANT);
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (level.getRawBrightness(pos, 0) >= 9) {
         int age = this.getAge(state);
         if (age < this.getMaxAge()) {
            float growth = GeneralUtil.GrowthSpeedUtil.getGrowthSpeed(state, level, pos);
            if (random.nextInt((int)(25.0F / growth) + 1) == 0) {
               level.setBlock(pos, this.getStateForAge(age + 1), 2);
            }
         }
      }

      if ((Integer)state.getValue(AGE) == this.getMaxAge() && (!(Boolean)state.getValue(BIG) || !(Boolean)state.getValue(GIANT))) {
         this.tryTransformToBigCrop(level, pos, state, false);
      }
   }
}
