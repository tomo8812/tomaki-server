package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.core.RegistryAccess;

@FunctionalInterface
public interface ServerResourcesLoadCallback {
   EventInvoker<ServerResourcesLoadCallback> EVENT = EventInvoker.lookup(ServerResourcesLoadCallback.class);

   void onServerResourcesLoad(RegistryAccess var1);
}
