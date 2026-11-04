package org.valkyrienskies.mod.mixin.accessors.server.level;

import net.minecraft.class_3215;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_3215.class)
public interface ServerChunkCacheAccessor {
   @Invoker("clearCache")
   void callClearCache();

   @Invoker("runDistanceManagerUpdates")
   boolean callRunDistanceManagerUpdates();
}
