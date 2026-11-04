package dev.terminalmc.clientsort.mixin.accessor;

import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Container.class)
public interface ContainerAccessor {
   @Invoker("setChanged")
   void clientsort$setChanged();
}
