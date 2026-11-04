package org.embeddedt.modernfix.common.mixin.perf.faster_texture_stitching;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.StitcherException;
import net.minecraft.client.renderer.texture.Stitcher.Entry;
import net.minecraft.client.renderer.texture.Stitcher.Holder;
import net.minecraft.client.renderer.texture.Stitcher.SpriteLoader;
import org.embeddedt.modernfix.ModernFix;
import org.embeddedt.modernfix.annotation.ClientOnlyMixin;
import org.embeddedt.modernfix.platform.ModernFixPlatformHooks;
import org.embeddedt.modernfix.textures.StbStitcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Stitcher.class)
@ClientOnlyMixin
public class StitcherMixin<T extends Entry> {
   @Shadow
   @Final
   private List<Holder<T>> texturesToBeStitched;
   @Shadow
   private int storageX;
   @Shadow
   private int storageY;
   @Shadow
   @Final
   private int maxWidth;
   @Shadow
   @Final
   private int maxHeight;
   @Shadow
   @Final
   private static Comparator<Holder<?>> HOLDER_COMPARATOR;
   private List<StbStitcher.LoadableSpriteInfo<T>> loadableSpriteInfos;

   @Inject(method = "stitch", at = @At("HEAD"), cancellable = true)
   private void stitchFast(CallbackInfo ci) {
      this.loadableSpriteInfos = null;
      if (!ModernFixPlatformHooks.INSTANCE.isLoadingNormally()) {
         ModernFix.LOGGER.error("Using vanilla stitcher implementation due to invalid loading state");
      } else if (this.texturesToBeStitched.size() >= 100) {
         ci.cancel();
         ObjectArrayList<Holder<T>> holderList = new ObjectArrayList(this.texturesToBeStitched);
         holderList.sort(HOLDER_COMPARATOR);
         Holder<T>[] aholder = (Holder<T>[])holderList.toArray(new Holder[0]);
         Pair<Pair<Integer, Integer>, List<StbStitcher.LoadableSpriteInfo<T>>> packingInfo = StbStitcher.packRects(aholder);
         this.storageX = (Integer)((Pair)packingInfo.getFirst()).getFirst();
         this.storageY = (Integer)((Pair)packingInfo.getFirst()).getSecond();
         if (this.storageX <= this.maxWidth && this.storageY <= this.maxHeight) {
            this.loadableSpriteInfos = (List<StbStitcher.LoadableSpriteInfo<T>>)packingInfo.getSecond();
         } else {
            ModernFix.LOGGER.error("Requested atlas size {}x{} exceeds maximum of {}x{}", this.storageX, this.storageY, this.maxWidth, this.maxHeight);
            throw new StitcherException(aholder[0].entry(), Stream.of(aholder).map(arg -> arg.entry()).collect(ImmutableList.toImmutableList()));
         }
      }
   }

   @Inject(method = "gatherSprites", at = @At("HEAD"), cancellable = true)
   private void gatherSpritesFast(SpriteLoader<T> spriteLoader, CallbackInfo ci) {
      if (this.loadableSpriteInfos != null) {
         ci.cancel();

         for (StbStitcher.LoadableSpriteInfo<T> info : this.loadableSpriteInfos) {
            spriteLoader.load(info.info, info.x, info.y);
         }
      }
   }
}
