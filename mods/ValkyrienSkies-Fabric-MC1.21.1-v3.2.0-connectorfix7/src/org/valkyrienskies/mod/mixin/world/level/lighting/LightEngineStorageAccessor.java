package org.valkyrienskies.mod.mixin.world.level.lighting;

import net.minecraft.class_3558;
import net.minecraft.class_3560;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_3558.class)
public interface LightEngineStorageAccessor {
   @Accessor("storage")
   class_3560 vs$getStorage();
}
