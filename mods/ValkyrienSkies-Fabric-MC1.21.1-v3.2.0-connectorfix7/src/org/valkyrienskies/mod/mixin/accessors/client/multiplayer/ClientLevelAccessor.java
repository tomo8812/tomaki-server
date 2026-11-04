package org.valkyrienskies.mod.mixin.accessors.client.multiplayer;

import net.minecraft.class_638;
import net.minecraft.class_761;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_638.class)
public interface ClientLevelAccessor {
   @Accessor
   class_761 getLevelRenderer();
}
