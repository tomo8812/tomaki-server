package org.valkyrienskies.mod.mixin.accessors.server.level;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.class_3204;
import net.minecraft.class_3228;
import net.minecraft.class_4706;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_3204.class)
public interface DistanceManagerAccessor {
   @Accessor("tickets")
   Long2ObjectOpenHashMap<class_4706<class_3228<?>>> getTickets();
}
