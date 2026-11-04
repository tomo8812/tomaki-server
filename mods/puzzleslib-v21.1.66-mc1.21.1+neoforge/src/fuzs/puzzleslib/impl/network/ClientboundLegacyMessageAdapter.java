package fuzs.puzzleslib.impl.network;

import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.ClientMessageListener;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

public record ClientboundLegacyMessageAdapter<T extends MessageV2<T>>(T message) implements ClientboundMessage<T> {
   public ClientMessageListener<T> getHandler() {
      return new ClientMessageListener<T>() {
         public void handle(T message, Minecraft minecraft, ClientPacketListener handler, LocalPlayer player, ClientLevel level) {
            message.makeHandler().handle(message, player, minecraft);
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public void handle(Object param1, Minecraft nullx, ClientPacketListener nullxx, LocalPlayer nullxxx, ClientLevel nullxxxx) {
            this.handle((T)var1, nullx, nullxx, nullxxx, nullxxxx);
         }
      };
   }

   public T unwrap() {
      return this.message;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object unwrap() {
      return this.unwrap();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object getHandler() {
      return this.getHandler();
   }
}
