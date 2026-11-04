package org.embeddedt.modernfix.common.mixin.perf.resourcepacks;

import java.io.File;
import java.util.zip.ZipFile;
import net.minecraft.server.packs.FilePackResources.SharedZipFileAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SharedZipFileAccess.class)
public interface SharedZipFileAccessAccessor {
   @Invoker("getOrCreateZipFile")
   ZipFile mfix$getOrCreateZipFile();

   @Accessor("file")
   File mfix$getFile();
}
