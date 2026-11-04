package de.maxhenkel.gravestone.net;

import de.maxhenkel.gravestone.corelib.death.Death;
import de.maxhenkel.gravestone.corelib.net.Message;
import de.maxhenkel.gravestone.gui.ObituaryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageOpenObituary implements Message<MessageOpenObituary> {
   public static final Type<MessageOpenObituary> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("gravestone", "open_obituary"));
   private Death death;

   public MessageOpenObituary() {
   }

   public MessageOpenObituary(Death death) {
      this.death = death;
   }

   @Override
   public PacketFlow getExecutingSide() {
      return PacketFlow.CLIENTBOUND;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void executeClientSide(IPayloadContext context) {
      Minecraft.getInstance().setScreen(new ObituaryScreen(this.death));
   }

   public MessageOpenObituary fromBytes(RegistryFriendlyByteBuf buf) {
      this.death = Death.fromNBT(buf.registryAccess(), buf.readNbt());
      return this;
   }

   @Override
   public void toBytes(RegistryFriendlyByteBuf buf) {
      buf.writeNbt(this.death.toNBT(buf.registryAccess()));
   }

   @Override
   public Type<MessageOpenObituary> type() {
      return TYPE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Message fromBytes(RegistryFriendlyByteBuf param1) {
      return this.fromBytes(var1);
   }
}
