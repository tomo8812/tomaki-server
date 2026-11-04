package com.kishku7.chunksmith.mixin;

import net.minecraft.server.level.DistanceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DistanceManager.class)
public interface DistanceManagerPurgeInvoker {
   @Invoker("purgeStaleTickets")
   void chunksmith$invokePurgeStaleTickets();
}
