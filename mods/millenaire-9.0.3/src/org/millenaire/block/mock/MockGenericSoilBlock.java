package org.millenaire.block.mock;

import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.Property;
import org.millenaire.block.BlockGrapeVine;
import org.millenaire.item.CropSeedItem;

public class MockGenericSoilBlock extends MockPayloadBlock {
   public MockGenericSoilBlock(Properties properties) {
      super(properties);
   }

   @Nullable
   @Override
   protected Property<? extends Comparable<?>> variantProperty() {
      return null;
   }

   @Override
   protected String translationKeyPrefix() {
      return "block.millenaire.mock_generic_soil.";
   }

   @Override
   public String pointType() {
      return "soil";
   }

   @Nullable
   @Override
   public String payloadFor(ItemStack stack) {
      String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
      Block crop = cropBlockOf(stack.getItem());
      return (crop == null || !isAcceptedCrop(crop)) && GenericSoilAliases.resolve(id) == null ? null : id;
   }

   @Nullable
   static Block cropBlockOf(Item item) {
      if (item instanceof BlockItem blockItem) {
         return blockItem.getBlock();
      } else {
         return item instanceof CropSeedItem seedItem ? seedItem.getCropBlock() : null;
      }
   }

   public static boolean isAcceptedCrop(Block block) {
      return block instanceof CropBlock || block instanceof StemBlock || block instanceof BlockGrapeVine;
   }

   @Override
   public String acceptedItemsKey() {
      return "block.millenaire.mock_generic_soil.accepted";
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
      return Blocks.DIRT.defaultBlockState();
   }

   @Nullable
   @Override
   public BlockState getReplacementState(BlockState mockState, @Nullable String payload) {
      String alias = GenericSoilAliases.resolve(payload);
      if (alias != null) {
         for (CropType crop : CropType.values()) {
            if (crop.getSerializedName().equals(alias)) {
               return MockSoilBlock.replacementFor(crop);
            }
         }
      }

      return this.getReplacementState(mockState);
   }
}
