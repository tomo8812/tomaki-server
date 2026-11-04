package com.archiangel.quadhotbar;

import com.archiangel.quadhotbar.client.QuadHotbarClientEvents;
import io.netty.buffer.ByteBuf;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class QuadHotbarPolicyNetwork {
   private static final Map<ServerPlayer, QuadHotbarPolicy> SENT = new WeakHashMap<>();

   private QuadHotbarPolicyNetwork() {
   }

   public static void register(PayloadRegistrar registrar) {
      registrar.playToClient(
         QuadHotbarPolicyNetwork.Payload.TYPE,
         QuadHotbarPolicyNetwork.Payload.CODEC,
         (payload, context) -> context.enqueueWork(() -> QuadHotbarClientEvents.setServerPolicy(payload.policy))
      );
   }

   public static boolean sync(ServerPlayer player) {
      QuadHotbarPolicy policy = QuadHotbarServerConfig.forPlayer(player);
      if (policy.equals(SENT.get(player))) {
         return false;
      }

      SENT.put(player, policy);
      PacketDistributor.sendToPlayer(player, new QuadHotbarPolicyNetwork.Payload(policy), new CustomPacketPayload[0]);
      return true;
   }

   public static void forget(ServerPlayer player) {
      SENT.remove(player);
   }

   private record Payload(QuadHotbarPolicy policy) implements CustomPacketPayload {
      private static final Type<QuadHotbarPolicyNetwork.Payload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("quadhotbar", "player_policy"));
      private static final StreamCodec<ByteBuf, QuadHotbarPolicyNetwork.Payload> CODEC = new StreamCodec<ByteBuf, QuadHotbarPolicyNetwork.Payload>() {
         public QuadHotbarPolicyNetwork.Payload decode(ByteBuf buffer) {
            return new QuadHotbarPolicyNetwork.Payload(
               new QuadHotbarPolicy(
                  buffer.readBoolean(),
                  buffer.readBoolean(),
                  buffer.readBoolean(),
                  buffer.readBoolean(),
                  QuadHotbarServerConfig.OverviewLayout.values()[ByteBufCodecs.VAR_INT.decode(buffer)],
                  (Integer)ByteBufCodecs.VAR_INT.decode(buffer),
                  (Integer)ByteBufCodecs.VAR_INT.decode(buffer),
                  (Integer)ByteBufCodecs.VAR_INT.decode(buffer),
                  (Integer)ByteBufCodecs.VAR_INT.decode(buffer)
               )
            );
         }

         public void encode(ByteBuf buffer, QuadHotbarPolicyNetwork.Payload payload) {
            QuadHotbarPolicy p = payload.policy;
            buffer.writeBoolean(p.allowFreedom());
            buffer.writeBoolean(p.overviewAllowed());
            buffer.writeBoolean(p.showOverviewButton());
            buffer.writeBoolean(p.containerOverviewAllowed());
            ByteBufCodecs.VAR_INT.encode(buffer, p.overviewLayout().ordinal());
            ByteBufCodecs.VAR_INT.encode(buffer, p.pageLimit());
            ByteBufCodecs.VAR_INT.encode(buffer, p.rowLimit());
            ByteBufCodecs.VAR_INT.encode(buffer, p.maxOverviewWidth());
            ByteBufCodecs.VAR_INT.encode(buffer, p.maxOverviewHeight());
         }

         // $VF: synthetic method
         // $VF: bridge method
         public Object decode(Object param1) {
            return this.decode((ByteBuf)var1);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public void encode(Object param1, Object nullx) {
            this.encode((ByteBuf)var1, (QuadHotbarPolicyNetwork.Payload)nullx);
         }
      };

      public Type<QuadHotbarPolicyNetwork.Payload> type() {
         return TYPE;
      }
   }
}
