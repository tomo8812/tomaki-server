package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.core.RegistryAccess;

@FunctionalInterface
public interface ClientTagsUpdatedCallback {
   EventInvoker<ClientTagsUpdatedCallback> EVENT = EventInvoker.lookup(ClientTagsUpdatedCallback.class);

   void onClientTagsUpdated(RegistryAccess var1);
}
