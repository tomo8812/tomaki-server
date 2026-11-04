package fuzs.puzzleslib.neoforge.impl.attachment.builder;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;

public final class NeoForgeLevelDataAttachmentBuilder<V> extends NeoForgeDataAttachmentBuilder<Level, V> implements DataAttachmentRegistry.Builder<Level, V> {
   protected RegistryAccess getRegistryAccess(Level holder) {
      return holder.registryAccess();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected RegistryAccess getRegistryAccess(Object param1) {
      return this.getRegistryAccess((Level)var1);
   }
}
