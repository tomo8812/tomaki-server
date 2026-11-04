package org.embeddedt.modernfix.common.mixin.perf.dynamic_resources;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.ModelBakery.BakedCacheKey;
import net.minecraft.client.resources.model.ModelBakery.TextureGetter;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.embeddedt.modernfix.ModernFix;
import org.embeddedt.modernfix.ModernFixClient;
import org.embeddedt.modernfix.annotation.ClientOnlyMixin;
import org.embeddedt.modernfix.api.entrypoint.ModernFixClientIntegration;
import org.embeddedt.modernfix.duck.IBlockStateModelLoader;
import org.embeddedt.modernfix.duck.IExtendedModelBakery;
import org.embeddedt.modernfix.util.DynamicOverridableMap;
import org.embeddedt.modernfix.util.ForwardingInclDefaultsMap;
import org.embeddedt.modernfix.util.LRUMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBakery.class)
@ClientOnlyMixin
public abstract class ModelBakeryMixin implements IExtendedModelBakery {
   @Unique
   private BlockStateModelLoader dynamicLoader;
   @Unique
   private final ReentrantLock modelBakeryLock = new ReentrantLock();
   @Unique
   private TextureGetter textureGetter;
   @Unique
   private BakedModel bakedMissingModel;
   @Unique
   private LRUMap<ModelResourceLocation, UnbakedModel> mfix$rawTopLevelModels;
   @Unique
   private final Set<ModelResourceLocation> mfix$inFlightModels = new HashSet<>();
   @Shadow
   @Final
   private UnbakedModel missingModel;
   @Unique
   private static final boolean DEBUG_MODEL_LOADS = Boolean.getBoolean("modernfix.debugDynamicModelLoading");
   @Shadow
   @Mutable
   @Final
   private Map<ModelResourceLocation, BakedModel> bakedTopLevelModels;
   @Shadow
   @Mutable
   @Final
   public Map<ModelResourceLocation, UnbakedModel> topLevelModels;
   @Shadow
   @Mutable
   @Final
   private Map<ResourceLocation, UnbakedModel> unbakedCache;
   @Shadow
   @Mutable
   @Final
   public Map<BakedCacheKey, BakedModel> bakedCache;
   @Shadow
   @Final
   public static ModelResourceLocation MISSING_MODEL_VARIANT;
   @Shadow
   @Final
   private Map<ResourceLocation, BlockModel> modelResources;
   @Shadow
   @Final
   public static FileToIdConverter MODEL_LISTER;
   private final Map<ModelResourceLocation, BakedModel> mfix$emulatedBakedRegistry = new DynamicOverridableMap<>(
      ModelResourceLocation.class, this::loadBakedModelDynamic
   );
   private boolean inInitialLoad = true;
   @Unique
   private int tickCount;
   @Unique
   private static final int MAXIMUM_CACHE_SIZE = 1000;

   @Shadow
   abstract UnbakedModel getModel(ResourceLocation var1);

   @Shadow(aliases = "lambda$bakeModels$6")
   protected abstract void method_61072(TextureGetter var1, ModelResourceLocation var2, UnbakedModel var3);

   @Shadow
   protected abstract void loadItemModelAndDependencies(ResourceLocation var1);

   @Shadow
   protected abstract void registerModelAndLoadDependencies(ModelResourceLocation var1, UnbakedModel var2);

   @Override
   public UnbakedModel mfix$loadUnbakedModelDynamic(ModelResourceLocation location) {
      if (location.equals(MISSING_MODEL_VARIANT)) {
         return this.missingModel;
      }

      this.modelBakeryLock.lock();

      try {
         UnbakedModel existing = this.mfix$rawTopLevelModels.get(location);
         if (existing != null) {
            return existing;
         }

         if (!this.mfix$inFlightModels.add(location)) {
            return null;
         }

         try {
            if (DEBUG_MODEL_LOADS) {
               ModernFix.LOGGER.info("Loading model {}", location);
            }

            if (location.variant().equals("inventory")) {
               this.loadItemModelAndDependencies(location.id());
            } else if (!location.variant().equals("fabric_resource") && !location.variant().equals("standalone")) {
               ((IBlockStateModelLoader)this.dynamicLoader).loadSpecificBlock(location);
            } else {
               if (!this.modelResources.containsKey(MODEL_LISTER.idToFile(location.id()))) {
                  return null;
               }

               UnbakedModel unbakedModel = this.getModel(location.id());
               this.registerModelAndLoadDependencies(location, unbakedModel);
            }

            return (UnbakedModel)this.mfix$rawTopLevelModels.getOrDefault(location, this.missingModel);
         } finally {
            this.mfix$inFlightModels.remove(location);
         }
      } finally {
         this.modelBakeryLock.unlock();
      }
   }

   @WrapMethod(method = "getModel")
   private UnbakedModel mfix$lockWhenGettingModel(ResourceLocation modelLocation, Operation<UnbakedModel> original) {
      this.modelBakeryLock.lock();

      try {
         return (UnbakedModel)original.call(new Object[]{modelLocation});
      } finally {
         this.modelBakeryLock.unlock();
      }
   }

   @Override
   public UnbakedModel mfix$getMissingModel() {
      return this.missingModel;
   }

   @Unique
   private BakedModel loadBakedModelDynamic(ModelResourceLocation location) {
      if (location.equals(MISSING_MODEL_VARIANT)) {
         return this.bakedMissingModel;
      }

      this.modelBakeryLock.lock();

      BakedModel model;
      try {
         model = this.bakedTopLevelModels.get(location);
         if (model == null) {
            UnbakedModel prototype = this.mfix$loadUnbakedModelDynamic(location);
            if (prototype == null) {
               return null;
            }

            if (prototype == this.missingModel) {
               model = this.bakedMissingModel;
            } else {
               prototype.resolveParents(this::getModel);
               if (DEBUG_MODEL_LOADS) {
                  ModernFix.LOGGER.info("Baking model {}", location);
               }

               this.method_61072(this.textureGetter, location, prototype);
               model = this.bakedTopLevelModels.remove(location);
               if (model == null) {
                  ModernFix.LOGGER.error("Failed to load model " + location);
                  model = this.bakedMissingModel;
               }

               for (ModernFixClientIntegration integration : ModernFixClient.CLIENT_INTEGRATIONS) {
                  try {
                     model = integration.onBakedModelLoad(location, prototype, model, BlockModelRotation.X0_Y0, (ModelBakery)this, this.textureGetter);
                  } catch (RuntimeException e) {
                     ModernFix.LOGGER.error("Exception encountered running dynamic resources integration", e);
                  }
               }
            }
         }
      } finally {
         this.modelBakeryLock.unlock();
      }

      return model;
   }

   @ModifyExpressionValue(method = "<init>", at = @At(value = "CONSTANT", args = "stringValue=missing_model"))
   private String replaceBackingMaps(String original) {
      this.unbakedCache = new LRUMap<ResourceLocation, UnbakedModel>(this.unbakedCache);
      this.bakedCache = new LRUMap<BakedCacheKey, BakedModel>(this.bakedCache);
      final LRUMap<ModelResourceLocation, UnbakedModel> rawTopLevelModels = new LRUMap<>(this.topLevelModels);
      this.mfix$rawTopLevelModels = rawTopLevelModels;
      final IExtendedModelBakery self = this;
      this.topLevelModels = new ForwardingInclDefaultsMap<ModelResourceLocation, UnbakedModel>() {
         protected Map<ModelResourceLocation, UnbakedModel> delegate() {
            return rawTopLevelModels;
         }

         public UnbakedModel get(Object key) {
            UnbakedModel model = rawTopLevelModels.get(key);
            if (model == null && key instanceof ModelResourceLocation mrl) {
               self.mfix$loadUnbakedModelDynamic(mrl);
               model = rawTopLevelModels.get(mrl);
            }

            return model;
         }

         public UnbakedModel getOrDefault(Object key, UnbakedModel defaultValue) {
            UnbakedModel model = this.get(key);
            return model != null ? model : defaultValue;
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public Object getOrDefault(Object param1, Object nullx) {
            return this.getOrDefault(var1, (UnbakedModel)nullx);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public Object get(Object param1) {
            return this.get(var1);
         }

         // $VF: synthetic method
         // $VF: bridge method
         protected Object delegate() {
            return this.delegate();
         }
      };
      this.bakedTopLevelModels = new LRUMap<ModelResourceLocation, BakedModel>(this.bakedTopLevelModels);
      return original;
   }

   @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BlockStateModelLoader;loadAllBlockStates()V"))
   private void noInitialBlockStateLoad(BlockStateModelLoader instance, Operation<Void> original) {
      this.dynamicLoader = instance;
      original.call(new Object[]{instance});
   }

   @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/DefaultedRegistry;keySet()Ljava/util/Set;"))
   private Set<?> skipLoadingItems(DefaultedRegistry instance) {
      return Collections.emptySet();
   }

   @Inject(method = "bakeModels", at = @At("HEAD"))
   private void storeTextureGetterAndBakeMissing(TextureGetter textureGetter, CallbackInfo ci) {
      this.textureGetter = textureGetter;
      this.method_61072(textureGetter, MISSING_MODEL_VARIANT, Objects.requireNonNull(this.mfix$rawTopLevelModels.get(MISSING_MODEL_VARIANT)));
      this.bakedMissingModel = this.bakedTopLevelModels.get(MISSING_MODEL_VARIANT);
   }

   @Inject(method = "bakeModels", at = @At("RETURN"))
   private void onInitialBakeFinish(TextureGetter textureGetter, CallbackInfo ci) {
      ObjectOpenHashSet<ModelResourceLocation> permanentMRLs = new ObjectOpenHashSet(this.bakedTopLevelModels.keySet());
      ((LRUMap)this.bakedTopLevelModels).setPermanentEntries(permanentMRLs);
      ModernFix.LOGGER.info("Dynamic model bakery initial baking finished, with {} permanent top level baked models", this.bakedTopLevelModels.size());
   }

   @Inject(method = "<init>", at = @At("RETURN"))
   private void onInitialLoadFinish(BlockColors blockColors, ProfilerFiller profilerFiller, Map map, Map map2, CallbackInfo ci) {
      ObjectOpenHashSet<ModelResourceLocation> permanentMRLs = new ObjectOpenHashSet(this.mfix$rawTopLevelModels.keySet());
      this.mfix$rawTopLevelModels.setPermanentEntries(permanentMRLs);
      ModernFix.LOGGER.info("Dynamic model bakery loading finished, with {} permanent top level models", this.mfix$rawTopLevelModels.size());
   }

   private void runCleanup() {
      try {
         ((LRUMap)this.unbakedCache).dropEntriesToMeetSize(1000);
      } catch (RuntimeException e) {
         throw new IllegalStateException("Exception dropping entries in unbaked cache", e);
      }

      try {
         ((LRUMap)this.bakedCache).dropEntriesToMeetSize(1000);
      } catch (RuntimeException e) {
         throw new IllegalStateException("Exception dropping entries in baked cache", e);
      }

      try {
         this.mfix$rawTopLevelModels.dropEntriesToMeetSize(1000);
      } catch (RuntimeException e) {
         throw new IllegalStateException("Exception dropping entries in top level models", e);
      }

      try {
         ((LRUMap)this.bakedTopLevelModels).dropEntriesToMeetSize(1000);
      } catch (RuntimeException e) {
         throw new IllegalStateException("Exception dropping entries in baked top level models", e);
      }
   }

   @Override
   public void mfix$finishLoading() {
      this.inInitialLoad = false;
   }

   @Override
   public void mfix$tick() {
      if (!this.inInitialLoad) {
         this.tickCount++;
         if (this.tickCount % 200 == 0 && this.modelBakeryLock.tryLock()) {
            try {
               this.runCleanup();
            } finally {
               this.modelBakeryLock.unlock();
            }
         }
      }
   }

   @Overwrite
   public Map<ModelResourceLocation, BakedModel> getBakedTopLevelModels() {
      return this.mfix$emulatedBakedRegistry;
   }

   @Override
   public ReentrantLock mfix$getLock() {
      return this.modelBakeryLock;
   }
}
