package org.millenaire.block.mock;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MockGenericTreeSpawnBlock extends MockPayloadBlock {
   private static final VoxelShape CARPET_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

   public MockGenericTreeSpawnBlock(Properties properties) {
      super(properties);
   }

   @Override
   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return CARPET_SHAPE;
   }

   @Nullable
   @Override
   protected Property<? extends Comparable<?>> variantProperty() {
      return null;
   }

   @Override
   protected String translationKeyPrefix() {
      return "block.millenaire.mock_generic_tree_spawn.";
   }

   @Override
   public String pointType() {
      return "treeSpawn";
   }

   @Nullable
   @Override
   public String payloadFor(ItemStack stack) {
      return stack.getItem() instanceof BlockItem blockItem && isAcceptedSapling(blockItem.getBlock())
         ? BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()
         : null;
   }

   public static boolean isAcceptedSapling(Block block) {
      return block.defaultBlockState().is(BlockTags.SAPLINGS) || block instanceof SaplingBlock;
   }

   @Override
   public String acceptedItemsKey() {
      return "block.millenaire.mock_generic_tree_spawn.accepted";
   }

   @Override
   public ItemStack displayStack(String payload) {
      ResourceLocation id = ResourceLocation.tryParse(payload);
      if (id == null) {
         return ItemStack.EMPTY;
      }

      Item item = (Item)BuiltInRegistries.ITEM.getOptional(id).orElse(null);
      return item != null ? new ItemStack(item) : ItemStack.EMPTY;
   }

   @Nullable
   @Override
   public BlockState getReplacementState(BlockState mockState) {
      return null;
   }
}
