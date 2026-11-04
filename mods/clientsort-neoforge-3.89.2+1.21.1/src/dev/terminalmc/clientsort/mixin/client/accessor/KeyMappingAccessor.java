package dev.terminalmc.clientsort.mixin.client.accessor;

import com.mojang.blaze3d.platform.InputConstants.Key;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {
   @Accessor("ALL")
   static Map<String, KeyMapping> clientsort$getAll() {
      throw new UnsupportedOperationException();
   }

   @Accessor("key")
   Key clientsort$getKey();
}
