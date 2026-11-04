package net.caffeinemc.mods.lithium.mixin.block.hopper;

import java.util.List;
import net.minecraft.core.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NonNullList.class)
public interface NonNullListAccessor<T> {
   @Accessor("list")
   List<T> getDelegate();
}
