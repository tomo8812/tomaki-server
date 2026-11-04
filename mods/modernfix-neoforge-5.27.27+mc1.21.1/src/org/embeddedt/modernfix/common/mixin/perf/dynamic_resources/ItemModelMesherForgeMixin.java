package org.embeddedt.modernfix.common.mixin.perf.dynamic_resources;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.RegistryAwareItemModelShaper;
import org.embeddedt.modernfix.annotation.ClientOnlyMixin;
import org.embeddedt.modernfix.dynamicresources.DynamicModelCache;
import org.embeddedt.modernfix.dynamicresources.ModelLocationCache;
import org.embeddedt.modernfix.util.ItemMesherMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegistryAwareItemModelShaper.class)
@ClientOnlyMixin
public abstract class ItemModelMesherForgeMixin extends ItemModelShaper {
   @Shadow(remap = false)
   @Final
   @Mutable
   private Map<Item, ModelResourceLocation> locations;
   private Map<Item, ModelResourceLocation> overrideLocations;
   private final DynamicModelCache<Item> mfix$modelCache = new DynamicModelCache<>(k -> this.mfix$getModelSlow((Item)k), true);
   private static final ModelResourceLocation SENTINEL = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("modernfix", "sentinel"), "sentinel");

   public ItemModelMesherForgeMixin(ModelManager arg) {
      super(arg);
   }

   @Inject(method = "<init>", at = @At("RETURN"))
   private void replaceLocationMap(CallbackInfo ci) {
      this.overrideLocations = new HashMap<>();
      this.locations = new ItemMesherMap<>(this::mfix$getLocationForge);
   }

   @Unique
   private ModelResourceLocation mfix$getLocationForge(Item item) {
      ModelResourceLocation map = this.overrideLocations.getOrDefault(item, SENTINEL);
      if (map == SENTINEL) {
         map = ModelLocationCache.get(item);
      }

      return map;
   }

   private BakedModel mfix$getModelSlow(Item key) {
      ModelResourceLocation map = this.mfix$getLocationForge(key);
      return map == null ? null : this.getModelManager().getModel(map);
   }

   @Overwrite
   public BakedModel getItemModel(Item item) {
      return this.mfix$modelCache.get(item);
   }

   @Overwrite
   public void register(Item item, ModelResourceLocation location) {
      this.overrideLocations.put(item, location);
   }

   @Overwrite
   public void rebuildCache() {
      this.mfix$modelCache.clear();
   }
}
