package org.millenaire.block.mock;

import javax.annotation.Nullable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class MockGenericMaterialBlock extends MockPayloadBlock {
   private final String pointType;
   private final String name;
   private final boolean fullCubeOnly;

   public MockGenericMaterialBlock(Properties properties, String pointType, String name, boolean fullCubeOnly) {
      super(properties);
      this.pointType = pointType;
      this.name = name;
      this.fullCubeOnly = fullCubeOnly;
   }

   public boolean isSource() {
      return "source".equals(this.pointType);
   }

   @Nullable
   @Override
   protected Property<? extends Comparable<?>> variantProperty() {
      return null;
   }

   @Override
   protected String translationKeyPrefix() {
      return "block.millenaire." + this.name + ".";
   }

   @Override
   public String pointType() {
      return this.pointType;
   }

   @Nullable
   @Override
   public String payloadFor(ItemStack stack) {
      if (stack.getItem() instanceof BlockItem blockItem) {
         BlockState state = blockItem.getBlock().defaultBlockState();
         return GenericBlockPayload.isAccepted(state, this.fullCubeOnly) ? GenericBlockPayload.serialize(state) : null;
      } else {
         return null;
      }
   }

   @Nullable
   @Override
   public String payloadFor(ItemStack stack, BlockPlaceContext context) {
      if (stack.getItem() instanceof BlockItem blockItem) {
         BlockState state = blockItem.getBlock().getStateForPlacement(context);
         if (state == null) {
            state = blockItem.getBlock().defaultBlockState();
         }

         if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
            state = (BlockState)state.setValue(BlockStateProperties.WATERLOGGED, false);
         }

         return GenericBlockPayload.isAccepted(state, this.fullCubeOnly) ? GenericBlockPayload.serialize(state) : null;
      } else {
         return null;
      }
   }

   @Override
   public boolean authorsWhileSneaking() {
      return false;
   }

   @Override
   public String acceptedItemsKey() {
      return "block.millenaire." + this.name + ".accepted";
   }

   @Override
   public ItemStack displayStack(String payload) {
      BlockState state = this.resolvedState(payload);
      return state != null ? new ItemStack(state.getBlock()) : ItemStack.EMPTY;
   }

   @Nullable
   public BlockState resolvedState(@Nullable String payload) {
      return GenericBlockPayload.resolve(payload, this.fullCubeOnly);
   }

   @Nullable
   @Override
   public BlockState getReplacementState(BlockState mockState) {
      return null;
   }

   @Nullable
   @Override
   public BlockState getReplacementState(BlockState mockState, @Nullable String payload) {
      return this.resolvedState(payload);
   }
}
