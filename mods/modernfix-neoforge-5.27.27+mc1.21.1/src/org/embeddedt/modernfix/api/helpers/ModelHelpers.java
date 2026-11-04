package org.embeddedt.modernfix.api.helpers;

import com.google.common.collect.ImmutableList;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.embeddedt.modernfix.dynamicresources.ModelBakeryHelpers;
import org.embeddedt.modernfix.util.DynamicMap;

public final class ModelHelpers {
   public static ImmutableList<BlockState> getBlockStateForLocation(ModelResourceLocation location) {
      Optional<Block> blockOpt = BuiltInRegistries.BLOCK.getOptional(location.id());
      return blockOpt.isPresent() ? ModelBakeryHelpers.getBlockStatesForMRL(blockOpt.get().getStateDefinition(), location) : ImmutableList.of();
   }

   public static ImmutableList<BlockState> getBlockStateForLocation(StateDefinition<Block, BlockState> definition, ModelResourceLocation location) {
      return ModelBakeryHelpers.getBlockStatesForMRL(definition, location);
   }

   public static Map<ResourceLocation, BakedModel> createFakeTopLevelMap(BiFunction<ResourceLocation, ModelState, BakedModel> modelGetter) {
      return new DynamicMap<>(ResourceLocation.class, location -> modelGetter.apply(location, BlockModelRotation.X0_Y0));
   }

   public static ModelBaker adaptBakery(ModelBakery bakery) {
      throw new UnsupportedOperationException("TODO");
   }
}
