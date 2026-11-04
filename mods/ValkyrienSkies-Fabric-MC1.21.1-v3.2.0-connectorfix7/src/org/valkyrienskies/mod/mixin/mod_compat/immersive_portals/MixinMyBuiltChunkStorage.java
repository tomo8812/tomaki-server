package org.valkyrienskies.mod.mixin.mod_compat.immersive_portals;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_3532;
import net.minecraft.class_761;
import net.minecraft.class_769;
import net.minecraft.class_846;
import net.minecraft.class_846.class_851;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.mixinducks.client.render.IVSViewAreaMethods;
import qouteall.imm_ptl.core.render.ImmPtlViewArea;

@Mixin(ImmPtlViewArea.class)
public class MixinMyBuiltChunkStorage extends class_769 implements IVSViewAreaMethods {
   @Unique
   private final Long2ObjectMap<class_851[]> vs$shipRenderChunks = new Long2ObjectOpenHashMap();
   @Unique
   private class_846 vs$chunkBuilder;

   public MixinMyBuiltChunkStorage(class_846 chunkRenderDispatcher, class_1937 level, int i, class_761 levelRenderer) {
      super(chunkRenderDispatcher, level, i, levelRenderer);
   }

   @Inject(method = "<init>", at = @At("TAIL"))
   private void postInit(class_846 chunkBuilder, class_1937 world, int viewDistance, class_761 worldRenderer, CallbackInfo callbackInfo) {
      this.vs$chunkBuilder = chunkBuilder;
   }

   @Inject(method = "setDirty", at = @At("HEAD"), cancellable = true)
   private void preScheduleRebuild(int x, int y, int z, boolean important, CallbackInfo callbackInfo) {
      int yIndex = y - this.field_4151.method_32891();
      if (yIndex >= 0 && yIndex < this.field_4149) {
         if (VSGameUtilsKt.isChunkInShipyard(this.field_4151, x, z)) {
            long chunkPosAsLong = class_1923.method_8331(x, z);
            class_851[] renderChunksArray = (class_851[])this.vs$shipRenderChunks.computeIfAbsent(chunkPosAsLong, k -> new class_851[this.field_4149]);
            if (renderChunksArray[yIndex] == null) {
               class_851 builtChunk = new class_851(this.vs$chunkBuilder, 0, x << 4, y << 4, z << 4);
               renderChunksArray[yIndex] = builtChunk;
            }

            renderChunksArray[yIndex].method_3654(important);
            callbackInfo.cancel();
         }
      }
   }

   @Inject(method = "provideBuiltChunkByChunkPos", at = @At("HEAD"), cancellable = true)
   private void preProvideBuiltChunkByChunkPos(int cx, int cy, int cz, CallbackInfoReturnable<class_851> callbackInfoReturnable) {
      int chunkX = class_3532.method_48116(cx, 16);
      int chunkY = class_3532.method_48116(cy - this.field_4151.method_31607(), 16);
      int chunkZ = class_3532.method_48116(cz, 16);
      if (chunkY >= 0 && chunkY < this.field_4149) {
         if (VSGameUtilsKt.isChunkInShipyard(this.field_4151, chunkX, chunkZ)) {
            long chunkPosAsLong = class_1923.method_8331(chunkX, chunkZ);
            class_851[] renderChunksArray = (class_851[])this.vs$shipRenderChunks.get(chunkPosAsLong);
            if (renderChunksArray == null) {
               callbackInfoReturnable.setReturnValue(null);
               return;
            }

            class_851 renderChunk = renderChunksArray[chunkY];
            callbackInfoReturnable.setReturnValue(renderChunk);
         }
      }
   }

   @Override
   public class_851 vs$getShipRenderSection(int chunkX, int sectionY, int chunkZ) {
      int yIndex = sectionY - this.field_4151.method_32891();
      if (yIndex >= 0 && yIndex < this.field_4149) {
         class_851[] arr = (class_851[])this.vs$shipRenderChunks.get(class_1923.method_8331(chunkX, chunkZ));
         return arr != null ? arr[yIndex] : null;
      } else {
         return null;
      }
   }

   @Override
   public class_851 vs$getOrCreateShipRenderSection(int chunkX, int sectionY, int chunkZ) {
      int yIndex = sectionY - this.field_4151.method_32891();
      if (yIndex >= 0 && yIndex < this.field_4149) {
         long key = class_1923.method_8331(chunkX, chunkZ);
         class_851[] arr = (class_851[])this.vs$shipRenderChunks.computeIfAbsent(key, k -> new class_851[this.field_4149]);
         if (arr[yIndex] == null) {
            arr[yIndex] = new class_851(this.vs$chunkBuilder, 0, chunkX << 4, sectionY << 4, chunkZ << 4);
         }

         arr[yIndex].method_3654(true);
         return arr[yIndex];
      } else {
         return null;
      }
   }

   @Override
   public void valkyrienskies$unloadChunk(int chunkX, int chunkZ) {
      if (VSGameUtilsKt.isChunkInShipyard(this.field_4151, chunkX, chunkZ)) {
         class_851[] chunks = (class_851[])this.vs$shipRenderChunks.remove(class_1923.method_8331(chunkX, chunkZ));
         if (chunks != null) {
            for (class_851 chunk : chunks) {
               if (chunk != null) {
                  chunk.method_3659();
               }
            }
         }
      }
   }

   @Inject(method = "releaseAllBuffers", at = @At("HEAD"))
   private void postReleaseAllBuffers(CallbackInfo ci) {
      ObjectIterator var2 = this.vs$shipRenderChunks.long2ObjectEntrySet().iterator();

      while (var2.hasNext()) {
         Entry<class_851[]> entry = (Entry<class_851[]>)var2.next();

         for (class_851 renderChunk : (class_851[])entry.getValue()) {
            if (renderChunk != null) {
               renderChunk.method_3659();
            }
         }
      }

      this.vs$shipRenderChunks.clear();
   }
}
