package fuzs.puzzleslib.api.client.data.v2;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.data.v2.models.MaterialMapper;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.ItemModelGenerators.TrimModelData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractAtlasProvider implements DataProvider {
   public static final ResourceLocation TRIM_PALETTE_KEY = ResourceLocation.withDefaultNamespace("trims/color_palettes/trim_palette");
   public static final List<ResourceKey<TrimPattern>> VANILLA_PATTERNS = List.of(
      TrimPatterns.SENTRY,
      TrimPatterns.DUNE,
      TrimPatterns.COAST,
      TrimPatterns.WILD,
      TrimPatterns.WARD,
      TrimPatterns.EYE,
      TrimPatterns.VEX,
      TrimPatterns.TIDE,
      TrimPatterns.SNOUT,
      TrimPatterns.RIB,
      TrimPatterns.SPIRE,
      TrimPatterns.WAYFINDER,
      TrimPatterns.SHAPER,
      TrimPatterns.SILENCE,
      TrimPatterns.RAISER,
      TrimPatterns.HOST,
      TrimPatterns.FLOW,
      TrimPatterns.BOLT
   );
   private final Map<ResourceLocation, List<SpriteSource>> values = new LinkedHashMap<>();
   private final PathProvider pathProvider;

   public AbstractAtlasProvider(DataProviderContext context) {
      this(context.getPackOutput());
   }

   public AbstractAtlasProvider(PackOutput packOutput) {
      this.pathProvider = packOutput.createPathProvider(Target.RESOURCE_PACK, "atlases");
   }

   public static SpriteSource forMaterial(Material material) {
      return new SingleFile(material.texture(), Optional.empty());
   }

   public static SpriteSource forMapper(MaterialMapper mapper) {
      return new DirectoryLister(mapper.prefix(), mapper.prefix() + "/");
   }

   public static List<SpriteSource> simpleMapper(MaterialMapper mapper) {
      return List.of(forMapper(mapper));
   }

   public static List<SpriteSource> noPrefixMapper(String path) {
      return List.of(new DirectoryLister(path, ""));
   }

   public static AbstractAtlasProvider.TrimPatternBuilder armorTrims() {
      return new AbstractAtlasProvider.TrimPatternBuilder();
   }

   public static AbstractAtlasProvider.TrimPatternBuilder armorTrimPatterns() {
      return armorTrims().addVanillaLayers().addVanillaPermutations();
   }

   public static AbstractAtlasProvider.TrimPatternBuilder armorTrimPermutations() {
      return armorTrims().addVanillaPatterns().addVanillaLayers();
   }

   public final CompletableFuture<?> run(CachedOutput output) {
      this.addAtlases();
      return CompletableFuture.allOf(
         this.values.entrySet().stream().map(entry -> this.storeAtlas(output, entry.getKey(), entry.getValue())).toArray(CompletableFuture[]::new)
      );
   }

   public final CompletableFuture<?> storeAtlas(CachedOutput output, ResourceLocation atlasId, List<SpriteSource> sources) {
      return DataProvider.saveStable(output, RegistryAccess.EMPTY, SpriteSources.FILE_CODEC, sources, this.pathProvider.json(atlasId));
   }

   public abstract void addAtlases();

   protected void addMaterial(Material material) {
      this.add((ResourceLocation)ModelManager.VANILLA_ATLASES.get(material.atlasLocation()), forMaterial(material));
   }

   protected void add(ResourceLocation id, SpriteSource... spriteSources) {
      this.add(id, Arrays.asList(spriteSources));
   }

   protected void add(ResourceLocation id, List<SpriteSource> spriteSources) {
      this.values.computeIfAbsent(id, location -> new ArrayList<>()).addAll(spriteSources);
   }

   public String getName() {
      return "Atlas Definitions";
   }

   public static class TrimPatternBuilder {
      private final List<ResourceKey<TrimPattern>> patterns = new ArrayList<>();
      private final List<UnaryOperator<String>> layers = new ArrayList<>();
      private final Map<String, ResourceLocation> permutations = new TreeMap<>();
      private ResourceLocation palette = AbstractAtlasProvider.TRIM_PALETTE_KEY;
      @Nullable
      private String namespaceOverride;

      TrimPatternBuilder() {
      }

      public AbstractAtlasProvider.TrimPatternBuilder addPattern(ResourceKey<TrimPattern> pattern) {
         this.patterns.add(pattern);
         return this;
      }

      public AbstractAtlasProvider.TrimPatternBuilder addPatterns(List<ResourceKey<TrimPattern>> patterns) {
         this.patterns.addAll(patterns);
         return this;
      }

      public AbstractAtlasProvider.TrimPatternBuilder addVanillaPatterns() {
         return this.addPatterns(AbstractAtlasProvider.VANILLA_PATTERNS);
      }

      public AbstractAtlasProvider.TrimPatternBuilder addLayer(UnaryOperator<String> layer) {
         this.layers.add(layer);
         return this;
      }

      public AbstractAtlasProvider.TrimPatternBuilder addLayers(List<UnaryOperator<String>> layers) {
         this.layers.addAll(layers);
         return this;
      }

      public AbstractAtlasProvider.TrimPatternBuilder addVanillaLayers() {
         return this.addLayer(name -> "trims/models/armor/" + name).addLayer(name -> "trims/models/armor/" + name + "_leggings");
      }

      public AbstractAtlasProvider.TrimPatternBuilder addPermutation(TrimModelData data) {
         data.overrideArmorMaterials().values().forEach(this::addPermutation);
         return this.addPermutation(data.name());
      }

      public AbstractAtlasProvider.TrimPatternBuilder addPermutations(List<TrimModelData> data) {
         data.forEach(this::addPermutation);
         return this;
      }

      public AbstractAtlasProvider.TrimPatternBuilder addPermutation(String asset) {
         Objects.requireNonNull(this.namespaceOverride, "namespace is null");
         return this.addPermutation(ResourceLocation.fromNamespaceAndPath(this.namespaceOverride, asset));
      }

      public AbstractAtlasProvider.TrimPatternBuilder addPermutation(ResourceLocation base, Map<Holder<ArmorMaterial>, ResourceLocation> overrides) {
         overrides.values().forEach(this::addPermutation);
         return this.addPermutation(base);
      }

      public AbstractAtlasProvider.TrimPatternBuilder addPermutation(ResourceLocation asset) {
         this.permutations.put(asset.getPath(), asset.withPrefix("trims/color_palettes/"));
         return this;
      }

      public AbstractAtlasProvider.TrimPatternBuilder addPermutation(String suffix, ResourceLocation palette) {
         this.permutations.put(suffix, palette);
         return this;
      }

      public AbstractAtlasProvider.TrimPatternBuilder addPermutations(Map<String, ResourceLocation> permutations) {
         this.permutations.putAll(permutations);
         return this;
      }

      public AbstractAtlasProvider.TrimPatternBuilder addVanillaPermutations() {
         return this.setNamespaceOverride("minecraft").addPermutations(ItemModelGenerators.GENERATED_TRIM_MODELS).setNamespaceOverride(null);
      }

      public AbstractAtlasProvider.TrimPatternBuilder setPalette(ResourceLocation palette) {
         Objects.requireNonNull(palette, "palette is null");
         this.palette = palette;
         return this;
      }

      public AbstractAtlasProvider.TrimPatternBuilder setNamespaceOverride(@Nullable String namespaceOverride) {
         this.namespaceOverride = namespaceOverride;
         return this;
      }

      public List<SpriteSource> build() {
         Preconditions.checkArgument(!this.patterns.isEmpty(), "patterns is empty");
         Preconditions.checkArgument(!this.layers.isEmpty(), "layers is empty");
         Preconditions.checkArgument(!this.permutations.isEmpty(), "permutations is empty");
         List<ResourceLocation> textures = patternTextures(this.patterns, this.layers);
         return List.of(new PalettedPermutations(textures, this.palette, this.permutations));
      }

      private static List<ResourceLocation> patternTextures(List<ResourceKey<TrimPattern>> patterns, List<UnaryOperator<String>> layers) {
         List<ResourceLocation> result = new ArrayList<>(patterns.size() * layers.size());

         for (ResourceKey<TrimPattern> vanillaPattern : patterns) {
            ResourceLocation assetId = vanillaPattern.location();

            for (UnaryOperator<String> humanoidLayer : layers) {
               result.add(assetId.withPath(humanoidLayer));
            }
         }

         return result;
      }
   }
}
