package com.archiangel.quadhotbar;

import com.archiangel.quadhotbar.client.QuadHotbarClientEvents;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class QuadHotbarNetwork {
   public static final String PROTOCOL = "11";

   private QuadHotbarNetwork() {
   }

   public static void registerPayloads(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("11").optional();
      QuadHotbarPolicyNetwork.register(registrar);
      QuadHotbarOverviewNetwork.register(registrar);
      QuadHotbarContainerOverview.register(registrar);
      registrar.playToClient(
         QuadHotbarNetwork.ServerSupportPayload.TYPE, QuadHotbarNetwork.ServerSupportPayload.STREAM_CODEC, QuadHotbarNetwork.ServerSupportPayload::handle
      );
      registrar.playToClient(QuadHotbarNetwork.PageSyncPayload.TYPE, QuadHotbarNetwork.PageSyncPayload.STREAM_CODEC, QuadHotbarNetwork.PageSyncPayload::handle);
      registrar.playToClient(
         QuadHotbarNetwork.SelectionSyncPayload.TYPE, QuadHotbarNetwork.SelectionSyncPayload.STREAM_CODEC, QuadHotbarNetwork.SelectionSyncPayload::handle
      );
      registrar.playToServer(
         QuadHotbarNetwork.PageRequestPayload.TYPE, QuadHotbarNetwork.PageRequestPayload.STREAM_CODEC, QuadHotbarNetwork.PageRequestPayload::handle
      );
   }

   public static void requestPageSettings(int pages, int rows) {
      PacketDistributor.sendToServer(
         new QuadHotbarNetwork.PageRequestPayload(0, pages, rows, 0, QuadHotbarConfig.inventoryRowOrder == QuadHotbarLayout.InventoryOrder.BOTTOM_TO_TOP),
         new CustomPacketPayload[0]
      );
   }

   public static void requestSlot(int row, int column, int requestId) {
      PacketDistributor.sendToServer(new QuadHotbarNetwork.PageRequestPayload(1, row, column, requestId, false), new CustomPacketPayload[0]);
   }

   public static void requestScroll(int direction, boolean currentHotbarOnly, int requestId) {
      PacketDistributor.sendToServer(
         new QuadHotbarNetwork.PageRequestPayload(4, direction, currentHotbarOnly ? 1 : 0, requestId, false), new CustomPacketPayload[0]
      );
   }

   public static void requestInventoryPage(int direction) {
      QuadHotbarOverviewNetwork.turnPage(direction);
   }

   public static void turnInventoryPage(ServerPlayer player, int direction) {
      QuadHotbarPages.State state = QuadHotbarPages.state(player);
      int selected = Mth.clamp(player.getInventory().selected, 0, 35);
      int page = Math.floorMod(state.activePage + direction, state.pageCount);
      state.hotbarPage = QuadHotbarPages.orderedRow(state, page, selected) / state.hotbarRows;
      changeActive(player, page, selected);
   }

   public static void requestHotbarPage(int direction) {
      PacketDistributor.sendToServer(new QuadHotbarNetwork.PageRequestPayload(3, direction, 0, 0, false), new CustomPacketPayload[0]);
   }

   public static void sendPageSync(ServerPlayer player) {
      sendPageSync(player, 0);
   }

   private static void sendPageSync(ServerPlayer player, int selectionRequestId) {
      QuadHotbarPolicyNetwork.sync(player);
      QuadHotbarPages.State state = QuadHotbarPages.state(player);
      int selectedRow = QuadHotbarPages.orderedRow(state, state.activePage, Mth.clamp(player.getInventory().selected, 0, 35));
      if (selectedRow < state.hotbarPage * state.hotbarRows || selectedRow >= (state.hotbarPage + 1) * state.hotbarRows) {
         state.hotbarPage = Mth.clamp(selectedRow / state.hotbarRows, 0, QuadHotbarPages.hotbarPageCount(state) - 1);
      }

      int firstPage = state.hotbarPage * state.hotbarRows / 4;
      int lastPage = (state.hotbarPage * state.hotbarRows + QuadHotbarPages.visibleRows(state) - 1) / 4;
      PacketDistributor.sendToPlayer(
         player,
         new QuadHotbarNetwork.PageSyncPayload(
            state.pageCount,
            state.hotbarRows,
            QuadHotbarPages.hotbarPageCount(state),
            state.activePage,
            state.hotbarPage,
            player.getInventory().selected,
            selectionRequestId,
            state.inventoryRowOrder == QuadHotbarLayout.InventoryOrder.BOTTOM_TO_TOP,
            QuadHotbarPages.snapshot(player, firstPage),
            QuadHotbarPages.snapshot(player, lastPage),
            QuadHotbarPages.snapshot(player, lastPage - firstPage == 2 ? firstPage + 1 : firstPage)
         ),
         new CustomPacketPayload[0]
      );
   }

   public static boolean enforceServerLimits(ServerPlayer player) {
      QuadHotbarPages.State state = QuadHotbarPages.state(player);
      QuadHotbarPages.PageSettings allowed = QuadHotbarPages.limitSettings(
         state.pageCount,
         state.hotbarRows,
         QuadHotbarServerConfig.forPlayer(player).maxInventoryPages(),
         QuadHotbarServerConfig.forPlayer(player).maxHotbarRows()
      );
      if (state.pageCount == allowed.pages()
         && state.hotbarRows == allowed.rows()
         && state.activePage < state.pageCount
         && state.hotbarPage < QuadHotbarPages.hotbarPageCount(state)) {
         return false;
      }

      applyPageSettings(player, allowed.pages(), allowed.rows());
      return true;
   }

   private static void applyPageSettings(ServerPlayer player, int requestedPages, int requestedRows) {
      QuadHotbarPages.PageSettings allowed = QuadHotbarPages.limitSettings(
         requestedPages, requestedRows, QuadHotbarServerConfig.forPlayer(player).maxInventoryPages(), QuadHotbarServerConfig.forPlayer(player).maxHotbarRows()
      );
      QuadHotbarPages.State state = QuadHotbarPages.state(player);
      if ((state.pageCount != allowed.pages() || state.hotbarRows != allowed.rows()) && player.containerMenu instanceof QuadHotbarOverviewMenu) {
         player.closeContainer();
      }

      if (state.activePage >= allowed.pages()) {
         QuadHotbarPages.switchActivePage(player, 0);
      }

      state.pageCount = allowed.pages();
      state.hotbarRows = allowed.rows();
      int selected = Mth.clamp(player.getInventory().selected, 0, 35);
      player.getInventory().selected = selected;
      int globalRow = QuadHotbarPages.orderedRow(state, state.activePage, selected);
      state.hotbarPage = Mth.clamp(globalRow / state.hotbarRows, 0, QuadHotbarPages.hotbarPageCount(state) - 1);
      player.inventoryMenu.broadcastFullState();
      sendPageSync(player);
   }

   private static void changeActive(ServerPlayer player, int page, int slot) {
      changeActive(player, page, slot, 0);
   }

   private static void changeActive(ServerPlayer player, int page, int slot, int selectionRequestId) {
      QuadHotbarPages.switchActivePage(player, page);
      player.getInventory().selected = Mth.clamp(slot, 0, 35);
      player.inventoryMenu.broadcastFullState();
      sendPageSync(player, selectionRequestId);
      if (player.containerMenu instanceof QuadHotbarOverviewMenu menu) {
         menu.activePage = QuadHotbarPages.state(player).activePage;
         menu.broadcastFullState();
      }
   }

   @EventBusSubscriber(modid = "quadhotbar")
   public static final class ForgeEvents {
      private ForgeEvents() {
      }

      @SubscribeEvent
      public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
         if (event.getEntity() instanceof ServerPlayer player) {
            QuadHotbarPolicyNetwork.forget(player);
            QuadHotbarPolicyNetwork.sync(player);
            PacketDistributor.sendToPlayer(player, new QuadHotbarNetwork.ServerSupportPayload(player.getInventory().selected), new CustomPacketPayload[0]);
            QuadHotbarPages.State state = QuadHotbarPages.state(player);
            QuadHotbarNetwork.applyPageSettings(player, state.pageCount, state.hotbarRows);
         }
      }

      @SubscribeEvent
      public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
         if (event.getEntity() instanceof ServerPlayer player) {
            QuadHotbarPolicyNetwork.forget(player);
         }
      }

      @SubscribeEvent
      public static void onPlayerTick(Post event) {
         if (event.getEntity() instanceof ServerPlayer player
            && player.tickCount % 20 == 0
            && QuadHotbarPolicyNetwork.sync(player)
            && !QuadHotbarNetwork.enforceServerLimits(player)) {
            QuadHotbarNetwork.sendPageSync(player);
         }
      }

      @SubscribeEvent
      public static void onPlayerClone(Clone event) {
         if (event.getOriginal() instanceof ServerPlayer original && event.getEntity() instanceof ServerPlayer replacement) {
            CompoundTag tag = new CompoundTag();
            QuadHotbarPages.save(original, tag);
            QuadHotbarPages.load(replacement, tag);
         }
      }

      @SubscribeEvent
      public static void onPlayerRespawn(PlayerRespawnEvent event) {
         if (event.getEntity() instanceof ServerPlayer player) {
            QuadHotbarPages.State state = QuadHotbarPages.state(player);
            QuadHotbarNetwork.applyPageSettings(player, state.pageCount, state.hotbarRows);
         }
      }
   }

   private record PageRequestPayload(int action, int first, int second, int requestId, boolean bottomToTop) implements CustomPacketPayload {
      private static final Type<QuadHotbarNetwork.PageRequestPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("quadhotbar", "page_request"));
      private static final StreamCodec<ByteBuf, QuadHotbarNetwork.PageRequestPayload> STREAM_CODEC = new StreamCodec<ByteBuf, QuadHotbarNetwork.PageRequestPayload>() {
         public QuadHotbarNetwork.PageRequestPayload decode(ByteBuf buffer) {
            return new QuadHotbarNetwork.PageRequestPayload(
               (Integer)ByteBufCodecs.VAR_INT.decode(buffer),
               (Integer)ByteBufCodecs.VAR_INT.decode(buffer),
               (Integer)ByteBufCodecs.VAR_INT.decode(buffer),
               (Integer)ByteBufCodecs.VAR_INT.decode(buffer),
               (Boolean)ByteBufCodecs.BOOL.decode(buffer)
            );
         }

         public void encode(ByteBuf buffer, QuadHotbarNetwork.PageRequestPayload payload) {
            ByteBufCodecs.VAR_INT.encode(buffer, payload.action);
            ByteBufCodecs.VAR_INT.encode(buffer, payload.first);
            ByteBufCodecs.VAR_INT.encode(buffer, payload.second);
            ByteBufCodecs.VAR_INT.encode(buffer, payload.requestId);
            ByteBufCodecs.BOOL.encode(buffer, payload.bottomToTop);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public Object decode(Object param1) {
            return this.decode((ByteBuf)var1);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public void encode(Object param1, Object nullx) {
            this.encode((ByteBuf)var1, (QuadHotbarNetwork.PageRequestPayload)nullx);
         }
      };

      private static void handle(QuadHotbarNetwork.PageRequestPayload request, IPayloadContext context) {
         context.enqueueWork(
            () -> {
               if (context.player() instanceof ServerPlayer player) {
                  if (player.containerMenu != player.inventoryMenu) {
                     QuadHotbarNetwork.sendPageSync(player, request.requestId);
                  } else {
                     QuadHotbarPages.State state = QuadHotbarPages.state(player);
                     if (request.action == 0) {
                        state.inventoryRowOrder = request.bottomToTop
                           ? QuadHotbarLayout.InventoryOrder.BOTTOM_TO_TOP
                           : QuadHotbarLayout.InventoryOrder.TOP_TO_BOTTOM;
                        QuadHotbarNetwork.applyPageSettings(player, request.first, request.second);
                     } else {
                        QuadHotbarNetwork.enforceServerLimits(player);
                        if (request.action == 1) {
                           if (request.first < 0 || request.first >= QuadHotbarPages.visibleRows(state) || request.second < 0 || request.second >= 9) {
                              QuadHotbarNetwork.sendPageSync(player, request.requestId);
                              return;
                           }

                           int page = QuadHotbarPages.pageForRow(state, request.first);
                           int slot = QuadHotbarPages.slotForRow(state, request.first, request.second);
                           if (page == state.activePage) {
                              player.getInventory().selected = slot;
                              PacketDistributor.sendToPlayer(
                                 player, new QuadHotbarNetwork.SelectionSyncPayload(slot, request.requestId), new CustomPacketPayload[0]
                              );
                           } else {
                              QuadHotbarNetwork.changeActive(player, page, slot, request.requestId);
                           }
                        } else if (request.action == 2 || request.action == 3 || request.action == 4) {
                           if (request.first != -1 && request.first != 1) {
                              return;
                           }

                           int selectedSlot = Mth.clamp(player.getInventory().selected, 0, 35);
                           int column = selectedSlot % 9;
                           if (request.action == 4) {
                              if (request.second != 0 && request.second != 1) {
                                 QuadHotbarNetwork.sendPageSync(player, request.requestId);
                                 return;
                              }

                              int selectedRow = QuadHotbarPages.orderedRow(state, state.activePage, selectedSlot);
                              state.hotbarPage = Mth.clamp(selectedRow / state.hotbarRows, 0, QuadHotbarPages.hotbarPageCount(state) - 1);
                              int visualSlot = (selectedRow - state.hotbarPage * state.hotbarRows) * 9 + column;
                              int next = request.second == 1
                                 ? visualSlot / 9 * 9 + Math.floorMod(column - request.first, 9)
                                 : Math.floorMod(visualSlot - request.first, QuadHotbarPages.visibleRows(state) * 9);
                              int page = QuadHotbarPages.pageForRow(state, next / 9);
                              int slot = QuadHotbarPages.slotForRow(state, next / 9, next % 9);
                              QuadHotbarNetwork.changeActive(player, page, slot, request.requestId);
                           } else if (request.action == 2) {
                              QuadHotbarNetwork.turnInventoryPage(player, request.first);
                           } else {
                              int selectedRow = QuadHotbarPages.orderedRow(state, state.activePage, selectedSlot);
                              int relativeRow = Mth.clamp(selectedRow - state.hotbarPage * state.hotbarRows, 0, state.hotbarRows - 1);
                              state.hotbarPage = Math.floorMod(state.hotbarPage + request.first, QuadHotbarPages.hotbarPageCount(state));
                              int nextRow = state.hotbarPage * state.hotbarRows + relativeRow;
                              QuadHotbarNetwork.changeActive(player, nextRow / 4, state.inventoryRowOrder.inventoryRow(nextRow % 4) * 9 + column);
                           }
                        }
                     }
                  }
               }
            }
         );
      }

      public Type<QuadHotbarNetwork.PageRequestPayload> type() {
         return TYPE;
      }
   }

   private record PageSyncPayload(
      int pageCount,
      int hotbarRows,
      int hotbarPageCount,
      int activePage,
      int hotbarPage,
      int selectedSlot,
      int requestId,
      boolean bottomToTop,
      List<ItemStack> firstItems,
      List<ItemStack> secondItems,
      List<ItemStack> middleItems
   ) implements CustomPacketPayload {
      private static final Type<QuadHotbarNetwork.PageSyncPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("quadhotbar", "page_sync"));
      private static final StreamCodec<RegistryFriendlyByteBuf, QuadHotbarNetwork.PageSyncPayload> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, QuadHotbarNetwork.PageSyncPayload>() {
         public QuadHotbarNetwork.PageSyncPayload decode(RegistryFriendlyByteBuf buffer) {
            int pageCount = buffer.readVarInt();
            int rows = buffer.readVarInt();
            int hotbarPageCount = buffer.readVarInt();
            int active = buffer.readVarInt();
            int hotbar = buffer.readVarInt();
            int selected = buffer.readVarInt();
            int requestId = buffer.readVarInt();
            boolean bottomToTop = buffer.readBoolean();
            return new QuadHotbarNetwork.PageSyncPayload(
               pageCount,
               rows,
               hotbarPageCount,
               active,
               hotbar,
               selected,
               requestId,
               bottomToTop,
               QuadHotbarNetwork.PageSyncPayload.readItems(buffer),
               QuadHotbarNetwork.PageSyncPayload.readItems(buffer),
               QuadHotbarNetwork.PageSyncPayload.readItems(buffer)
            );
         }

         public void encode(RegistryFriendlyByteBuf buffer, QuadHotbarNetwork.PageSyncPayload payload) {
            buffer.writeVarInt(payload.pageCount);
            buffer.writeVarInt(payload.hotbarRows);
            buffer.writeVarInt(payload.hotbarPageCount);
            buffer.writeVarInt(payload.activePage);
            buffer.writeVarInt(payload.hotbarPage);
            buffer.writeVarInt(payload.selectedSlot);
            buffer.writeVarInt(payload.requestId);
            buffer.writeBoolean(payload.bottomToTop);
            QuadHotbarNetwork.PageSyncPayload.writeItems(buffer, payload.firstItems);
            QuadHotbarNetwork.PageSyncPayload.writeItems(buffer, payload.secondItems);
            QuadHotbarNetwork.PageSyncPayload.writeItems(buffer, payload.middleItems);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public Object decode(Object param1) {
            return this.decode((RegistryFriendlyByteBuf)var1);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public void encode(Object param1, Object nullx) {
            this.encode((RegistryFriendlyByteBuf)var1, (QuadHotbarNetwork.PageSyncPayload)nullx);
         }
      };

      private static List<ItemStack> readItems(RegistryFriendlyByteBuf buffer) {
         List<ItemStack> items = new ArrayList<>(36);

         for (int i = 0; i < 36; i++) {
            items.add((ItemStack)ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer));
         }

         return items;
      }

      private static void writeItems(RegistryFriendlyByteBuf buffer, List<ItemStack> items) {
         for (int i = 0; i < 36; i++) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, items.get(i));
         }
      }

      private static void handle(QuadHotbarNetwork.PageSyncPayload payload, IPayloadContext context) {
         context.enqueueWork(
            () -> QuadHotbarClientEvents.syncPagePayload(
               payload.pageCount,
               payload.hotbarRows,
               payload.activePage,
               payload.hotbarPage,
               payload.selectedSlot,
               payload.requestId,
               payload.bottomToTop,
               payload.firstItems,
               payload.secondItems,
               payload.middleItems
            )
         );
      }

      public Type<QuadHotbarNetwork.PageSyncPayload> type() {
         return TYPE;
      }
   }

   private record SelectionSyncPayload(int selectedSlot, int requestId) implements CustomPacketPayload {
      private static final Type<QuadHotbarNetwork.SelectionSyncPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("quadhotbar", "selection_sync"));
      private static final StreamCodec<ByteBuf, QuadHotbarNetwork.SelectionSyncPayload> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.VAR_INT,
         QuadHotbarNetwork.SelectionSyncPayload::selectedSlot,
         ByteBufCodecs.VAR_INT,
         QuadHotbarNetwork.SelectionSyncPayload::requestId,
         QuadHotbarNetwork.SelectionSyncPayload::new
      );

      private static void handle(QuadHotbarNetwork.SelectionSyncPayload payload, IPayloadContext context) {
         context.enqueueWork(() -> QuadHotbarClientEvents.syncPageSelection(payload.selectedSlot, payload.requestId));
      }

      public Type<QuadHotbarNetwork.SelectionSyncPayload> type() {
         return TYPE;
      }
   }

   private record ServerSupportPayload(int selectedSlot) implements CustomPacketPayload {
      private static final Type<QuadHotbarNetwork.ServerSupportPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("quadhotbar", "server_support"));
      private static final StreamCodec<ByteBuf, QuadHotbarNetwork.ServerSupportPayload> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.VAR_INT, QuadHotbarNetwork.ServerSupportPayload::selectedSlot, QuadHotbarNetwork.ServerSupportPayload::new
      );

      private static void handle(QuadHotbarNetwork.ServerSupportPayload payload, IPayloadContext context) {
         context.enqueueWork(() -> QuadHotbarClientEvents.syncServerSelectedSlot(payload.selectedSlot()));
      }

      public Type<QuadHotbarNetwork.ServerSupportPayload> type() {
         return TYPE;
      }
   }
}
