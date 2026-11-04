package org.embeddedt.modernfix.common.mixin.perf.resourcepacks;

import java.io.IOException;
import java.util.Set;
import java.util.zip.ZipFile;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.FilePackResources.SharedZipFileAccess;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import org.embeddedt.modernfix.ModernFix;
import org.embeddedt.modernfix.annotation.FeatureLevel;
import org.embeddedt.modernfix.annotation.RequiresFeatureLevel;
import org.embeddedt.modernfix.resources.ZipPackIndex;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FilePackResources.class)
@RequiresFeatureLevel(FeatureLevel.BETA)
public class FilePackResourcesMixin {
   @Shadow
   @Final
   private SharedZipFileAccess zipFileAccess;
   @Unique
   @Nullable
   private volatile ZipPackIndex mf$packIndex;

   @Unique
   @Nullable
   private ZipPackIndex mf$getOrCreateIndex() {
      ZipPackIndex index = this.mf$packIndex;
      if (index == null) {
         synchronized (this) {
            index = this.mf$packIndex;
            if (index == null) {
               SharedZipFileAccessAccessor access = (SharedZipFileAccessAccessor)this.zipFileAccess;
               if (access.mfix$getOrCreateZipFile() == null) {
                  return null;
               }

               try {
                  this.mf$packIndex = index = new ZipPackIndex(access.mfix$getFile().toPath());
               } catch (IOException e) {
                  ModernFix.LOGGER.error("Failed to build zip index for {}", access.mfix$getFile(), e);
               }
            }
         }
      }

      return index;
   }

   @Inject(method = "getNamespaces", at = @At("HEAD"), cancellable = true)
   private void mf$getNamespaces(PackType type, CallbackInfoReturnable<Set<String>> cir) {
      ZipPackIndex index = this.mf$getOrCreateIndex();
      if (index != null) {
         cir.setReturnValue(index.getNamespaces(type));
      }
   }

   @Inject(method = "listResources", at = @At("HEAD"), cancellable = true)
   private void mf$listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput, CallbackInfo ci) {
      ZipFile zf = ((SharedZipFileAccessAccessor)this.zipFileAccess).mfix$getOrCreateZipFile();
      ZipPackIndex index = this.mf$getOrCreateIndex();
      if (index != null && zf != null) {
         index.listResources(packType, namespace, path, zf, resourceOutput);
         ci.cancel();
      }
   }

   @Inject(method = "close", at = @At("HEAD"))
   private void mf$invalidateIndex(CallbackInfo ci) {
      this.mf$packIndex = null;
   }
}
