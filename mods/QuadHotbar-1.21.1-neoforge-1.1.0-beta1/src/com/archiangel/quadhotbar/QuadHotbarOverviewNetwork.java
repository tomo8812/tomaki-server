package com.archiangel.quadhotbar;

import com.archiangel.quadhotbar.client.QuadHotbarOverviewScreen;
import com.archiangel.quadhotbar.client.QuadHotbarOverviewSizing;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class QuadHotbarOverviewNetwork {
   private QuadHotbarOverviewNetwork() {
   }

   public static void register(PayloadRegistrar registrar) {
      registrar.playToServer(QuadHotbarOverviewNetwork.Request.TYPE, QuadHotbarOverviewNetwork.Request.CODEC, (request, context) -> context.enqueueWork(() -> {
         if (context.player() instanceof ServerPlayer player) {
            handle(player, request);
         }
      }));
      registrar.playToClient(
         QuadHotbarOverviewNetwork.ReturnToInventory.TYPE,
         QuadHotbarOverviewNetwork.ReturnToInventory.CODEC,
         (payload, context) -> context.enqueueWork(() -> QuadHotbarOverviewScreen.returnToInventory())
      );
   }

   public static void turnPage(int direction) {
      send(0, direction);
   }

   public static void open() {
      send(1, 0);
   }

   public static void returnToInventory() {
      send(2, 0);
   }

   public static void syncCreativeCursor() {
      send(3, 0);
   }

   private static void send(int action, int direction) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         ItemStack creativeCursor = player.hasInfiniteMaterials() ? player.inventoryMenu.getCarried().copy() : ItemStack.EMPTY;
         PacketDistributor.sendToServer(
            new QuadHotbarOverviewNetwork.Request(
               action,
               direction,
               QuadHotbarConfig.standaloneOverview,
               QuadHotbarOverviewSizing.effectiveWidth(),
               QuadHotbarConfig.overviewHeight,
               creativeCursor
            ),
            new CustomPacketPayload[0]
         );
      }
   }

   private static void handle(ServerPlayer player, QuadHotbarOverviewNetwork.Request request) {
      if (request.action == 2) {
         if (player.containerMenu instanceof QuadHotbarOverviewMenu menu) {
            acceptCreativeCursor(player, request);
            menu.returningToInventory = true;
            ItemStack var12 = menu.getCarried();
            menu.setCarried(ItemStack.EMPTY);
            player.closeContainer();
            player.inventoryMenu.setCarried(var12);
            player.inventoryMenu.broadcastFullState();
            QuadHotbarNetwork.sendPageSync(player);
            PacketDistributor.sendToPlayer(player, new QuadHotbarOverviewNetwork.ReturnToInventory(), new CustomPacketPayload[0]);
         }
      } else if (player.isAlive() && !player.isSpectator()) {
         if (request.action == 3) {
            if (player.containerMenu instanceof QuadHotbarOverviewMenu) {
               acceptCreativeCursor(player, request);
            }
         } else {
            boolean docked = player.containerMenu instanceof QuadHotbarOverviewMenu menu && !menu.standalone;
            if (player.containerMenu == player.inventoryMenu || docked && request.action == 0) {
               QuadHotbarNetwork.enforceServerLimits(player);
               if (QuadHotbarServerConfig.forPlayer(player).allowFreedom()) {
                  if (request.action == 0 || request.action == 1) {
                     if (request.action != 0 || request.direction == -1 || request.direction == 1) {
                        if (request.action != 1 || QuadHotbarServerConfig.forPlayer(player).allowPageOverview()) {
                           acceptCreativeCursor(player, request);
                           if (request.action == 0) {
                              QuadHotbarNetwork.turnInventoryPage(player, request.direction);
                           } else {
                              QuadHotbarPages.State state = QuadHotbarPages.state(player);

                              boolean var14 = switch (QuadHotbarServerConfig.forPlayer(player).overviewLayout()) {
                                 case DOCKED -> false;
                                 case STANDALONE -> true;
                                 case CLIENT -> request.standalone;
                              };
                              boolean standalone = var14;
                              int width = Math.min(
                                 QuadHotbarOverviewLayout.normalizeWidth(request.width), QuadHotbarServerConfig.forPlayer(player).maxOverviewWidth()
                              );
                              int height = Math.max(1, Math.min(request.height, QuadHotbarServerConfig.forPlayer(player).maxOverviewHeight()));
                              ItemStack cursor = player.inventoryMenu.getCarried();
                              OptionalInt opened = player.openMenu(
                                 new SimpleMenuProvider(
                                    (id, inventory, owner) -> {
                                       QuadHotbarOverviewMenu menu = new QuadHotbarOverviewMenu(
                                          id, inventory, state.pageCount, state.activePage, standalone, width, height
                                       );
                                       menu.setCarried(cursor);
                                       return menu;
                                    },
                                    Component.translatable("quadhotbar.overview.title")
                                 ),
                                 data -> {
                                    data.writeVarInt(state.pageCount);
                                    data.writeVarInt(state.activePage);
                                    data.writeBoolean(standalone);
                                    data.writeVarInt(width);
                                    data.writeVarInt(height);
                                 }
                              );
                              if (opened.isEmpty()) {
                                 player.inventoryMenu.setCarried(cursor);
                                 player.inventoryMenu.broadcastFullState();
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void acceptCreativeCursor(ServerPlayer player, QuadHotbarOverviewNetwork.Request request) {
      ItemStack stack = request.creativeCursor;
      if (player.hasInfiniteMaterials()
         && (stack.isEmpty() || stack.getCount() <= stack.getMaxStackSize() && stack.isItemEnabled(player.level().enabledFeatures()))) {
         player.inventoryMenu.setCarried(stack.copy());
      }
   }

   private record Request(int action, int direction, boolean standalone, int width, int height, ItemStack creativeCursor) implements CustomPacketPayload {
      private static final Type<QuadHotbarOverviewNetwork.Request> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("quadhotbar", "overview_request"));
      private static final StreamCodec<RegistryFriendlyByteBuf, QuadHotbarOverviewNetwork.Request> CODEC = new StreamCodec<RegistryFriendlyByteBuf, QuadHotbarOverviewNetwork.Request>() {
         public QuadHotbarOverviewNetwork.Request decode(RegistryFriendlyByteBuf buffer) {
            return new QuadHotbarOverviewNetwork.Request(
               buffer.readVarInt(),
               buffer.readVarInt(),
               buffer.readBoolean(),
               buffer.readVarInt(),
               buffer.readVarInt(),
               (ItemStack)ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer)
            );
         }

         public void encode(RegistryFriendlyByteBuf buffer, QuadHotbarOverviewNetwork.Request value) {
            buffer.writeVarInt(value.action);
            buffer.writeVarInt(value.direction);
            buffer.writeBoolean(value.standalone);
            buffer.writeVarInt(value.width);
            buffer.writeVarInt(value.height);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, value.creativeCursor);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public Object decode(Object param1) {
            return this.decode((RegistryFriendlyByteBuf)var1);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public void encode(Object param1, Object nullx) {
            this.encode((RegistryFriendlyByteBuf)var1, (QuadHotbarOverviewNetwork.Request)nullx);
         }
      };

      public Type<QuadHotbarOverviewNetwork.Request> type() {
         return TYPE;
      }
   }

   private record ReturnToInventory() implements CustomPacketPayload {
      private static final Type<QuadHotbarOverviewNetwork.ReturnToInventory> TYPE = new Type(
         ResourceLocation.fromNamespaceAndPath("quadhotbar", "overview_return")
      );
      private static final StreamCodec<RegistryFriendlyByteBuf, QuadHotbarOverviewNetwork.ReturnToInventory> CODEC = new StreamCodec<RegistryFriendlyByteBuf, QuadHotbarOverviewNetwork.ReturnToInventory>() {
         public QuadHotbarOverviewNetwork.ReturnToInventory decode(RegistryFriendlyByteBuf buffer) {
            return new QuadHotbarOverviewNetwork.ReturnToInventory();
         }

         public void encode(RegistryFriendlyByteBuf buffer, QuadHotbarOverviewNetwork.ReturnToInventory value) {
         }

         // $VF: synthetic method
         // $VF: bridge method
         public Object decode(Object param1) {
            return this.decode((RegistryFriendlyByteBuf)var1);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public void encode(Object param1, Object nullx) {
            this.encode((RegistryFriendlyByteBuf)var1, (QuadHotbarOverviewNetwork.ReturnToInventory)nullx);
         }
      };

      public Type<QuadHotbarOverviewNetwork.ReturnToInventory> type() {
         return TYPE;
      }
   }
}
