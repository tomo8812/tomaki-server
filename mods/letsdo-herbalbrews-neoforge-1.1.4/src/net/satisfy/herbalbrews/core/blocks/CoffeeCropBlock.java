package net.satisfy.herbalbrews.core.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.herbalbrews.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class CoffeeCropBlock extends CropBlock {
   public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);

   public CoffeeCropBlock(Properties settings) {
      super(settings);
   }

   @NotNull
   protected ItemLike getBaseSeedId() {
      return (ItemLike)ObjectRegistry.COFFEE_BEANS.get();
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

   protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
   ) {
      int i = (Integer)blockState.getValue(AGE);
      boolean bl = i == 3;
      if (!bl && player.getItemInHand(interactionHand).is(Items.BONE_MEAL)) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else if (i > 1) {
         int tomatoCount = level.random.nextInt(2) + (bl ? 1 : 0);
         popResource(level, blockPos, new ItemStack((ItemLike)ObjectRegistry.COFFEE_BEANS.get(), tomatoCount));
         level.playSound(null, blockPos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
         level.setBlock(blockPos, (BlockState)blockState.setValue(AGE, 1), 2);
         return ItemInteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return super.useItemOn(itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
      }
   }
}
