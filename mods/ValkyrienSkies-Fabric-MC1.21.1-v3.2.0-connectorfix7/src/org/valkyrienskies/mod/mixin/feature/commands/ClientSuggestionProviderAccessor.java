package org.valkyrienskies.mod.mixin.feature.commands;

import net.minecraft.class_310;
import net.minecraft.class_637;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_637.class)
public interface ClientSuggestionProviderAccessor {
   @Accessor
   class_310 getMinecraft();
}
