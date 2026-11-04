package org.millenaire.block.mock;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MockGenericAnimalSpawnBlock extends MockPayloadBlock {
   private static final VoxelShape CARPET_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

   public MockGenericAnimalSpawnBlock(Properties properties) {
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
      return "block.millenaire.mock_generic_animal_spawn.";
   }

   @Override
   public String pointType() {
      return "animalSpawn";
   }

   @Nullable
   @Override
   public String payloadFor(ItemStack stack) {
      return stack.getItem() instanceof SpawnEggItem egg ? BuiltInRegistries.ENTITY_TYPE.getKey(egg.getType(stack)).toString() : null;
   }

   @Override
   public String acceptedItemsKey() {
      return "block.millenaire.mock_generic_animal_spawn.accepted";
   }

   @Override
   public ItemStack displayStack(String payload) {
      EntityType<?> type = (EntityType<?>)EntityType.byString(payload).orElse(null);
      if (type == null) {
         return ItemStack.EMPTY;
      }

      SpawnEggItem egg = SpawnEggItem.byId(type);
      return egg != null ? new ItemStack(egg) : ItemStack.EMPTY;
   }

   @Nullable
   @Override
   public BlockState getReplacementState(BlockState mockState) {
      return null;
   }
}
