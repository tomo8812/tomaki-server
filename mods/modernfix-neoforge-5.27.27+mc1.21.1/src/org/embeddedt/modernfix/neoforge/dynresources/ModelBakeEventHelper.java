package org.embeddedt.modernfix.neoforge.dynresources;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo.ModVersion;
import org.embeddedt.modernfix.ModernFix;
import org.embeddedt.modernfix.util.ForwardingInclDefaultsMap;
import org.jetbrains.annotations.Nullable;

public class ModelBakeEventHelper {
   private static final Map<String, ModelBakeEventHelper.UniverseVisibility> MOD_VISIBILITY_CONFIGURATION = ImmutableMap.builder()
      .put("eternal_starlight", ModelBakeEventHelper.UniverseVisibility.SELF_AND_DEPS)
      .put("alexscaves", ModelBakeEventHelper.UniverseVisibility.SELF_AND_DEPS)
      .put("refinedstorage", ModelBakeEventHelper.UniverseVisibility.SELF_AND_DEPS)
      .put("cabletiers", ModelBakeEventHelper.UniverseVisibility.SELF_AND_DEPS)
      .build();
   private final Map<ModelResourceLocation, BakedModel> modelRegistry;
   private final Set<ModelResourceLocation> topLevelModelLocations;
   private final Set<String> namespacesWithModels;
   private final MutableGraph<String> dependencyGraph;
   private static final Set<String> WARNED_MOD_IDS = new HashSet<>();

   public ModelBakeEventHelper(Map<ModelResourceLocation, BakedModel> modelRegistry) {
      this.modelRegistry = modelRegistry;
      int blockStateCount = 0;

      for (Block b : BuiltInRegistries.BLOCK) {
         blockStateCount += b.getStateDefinition().getPossibleStates().size();
      }

      this.topLevelModelLocations = new ObjectLinkedOpenHashSet(blockStateCount + BuiltInRegistries.ITEM.size());
      this.namespacesWithModels = new ObjectOpenHashSet(ModList.get().size());
      ModelLocationBuilder modelLocationBuilder = new ModelLocationBuilder();
      BuiltInRegistries.BLOCK.entrySet().forEach(entry -> {
         ResourceLocation location = ((ResourceKey)entry.getKey()).location();
         modelLocationBuilder.generateForBlock(this.topLevelModelLocations, (Block)entry.getValue(), location);
         this.namespacesWithModels.add(location.getNamespace());
      });
      BuiltInRegistries.ITEM.keySet().forEach(key -> {
         this.topLevelModelLocations.add(new ModelResourceLocation(key, "inventory"));
         this.namespacesWithModels.add(key.getNamespace());
      });
      this.topLevelModelLocations.addAll(modelRegistry.keySet());
      FileToIdConverter itemModelLister = FileToIdConverter.json("models/item");
      itemModelLister.listMatchingResources(Minecraft.getInstance().getResourceManager()).keySet().forEach(itemModel -> {
         this.topLevelModelLocations.add(ModelResourceLocation.inventory(itemModelLister.fileToId(itemModel)));
         this.namespacesWithModels.add(itemModel.getNamespace());
      });

      for (ModelResourceLocation loc : modelRegistry.keySet()) {
         this.namespacesWithModels.add(loc.id().getNamespace());
      }

      this.dependencyGraph = this.buildDependencyGraph();
   }

   private MutableGraph<String> buildDependencyGraph() {
      MutableGraph<String> dependencyGraph = GraphBuilder.undirected().build();
      ModList.get().forEachModContainer((idx, mc) -> {
         dependencyGraph.addNode(idx);

         for (ModVersion versionx : mc.getModInfo().getDependencies()) {
            dependencyGraph.addNode(versionx.getModId());
         }
      });

      for (String id : dependencyGraph.nodes()) {
         Optional<? extends ModContainer> mContainer = ModList.get().getModContainerById(id);
         if (mContainer.isPresent()) {
            for (ModVersion version : mContainer.get().getModInfo().getDependencies()) {
               if (!Objects.equals(id, version.getModId()) && !version.getModId().equals("minecraft") && this.namespacesWithModels.contains(version.getModId())
                  )
                {
                  dependencyGraph.putEdge(id, version.getModId());
               }
            }
         }
      }

      return dependencyGraph;
   }

   private Map<ModelResourceLocation, BakedModel> createWarningRegistry(final String modId) {
      return new ForwardingInclDefaultsMap<ModelResourceLocation, BakedModel>() {
         protected Map<ModelResourceLocation, BakedModel> delegate() {
            return ModelBakeEventHelper.this.modelRegistry;
         }

         private void logWarning() {
            if (ModelBakeEventHelper.WARNED_MOD_IDS.add(modId)) {
               ModernFix.LOGGER
                  .warn(
                     "Mod '{}' is accessing Map#keySet/entrySet/values/replaceAll on the model registry map inside its event handler. This probably won't work as expected with dynamic resources on. Prefer using Map#get/put and constructing ModelResourceLocations another way.",
                     modId
                  );
            }
         }

         public Set<ModelResourceLocation> keySet() {
            this.logWarning();
            return super.keySet();
         }

         public Set<Entry<ModelResourceLocation, BakedModel>> entrySet() {
            this.logWarning();
            return super.entrySet();
         }

         public Collection<BakedModel> values() {
            this.logWarning();
            return super.values();
         }

         @Override
         public void replaceAll(BiFunction<? super ModelResourceLocation, ? super BakedModel, ? extends BakedModel> function) {
            this.logWarning();
            super.replaceAll(function);
         }

         // $VF: synthetic method
         // $VF: bridge method
         protected Object delegate() {
            return this.delegate();
         }
      };
   }

   private Set<String> computeVisibleModIds(String modId) {
      Set<String> deps;
      try {
         deps = this.dependencyGraph.adjacentNodes(modId);
      } catch (IllegalArgumentException e) {
         deps = Set.of();
      }

      if (deps.isEmpty()) {
         return Set.of(modId);
      }

      ObjectOpenHashSet<String> set = new ObjectOpenHashSet();
      set.add(modId);
      set.addAll(deps);
      return Set.copyOf(set);
   }

   public Map<ModelResourceLocation, BakedModel> wrapRegistry(String modId) {
      ModelBakeEventHelper.UniverseVisibility config = MOD_VISIBILITY_CONFIGURATION.getOrDefault(modId, ModelBakeEventHelper.UniverseVisibility.EVERYTHING);
      if (config == ModelBakeEventHelper.UniverseVisibility.NONE) {
         return this.createWarningRegistry(modId);
      }

      Set<String> modIdsToInclude = this.computeVisibleModIds(modId);
      Set<ModelResourceLocation> ourModelLocations;
      if (config == ModelBakeEventHelper.UniverseVisibility.SELF_AND_DEPS) {
         ModernFix.LOGGER.debug("Mod {} is restricted to seeing models from mods: [{}]", modId, String.join(", ", modIdsToInclude));
         ourModelLocations = Sets.filter(this.topLevelModelLocations, loc -> modIdsToInclude.contains(loc.id().getNamespace()));
      } else {
         ourModelLocations = this.topLevelModelLocations;
      }

      BakedModel missingModel = this.modelRegistry.get(ModelBakery.MISSING_MODEL_LOCATION);
      return new ModelBakeEventHelper.EmulatedModelRegistry(modId, modIdsToInclude, missingModel, ourModelLocations);
   }

   private static class DynamicModelEntrySet extends AbstractSet<Entry<ModelResourceLocation, BakedModel>> {
      private final Map<ModelResourceLocation, BakedModel> modelRegistry;
      private final Set<ModelResourceLocation> modelLocations;

      private DynamicModelEntrySet(Map<ModelResourceLocation, BakedModel> modelRegistry, Set<ModelResourceLocation> modelLocations) {
         this.modelRegistry = modelRegistry;
         this.modelLocations = modelLocations;
      }

      @Override
      public Iterator<Entry<ModelResourceLocation, BakedModel>> iterator() {
         final Iterator<ModelResourceLocation> iter = this.modelLocations.iterator();
         return new Iterator<Entry<ModelResourceLocation, BakedModel>>() {
            @Override
            public boolean hasNext() {
               return iter.hasNext();
            }

            public Entry<ModelResourceLocation, BakedModel> next() {
               return DynamicModelEntrySet.this.new DynamicModelEntry(iter.next());
            }

            // $VF: synthetic method
            // $VF: bridge method
            @Override
            public Object next() {
               return this.next();
            }
         };
      }

      @Override
      public boolean contains(Object o) {
         return o instanceof Entry entry ? this.modelRegistry.containsKey(entry.getKey()) : false;
      }

      @Override
      public int size() {
         return this.modelRegistry.size();
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         throw new UnsupportedOperationException();
      }

      private class DynamicModelEntry implements Entry<ModelResourceLocation, BakedModel> {
         private final ModelResourceLocation location;

         private DynamicModelEntry(ModelResourceLocation location) {
            this.location = location;
         }

         public ModelResourceLocation getKey() {
            return this.location;
         }

         public BakedModel getValue() {
            return DynamicModelEntrySet.this.modelRegistry.get(this.location);
         }

         public BakedModel setValue(BakedModel value) {
            return DynamicModelEntrySet.this.modelRegistry.put(this.location, value);
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public Object setValue(Object param1) {
            return this.setValue((BakedModel)var1);
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public Object getValue() {
            return this.getValue();
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public Object getKey() {
            return this.getKey();
         }
      }
   }

   public class EmulatedModelRegistry extends ForwardingMap<ModelResourceLocation, BakedModel> {
      private final Set<String> modIdsToInclude;
      private final BakedModel missingModel;
      private final Set<ModelResourceLocation> ourModelLocations;
      private final String modId;

      private EmulatedModelRegistry(String modId, Set<String> modIdsToInclude, BakedModel missingModel, Set<ModelResourceLocation> ourModelLocations) {
         this.modId = modId;
         this.modIdsToInclude = modIdsToInclude;
         this.missingModel = missingModel;
         this.ourModelLocations = ourModelLocations;
      }

      protected Map<ModelResourceLocation, BakedModel> delegate() {
         return ModelBakeEventHelper.this.modelRegistry;
      }

      public BakedModel get(@Nullable Object key) {
         BakedModel model = (BakedModel)super.get(key);
         if (model == null && key instanceof ModelResourceLocation mrl && this.modIdsToInclude.contains(mrl.id().getNamespace())) {
            ModernFix.LOGGER.warn("Model {} is missing, but was requested in model bake event. Returning missing model", key);
            return this.missingModel;
         } else {
            return model;
         }
      }

      public Set<ModelResourceLocation> keySet() {
         return Collections.unmodifiableSet(this.ourModelLocations);
      }

      public boolean containsKey(@Nullable Object key) {
         return this.ourModelLocations.contains(key) || super.containsKey(key);
      }

      public Set<Entry<ModelResourceLocation, BakedModel>> entrySet() {
         return new ModelBakeEventHelper.DynamicModelEntrySet(this, this.ourModelLocations);
      }

      public void replaceAll(BiFunction<? super ModelResourceLocation, ? super BakedModel, ? extends BakedModel> function) {
         ModernFix.LOGGER
            .warn(
               "Mod '{}' is calling replaceAll on the model registry. Some hacks will be used to keep this fast, but they may not be 100% compatible.",
               this.modId
            );

         for (ModelResourceLocation location : this.ourModelLocations) {
            boolean needsReplacement;
            try {
               needsReplacement = function.apply(location, null) != null;
            } catch (Throwable e) {
               needsReplacement = true;
            }

            if (needsReplacement) {
               BakedModel existing = this.get(location);
               BakedModel replacement = function.apply(location, existing);
               if (replacement != existing) {
                  this.put(location, replacement);
               }
            }
         }
      }

      // $VF: synthetic method
      // $VF: bridge method
      public Object get(@Nullable Object param1) {
         return this.get(var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      protected Object delegate() {
         return this.delegate();
      }
   }

   private enum UniverseVisibility {
      NONE,
      SELF_AND_DEPS,
      EVERYTHING;

      // $VF: synthetic method
      private static ModelBakeEventHelper.UniverseVisibility[] $values() {
         return new ModelBakeEventHelper.UniverseVisibility[]{NONE, SELF_AND_DEPS, EVERYTHING};
      }
   }
}
