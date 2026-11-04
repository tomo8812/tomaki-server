package net.caffeinemc.mods.lithium.mixin.alloc.entity_iteration;

import java.util.List;
import net.minecraft.util.ClassInstanceMultiMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClassInstanceMultiMap.class)
public interface ClassInstanceMultiMapAccessor<T> {
   @Accessor("allInstances")
   List<T> getAllInstances();
}
