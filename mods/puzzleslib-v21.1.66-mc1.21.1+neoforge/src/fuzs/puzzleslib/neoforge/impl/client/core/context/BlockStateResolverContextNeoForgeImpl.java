package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import fuzs.puzzleslib.api.client.core.v1.context.BlockStateResolverContext;
import fuzs.puzzleslib.api.client.renderer.v1.model.ModelLoadingHelper;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.neoforge.impl.client.renderer.ModelDiscovery;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent.ModifyBakingResult;

public final class BlockStateResolverContextNeoForgeImpl implements BlockStateResolverContext {
   private final ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
   private final Function<Material, TextureAtlasSprite> textureResolver;
   private final UnbakedModel missingModel;
   private final Supplier<TextureAtlasSprite> missingSprite;
   private final Function<ResourceLocation, BlockModel> modelResolver;
   private final Map<ResourceLocation, UnbakedModel> modelCache = new HashMap<>();
   private final BiConsumer<ModelResourceLocation, BakedModel> blockStateModelOutput;

   public BlockStateResolverContextNeoForgeImpl(ModifyBakingResult event) {
      this.textureResolver = event.getTextureGetter();
      ModelBakery bakery = event.getModelBakery();
      this.missingModel = bakery.missingModel;
      this.missingSprite = Suppliers.memoize(() -> {
         Material material = new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());
         TextureAtlasSprite textureAtlasSprite = (TextureAtlasSprite)event.getTextureGetter().apply(material);
         Objects.requireNonNull(textureAtlasSprite, "missing sprite is null");
         return textureAtlasSprite;
      });
      Map<ResourceLocation, BlockModel> modelResources = new HashMap<>();
      Function<ResourceLocation, BlockModel> modelResolver = this::getBlockModel;
      this.modelResolver = modelId -> bakery.modelResources.containsKey(modelId)
         ? (BlockModel)bakery.modelResources.get(modelId)
         : modelResources.computeIfAbsent(modelId, modelResolver);
      this.blockStateModelOutput = event.getModels()::put;
   }

   private BlockModel getBlockModel(ResourceLocation modelId) {
      BlockModel blockModel = ModelLoadingHelper.loadBlockModel(this.resourceManager, modelId);
      if (blockModel == null) {
         PuzzlesLib.LOGGER.warn("Missing block model: {}", modelId);
         return (BlockModel)this.missingModel;
      } else {
         return blockModel;
      }
   }

   @Override
   public void registerBlockStateResolver(Block block, Consumer<BiConsumer<BlockState, UnbakedModel>> blockStateConsumer) {
      ModelDiscovery discovery = new ModelDiscovery(this.modelResolver, this.modelCache, this.missingModel);
      blockStateConsumer.accept((state, model) -> discovery.registerModelAndLoadDependencies(BlockModelShaper.stateToModelLocation(state), model));
      discovery.getTopLevelModels().values().forEach(model -> model.resolveParents(discovery::getModel));
      this.loadModels(discovery).forEach(this.blockStateModelOutput);
   }

   private Map<ModelResourceLocation, BakedModel> loadModels(ModelDiscovery discovery) {
      return loadModels(InactiveProfiler.INSTANCE, this.textureResolver, discovery, this.missingSprite);
   }

   @Override
   public <T> void registerBlockStateResolver(
      Block block,
      BiFunction<ResourceManager, Executor, CompletableFuture<T>> resourceLoader,
      BiConsumer<T, BiConsumer<BlockState, UnbakedModel>> blockStateConsumer
   ) {
      this.registerBlockStateResolver(
         block, consumer -> blockStateConsumer.accept(resourceLoader.apply(this.resourceManager, Util.backgroundExecutor()).join(), consumer)
      );
   }

   private static Map<ModelResourceLocation, BakedModel> loadModels(
      ProfilerFiller profiler, Function<Material, TextureAtlasSprite> textureResolver, ModelDiscovery discovery, Supplier<TextureAtlasSprite> missingSprite
   ) {
      profiler.push(PuzzlesLibMod.id("baking").toString());
      Multimap<String, Material> missingSprites = Multimaps.synchronizedMultimap(HashMultimap.create());
      discovery.bakeModels((modelId, material) -> {
         TextureAtlasSprite sprite = textureResolver.apply(material);
         if (sprite != null) {
            return sprite;
         }

         missingSprites.put(modelId.toString(), material);
         return missingSprite.get();
      });
      missingSprites.asMap()
         .forEach(
            (string, collection) -> PuzzlesLib.LOGGER
               .warn(
                  "Missing textures in model {}:\n{}",
                  string,
                  collection.stream()
                     .sorted(Material.COMPARATOR)
                     .map(material -> "    " + material.atlasLocation() + ":" + material.texture())
                     .collect(Collectors.joining("\n"))
               )
         );
      profiler.pop();
      return discovery.getBakedTopLevelModels();
   }
}
