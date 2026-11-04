package org.valkyrienskies.mod.mixin.accessors.network;

import io.netty.channel.Channel;
import net.minecraft.class_2535;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_2535.class)
public interface ConnectionAccessor {
   @Accessor("channel")
   Channel valkyrienskies$getChannel();
}
