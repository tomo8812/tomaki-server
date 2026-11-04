package org.embeddedt.modernfix.duck.suspend_integrated_server_during_load;

import net.minecraft.resources.ResourceLocation;

public interface IDeferrableIntegratedServer {
   ResourceLocation CLIENT_LOAD_SENTINEL = ResourceLocation.fromNamespaceAndPath("modernfix", "mark_client_load_finished");

   void mfix$markClientLoadFinished();
}
