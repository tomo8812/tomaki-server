package org.embeddedt.modernfix.common.mixin.perf.dynamic_resources.ctm;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.ModelBakery.ModelBakerImpl;
import net.minecraft.client.resources.model.ModelBakery.TextureGetter;
import net.minecraft.resources.ResourceLocation;
import org.embeddedt.modernfix.ModernFixClient;
import org.embeddedt.modernfix.annotation.ClientOnlyMixin;
import org.embeddedt.modernfix.annotation.RequiresMod;
import org.embeddedt.modernfix.api.entrypoint.ModernFixClientIntegration;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.chisel.ctm.CTM;
import team.chisel.ctm.client.model.AbstractCTMBakedModel;
import team.chisel.ctm.client.model.ModelCTM;
import team.chisel.ctm.client.texture.IMetadataSectionCTM;
import team.chisel.ctm.client.util.ResourceUtil;
import team.chisel.ctm.client.util.TextureMetadataHandler;

@Mixin(TextureMetadataHandler.class)
@RequiresMod("ctm")
@ClientOnlyMixin
public abstract class TextureMetadataHandlerMixin implements ModernFixClientIntegration {
   @Shadow(remap = false)
   @Final
   private Multimap<ResourceLocation, Material> scrapedTextures;

   @Shadow(remap = false)
   @Nonnull
   protected abstract BakedModel wrap(UnbakedModel var1, BakedModel var2) throws IOException;

   @Inject(method = "<init>", at = @At("RETURN"))
   private void subscribeDynamic(CallbackInfo ci) {
      ModernFixClient.CLIENT_INTEGRATIONS.add(this);
   }

   @Inject(
      method = {
            "onModelBake(Lnet/neoforged/neoforge/client/event/ModelEvent$BakingCompleted;)V",
            "onModelBake(Lnet/neoforged/neoforge/client/event/ModelEvent$ModifyBakingResult;)V"
      },
      at = @At("HEAD"),
      cancellable = true,
      remap = false
   )
   private void noIteration(CallbackInfo ci) {
      ci.cancel();
   }

   @Override
   public BakedModel onBakedModelLoad(
      ModelResourceLocation mrl, UnbakedModel rootModel, BakedModel baked, ModelState state, ModelBakery bakery, TextureGetter getter
   ) {
      if (!(baked instanceof AbstractCTMBakedModel) && !baked.isCustomRenderer()) {
         Deque<ResourceLocation> dependencies = new ArrayDeque<>();
         Set<ResourceLocation> seenModels = new HashSet<>();
         dependencies.push(mrl.id());
         seenModels.add(mrl.id());
         boolean shouldWrap = false;
         new HashSet();

         while (true) {
            ResourceLocation dep;
            UnbakedModel model;
            while (true) {
               if (shouldWrap || dependencies.isEmpty()) {
                  if (shouldWrap) {
                     try {
                        baked = this.wrap(rootModel, baked);
                        this.handleInit(mrl, baked, bakery, getter);
                        dependencies.clear();
                     } catch (IOException e) {
                        CTM.logger.error("Could not wrap model " + mrl + ". Aborting...", e);
                     }

                     return baked;
                  }

                  return baked;
               }

               dep = dependencies.pop();

               try {
                  model = dep == mrl.id() ? rootModel : bakery.getModel(dep);
                  break;
               } catch (Exception e) {
               }
            }

            Collection<Material> textures = Sets.newHashSet(this.scrapedTextures.get(dep));
            Collection<ResourceLocation> newDependencies = model.getDependencies();

            for (Material tex : textures) {
               IMetadataSectionCTM meta = null;

               try {
                  meta = (IMetadataSectionCTM)ResourceUtil.getMetadata(ResourceUtil.spriteToAbsolute(tex.texture())).orElse(null);
               } catch (IOException var20) {
               }

               if (meta != null) {
                  shouldWrap = true;
               }
            }

            for (ResourceLocation newDep : newDependencies) {
               if (seenModels.add(newDep)) {
                  dependencies.push(newDep);
               }
            }
         }
      } else {
         return baked;
      }
   }

   private void handleInit(ModelResourceLocation key, BakedModel wrappedModel, ModelBakery bakery, TextureGetter spriteGetter) {
      if (wrappedModel instanceof AbstractCTMBakedModel baked && baked.getModel() instanceof ModelCTM ctmModel && !ctmModel.isInitialized()) {
         ((CTMModelBakeryAccessor)bakery).mfix$getBakedCache().clear();
         Objects.requireNonNull(bakery);
         ModelBakerImpl baker = new ModelBakerImpl(bakery, spriteGetter, key);
         ctmModel.bake(baker, m -> spriteGetter.get(key, m), BlockModelRotation.X0_Y0);
      }
   }
}
