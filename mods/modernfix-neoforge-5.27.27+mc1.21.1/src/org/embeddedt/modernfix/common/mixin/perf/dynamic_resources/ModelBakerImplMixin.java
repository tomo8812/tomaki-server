package org.embeddedt.modernfix.common.mixin.perf.dynamic_resources;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.ModelBakery.ModelBakerImpl;
import net.minecraft.resources.ResourceLocation;
import org.embeddedt.modernfix.ModernFix;
import org.embeddedt.modernfix.annotation.ClientOnlyMixin;
import org.embeddedt.modernfix.duck.IExtendedModelBakery;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelBakerImpl.class)
@ClientOnlyMixin
public abstract class ModelBakerImplMixin {
   @Shadow(aliases = "this$0")
   @Final
   private ModelBakery field_40571;
   @Unique
   private int mfix$getDepth = 0;

   @Shadow
   public abstract UnbakedModel getModel(ResourceLocation var1);

   @ModifyReturnValue(method = "getModel", at = @At("RETURN"))
   private UnbakedModel resolveParents(UnbakedModel model) {
      this.mfix$getDepth++;
      if (this.mfix$getDepth == 1) {
         try {
            model.resolveParents(this::getModel);
         } catch (Exception e) {
            ModernFix.LOGGER.warn("Exception encountered resolving parents", e);
         }
      }

      this.mfix$getDepth--;
      return model;
   }

   @WrapMethod(
      method = "bake(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/model/ModelState;)Lnet/minecraft/client/resources/model/BakedModel;"
   )
   private BakedModel mfix$lockWhenBaking(ResourceLocation location, ModelState transform, Operation<BakedModel> original) {
      ReentrantLock lock = ((IExtendedModelBakery)this.field_40571).mfix$getLock();
      lock.lock();

      try {
         return (BakedModel)original.call(new Object[]{location, transform});
      } finally {
         lock.unlock();
      }
   }

   @Overwrite(remap = false)
   public UnbakedModel getTopLevelModel(ModelResourceLocation location) {
      IExtendedModelBakery bakery = (IExtendedModelBakery)this.field_40571;
      UnbakedModel model = bakery.mfix$loadUnbakedModelDynamic(location);
      return model == bakery.mfix$getMissingModel() ? null : model;
   }

   @WrapMethod(
      method = "bake(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/model/ModelState;Ljava/util/function/Function;)Lnet/minecraft/client/resources/model/BakedModel;",
      remap = false
   )
   private BakedModel mfix$lockWhenBaking(
      ResourceLocation location, ModelState transform, Function<Material, TextureAtlasSprite> textureGetter, Operation<BakedModel> original
   ) {
      ReentrantLock lock = ((IExtendedModelBakery)this.field_40571).mfix$getLock();
      lock.lock();

      try {
         return (BakedModel)original.call(new Object[]{location, transform, textureGetter});
      } finally {
         lock.unlock();
      }
   }
}
