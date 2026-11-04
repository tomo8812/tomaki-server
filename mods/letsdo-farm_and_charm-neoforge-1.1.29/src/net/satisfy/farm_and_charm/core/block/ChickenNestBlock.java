package net.satisfy.farm_and_charm.core.block;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.StorageTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import net.satisfy.foundation.registry.FoundationParticles;
import net.satisfy.foundation.storage.StorageBlock;
import net.satisfy.foundation.storage.StorageBlockEntity;
import org.jetbrains.annotations.NotNull;

public class ChickenNestBlock extends StorageBlock {
   private static final VoxelShape CHICKEN_NEST = Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

   public ChickenNestBlock(Properties settings) {
      super(settings);
   }

   @NotNull
   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return CHICKEN_NEST;
   }

   public BlockEntityType<?> blockEntityType() {
      return (BlockEntityType<?>)EntityTypeRegistry.STORAGE_ENTITY.get();
   }

   public int size() {
      return 2;
   }

   public boolean canInsertStack(ItemStack stack) {
      return stack.is(TagRegistry.NEST_EGGS);
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (!(level.getBlockEntity(pos) instanceof StorageBlockEntity nest)) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         NonNullList var11 = nest.getInventory();
         if (this.canInsertStack(stack)) {
            for (int i = 0; i < var11.size(); i++) {
               if (((ItemStack)var11.get(i)).isEmpty()) {
                  this.add(level, pos, player, nest, stack, i);
                  return ItemInteractionResult.sidedSuccess(level.isClientSide);
               }
            }
         }

         for (int i = var11.size() - 1; i >= 0; i--) {
            if (!((ItemStack)var11.get(i)).isEmpty()) {
               this.remove(level, pos, player, nest, i);
               return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
         }

         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public void add(Level level, BlockPos pos, Player player, StorageBlockEntity storageEntity, ItemStack itemStack, int index) {
      super.add(level, pos, player, storageEntity, itemStack, index);
      spawnFeathers(level, pos);
   }

   public void remove(Level level, BlockPos pos, Player player, StorageBlockEntity storageEntity, int index) {
      super.remove(level, pos, player, storageEntity, index);
      spawnFeathers(level, pos);
   }

   private static void spawnFeathers(Level level, BlockPos pos) {
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(
            ColorParticleOption.create((ParticleType)FoundationParticles.FEATHER.get(), -1),
            pos.getX() + 0.5,
            pos.getY() + 0.3,
            pos.getZ() + 0.5,
            3 + level.random.nextInt(3),
            0.25,
            0.05,
            0.25,
            0.0
         );
      }
   }

   public ResourceLocation type() {
      return StorageTypeRegistry.CHICKEN_NEST;
   }

   public Direction[] unAllowedDirections() {
      return new Direction[]{Direction.DOWN};
   }

   public int getSection(Float x, Float z) {
      int xIndex = x < 0.5F ? 0 : 1;
      int zIndex = z < 0.5F ? 0 : 1;
      return zIndex * 2 + xIndex;
   }

   @NotNull
   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
      if (!state.canSurvive(world, pos)) {
         world.scheduleTick(pos, this, 1);
      }

      return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      tooltip.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
   }
}
