package org.embeddedt.modernfix.api.entrypoint;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.ModelBakery.TextureGetter;

public interface ModernFixClientIntegration {
   default void onDynamicResourcesStatusChange(boolean enabled) {
   }

   default BakedModel onBakedModelLoad(
      ModelResourceLocation location, UnbakedModel baseModel, BakedModel originalModel, ModelState state, ModelBakery bakery, TextureGetter textureGetter
   ) {
      return originalModel;
   }
}
