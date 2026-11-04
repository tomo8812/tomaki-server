package org.valkyrienskies.mod.mixin.accessors.network.protocol.game;

import net.minecraft.class_2637;
import net.minecraft.class_4076;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_2637.class)
public interface ClientboundSectionBlocksUpdatePacketAccessor {
   @Accessor
   class_4076 getSectionPos();
}
