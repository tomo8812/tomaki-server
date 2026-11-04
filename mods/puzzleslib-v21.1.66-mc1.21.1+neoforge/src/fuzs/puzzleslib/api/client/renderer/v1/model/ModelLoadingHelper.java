package fuzs.puzzleslib.api.client.renderer.v1.model;

import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.JsonObject;
import fuzs.puzzleslib.impl.PuzzlesLib;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.BlockStateModelLoader.LoadedJson;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;

public final class ModelLoadingHelper {
   private ModelLoadingHelper() {
   }

   @Experimental
   public static Map<BlockState, UnbakedModel> loadBlockState(ResourceManager resourceManager, Block block) {
      return loadBlockState(resourceManager, block, Util.backgroundExecutor()).join();
   }

   public static CompletableFuture<Map<BlockState, UnbakedModel>> loadBlockState(ResourceManager resourceManager, Block block, Executor executor) {
      return loadBlockState(resourceManager, BuiltInRegistries.BLOCK.getKey(block), block.getStateDefinition(), executor);
   }

   @Experimental
   public static Map<BlockState, UnbakedModel> loadBlockState(
      ResourceManager resourceManager, ResourceLocation blockId, StateDefinition<Block, BlockState> stateDefinition
   ) {
      return loadBlockState(resourceManager, blockId, stateDefinition, Util.backgroundExecutor()).join();
   }

   public static CompletableFuture<Map<BlockState, UnbakedModel>> loadBlockState(
      ResourceManager resourceManager, ResourceLocation blockId, StateDefinition<Block, BlockState> stateDefinition, Executor executor
   ) {
      return loadBlockState(resourceManager, blockId, blockId, stateDefinition, executor);
   }

   public static CompletableFuture<Map<BlockState, UnbakedModel>> loadBlockState(
      ResourceManager resourceManager,
      ResourceLocation sourceBlockId,
      ResourceLocation blockId,
      StateDefinition<Block, BlockState> stateDefinition,
      Executor executor
   ) {
      return loadBlockState(resourceManager, sourceBlockId, executor)
         .thenCompose(loadedBlockModelDefinitions -> loadBlockState((List<LoadedJson>)loadedBlockModelDefinitions, blockId, stateDefinition, executor));
   }

   public static CompletableFuture<List<LoadedJson>> loadBlockState(ResourceManager resourceManager, ResourceLocation blockId, Executor executor) {
      return CompletableFuture.<List>supplyAsync(() -> resourceManager.getResourceStack(BlockStateModelLoader.BLOCKSTATE_LISTER.idToFile(blockId)), executor)
         .thenApply(resourceStack -> {
            List<LoadedJson> blockModelDefinitions = new ArrayList<>(resourceStack.size());

            for (Resource resource : resourceStack) {
               try (Reader reader = resource.openAsReader()) {
                  JsonObject jsonObject = GsonHelper.parse(reader);
                  blockModelDefinitions.add(new LoadedJson(resource.sourcePackId(), jsonObject));
               } catch (Exception exception) {
                  PuzzlesLib.LOGGER.error("Failed to load blockstate definition {} from pack {}", new Object[]{blockId, resource.sourcePackId(), exception});
               }
            }

            return blockModelDefinitions;
         });
   }

   public static CompletableFuture<Map<BlockState, UnbakedModel>> loadBlockState(
      List<LoadedJson> loadedBlockModelDefinitions, ResourceLocation blockId, StateDefinition<Block, BlockState> stateDefinition, Executor executor
   ) {
      return CompletableFuture.supplyAsync(
         () -> {
            try {
               Map<ResourceLocation, List<LoadedJson>> blockStateResources = Collections.singletonMap(
                  BlockStateModelLoader.BLOCKSTATE_LISTER.idToFile(blockId), loadedBlockModelDefinitions
               );
               Map<BlockState, UnbakedModel> discoveredModelOutput = new HashMap<>();
               Map<ModelResourceLocation, BlockState> blockStateIds = new HashMap<>();
               UnmodifiableIterator var6 = stateDefinition.getPossibleStates().iterator();

               while (var6.hasNext()) {
                  BlockState blockState = (BlockState)var6.next();
                  blockStateIds.put(BlockModelShaper.stateToModelLocation(blockId, blockState), blockState);
               }

               new BlockStateModelLoader(
                     blockStateResources, InactiveProfiler.INSTANCE, missingModel(), Minecraft.getInstance().getBlockColors(), (modelId, model) -> {
                        BlockState blockStatex = blockStateIds.get(modelId);
                        Objects.requireNonNull(blockStatex, "block state is null");
                        discoveredModelOutput.put(blockStatex, model);
                     }
                  )
                  .loadBlockStateDefinitions(blockId, stateDefinition);
               return discoveredModelOutput;
            } catch (Exception exception) {
               PuzzlesLib.LOGGER.error("Failed to load blockstate definition {}", blockId, exception);
               return null;
            }
         },
         executor
      );
   }

   @Experimental
   @Nullable
   public static BlockModel loadBlockModel(ResourceManager resourceManager, ResourceLocation modelId) {
      return loadBlockModel(resourceManager, modelId, Util.backgroundExecutor()).join();
   }

   public static CompletableFuture<@Nullable BlockModel> loadBlockModel(ResourceManager resourceManager, ResourceLocation modelId, Executor executor) {
      return CompletableFuture.<Optional>supplyAsync(() -> resourceManager.getResource(modelId), executor)
         .thenApply(optional -> optional.<BlockModel>map(resource -> {
            try (Reader reader = resource.openAsReader()) {
               return BlockModel.fromStream(reader);
            } catch (Exception exception) {
               PuzzlesLib.LOGGER.error("Failed to load model {}", modelId, exception);
               return null;
            }
         }).orElse(null));
   }

   public static UnbakedModel missingModel() {
      BlockModel model = BlockModel.fromString(ModelBakery.MISSING_MODEL_MESH);
      model.name = ModelBakery.MISSING_MODEL_LOCATION.toString();
      return model;
   }
}
