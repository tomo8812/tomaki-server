package org.valkyrienskies.mod.mixin.accessors.client.world;

import net.minecraft.class_631.class_3681;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_3681.class)
public interface ClientChunkCacheStorageAccessor {
   @Invoker("inRange")
   boolean callInRange(int var1, int var2);

   @Accessor("chunkCount")
   int getChunkCount();

   @Accessor("chunkCount")
   void setChunkCount(int var1);
}
