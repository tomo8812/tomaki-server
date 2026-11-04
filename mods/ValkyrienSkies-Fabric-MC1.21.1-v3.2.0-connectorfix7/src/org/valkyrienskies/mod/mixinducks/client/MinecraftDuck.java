package org.valkyrienskies.mod.mixinducks.client;

import net.minecraft.class_239;
import org.valkyrienskies.mod.common.IShipObjectWorldClientProvider;

public interface MinecraftDuck extends IShipObjectWorldClientProvider {
   void vs$setOriginalCrosshairTarget(class_239 var1);

   class_239 vs$getOriginalCrosshairTarget();
}
