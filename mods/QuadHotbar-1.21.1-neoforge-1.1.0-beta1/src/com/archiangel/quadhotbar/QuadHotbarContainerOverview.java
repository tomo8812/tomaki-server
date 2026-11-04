package com.archiangel.quadhotbar;

import com.archiangel.quadhotbar.client.QuadHotbarContainerOverviewClient;
import com.archiangel.quadhotbar.compat.CorpsePages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = "quadhotbar")
public final class QuadHotbarContainerOverview {
   private static final Map<UUID, QuadHotbarContainerOverview.Session> SESSIONS = new HashMap<>();
   private static int nextToken;

   private QuadHotbarContainerOverview() {
   }

   public static void register(PayloadRegistrar registrar) {
      registrar.playToServer(
         QuadHotbarContainerOverview.Action.TYPE, QuadHotbarContainerOverview.Action.CODEC, (message, context) -> context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
               handle(player, message);
            }
         })
      );
      registrar.playToClient(
         QuadHotbarContainerOverview.Header.TYPE,
         QuadHotbarContainerOverview.Header.CODEC,
         (message, context) -> context.enqueueWork(() -> QuadHotbarContainerOverviewClient.open(message))
      );
      registrar.playToClient(
         QuadHotbarContainerOverview.Frame.TYPE,
         QuadHotbarContainerOverview.Frame.CODEC,
         (message, context) -> context.enqueueWork(() -> QuadHotbarContainerOverviewClient.sync(message))
      );
   }

   public static void requestOpen(int parent) {
      PacketDistributor.sendToServer(
         new QuadHotbarContainerOverview.Action(0, parent, 0, -1, 0, ClickType.PICKUP, QuadHotbarConfig.overviewHeight), new CustomPacketPayload[0]
      );
   }

   public static void click(int parent, int token, int slot, int button, ClickType type) {
      PacketDistributor.sendToServer(new QuadHotbarContainerOverview.Action(1, parent, token, slot, button, type, 0), new CustomPacketPayload[0]);
   }

   public static void close(int parent, int token) {
      PacketDistributor.sendToServer(new QuadHotbarContainerOverview.Action(2, parent, token, -1, 0, ClickType.PICKUP, 0), new CustomPacketPayload[0]);
   }

   private static void handle(ServerPlayer player, QuadHotbarContainerOverview.Action message) {
      if (message.action == 0) {
         open(player, message.parent, message.height);
      } else {
         QuadHotbarContainerOverview.Session session = SESSIONS.get(player.getUUID());
         if (session != null && session.token == message.token && session.parent.containerId == message.parent) {
            if (message.action == 2) {
               SESSIONS.remove(player.getUUID());
            } else if (message.action == 1 && session.valid(player) && session.editable) {
               if (message.slot >= 0 || message.slot == -999 && message.clickType == ClickType.QUICK_CRAFT) {
                  if (message.slot < session.menu.pageSlots && validButton(message.clickType, message.button)) {
                     session.menu.clicked(message.slot, message.button, message.clickType, player);
                     session.parent.broadcastFullState();
                     QuadHotbarNetwork.sendPageSync(player);
                     session.sync(player, false);
                  }
               }
            }
         }
      }
   }

   private static boolean validButton(ClickType type, int button) {
      return switch (type) {
         case PICKUP, QUICK_MOVE, THROW, PICKUP_ALL -> button == 0 || button == 1;
         case SWAP -> button >= 0 && button <= 8 || button == 40;
         case CLONE -> button == 2;
         case QUICK_CRAFT -> button >= 0 && button <= 10;
         default -> throw new MatchException(null, null);
      };
   }

   private static void open(ServerPlayer player, int id, int height) {
      AbstractContainerMenu parent = player.containerMenu;
      if (parent.containerId == id
         && parent != player.inventoryMenu
         && !(parent instanceof QuadHotbarOverviewMenu)
         && !parent.slots.isEmpty()
         && parent.stillValid(player)
         && player.isAlive()
         && !player.isSpectator()
         && QuadHotbarServerConfig.forPlayer(player).allowContainerOverview()) {
         QuadHotbarContainerOverview.Session existing = SESSIONS.get(player.getUUID());
         if (existing == null || existing.parent != parent || !existing.valid(player)) {
            QuadHotbarNetwork.enforceServerLimits(player);
            CorpsePages.View corpse = CorpsePages.find(parent);
            QuadHotbarPages.State state = QuadHotbarPages.state(player);
            int pages = corpse == null ? state.pageCount : corpse.pages();
            int active = corpse == null ? state.activePage : 0;
            int rows = Math.max(1, Math.min(height, QuadHotbarServerConfig.forPlayer(player).maxOverviewHeight()));
            Container storage = corpse == null ? QuadHotbarOverviewMenu.playerPages(player, pages) : corpse.storage();
            QuadHotbarOverviewMenu menu = QuadHotbarOverviewMenu.external(id, player.getInventory(), pages, active, rows, parent, storage, corpse != null);
            nextToken = nextToken == Integer.MAX_VALUE ? 1 : nextToken + 1;
            QuadHotbarContainerOverview.Session session = new QuadHotbarContainerOverview.Session(parent, menu, nextToken, corpse == null || corpse.editable());
            SESSIONS.put(player.getUUID(), session);
            PacketDistributor.sendToPlayer(
               player, new QuadHotbarContainerOverview.Header(id, nextToken, pages, active, rows, corpse != null), new CustomPacketPayload[0]
            );
            session.sync(player, true);
         }
      }
   }

   @SubscribeEvent
   public static void tick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         QuadHotbarContainerOverview.Session session = SESSIONS.get(player.getUUID());
         if (session != null) {
            if (!session.valid(player)) {
               SESSIONS.remove(player.getUUID());
               PacketDistributor.sendToPlayer(
                  player,
                  new QuadHotbarContainerOverview.Frame(session.parent.containerId, session.token, -2, List.of(), ItemStack.EMPTY),
                  new CustomPacketPayload[0]
               );
            } else if (player.tickCount % 2 == 0) {
               session.sync(player, false);
            }
         }
      }
   }

   @SubscribeEvent
   public static void logout(PlayerLoggedOutEvent event) {
      SESSIONS.remove(event.getEntity().getUUID());
   }

   @SubscribeEvent
   public static void stop(ServerStoppedEvent event) {
      SESSIONS.clear();
   }

   public record Action(int action, int parent, int token, int slot, int button, ClickType clickType, int height) implements CustomPacketPayload {
      static final Type<QuadHotbarContainerOverview.Action> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("quadhotbar", "container_overview_action"));
      static final StreamCodec<RegistryFriendlyByteBuf, QuadHotbarContainerOverview.Action> CODEC = new StreamCodec<RegistryFriendlyByteBuf, QuadHotbarContainerOverview.Action>() {
         public QuadHotbarContainerOverview.Action decode(RegistryFriendlyByteBuf b) {
            return new QuadHotbarContainerOverview.Action(
               b.readVarInt(), b.readVarInt(), b.readVarInt(), b.readVarInt(), b.readVarInt(), (ClickType)b.readEnum(ClickType.class), b.readVarInt()
            );
         }

         public void encode(RegistryFriendlyByteBuf b, QuadHotbarContainerOverview.Action v) {
            b.writeVarInt(v.action);
            b.writeVarInt(v.parent);
            b.writeVarInt(v.token);
            b.writeVarInt(v.slot);
            b.writeVarInt(v.button);
            b.writeEnum(v.clickType);
            b.writeVarInt(v.height);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public Object decode(Object param1) {
            return this.decode((RegistryFriendlyByteBuf)var1);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public void encode(Object param1, Object nullx) {
            this.encode((RegistryFriendlyByteBuf)var1, (QuadHotbarContainerOverview.Action)nullx);
         }
      };

      public Type<QuadHotbarContainerOverview.Action> type() {
         return TYPE;
      }
   }

   public record Frame(int parent, int token, int page, List<ItemStack> stacks, ItemStack cursor) implements CustomPacketPayload {
      static final Type<QuadHotbarContainerOverview.Frame> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("quadhotbar", "container_overview_frame"));
      static final StreamCodec<RegistryFriendlyByteBuf, QuadHotbarContainerOverview.Frame> CODEC = new StreamCodec<RegistryFriendlyByteBuf, QuadHotbarContainerOverview.Frame>() {
         public QuadHotbarContainerOverview.Frame decode(RegistryFriendlyByteBuf b) {
            int parent = b.readVarInt();
            int token = b.readVarInt();
            int page = b.readVarInt();
            int size = b.readVarInt();
            if (size != 0 && size != 36) {
               throw new IllegalArgumentException("Invalid overview page size");
            }

            ArrayList<ItemStack> stacks = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
               stacks.add((ItemStack)ItemStack.OPTIONAL_STREAM_CODEC.decode(b));
            }

            return new QuadHotbarContainerOverview.Frame(parent, token, page, stacks, (ItemStack)ItemStack.OPTIONAL_STREAM_CODEC.decode(b));
         }

         public void encode(RegistryFriendlyByteBuf b, QuadHotbarContainerOverview.Frame v) {
            b.writeVarInt(v.parent);
            b.writeVarInt(v.token);
            b.writeVarInt(v.page);
            b.writeVarInt(v.stacks.size());

            for (ItemStack stack : v.stacks) {
               ItemStack.OPTIONAL_STREAM_CODEC.encode(b, stack);
            }

            ItemStack.OPTIONAL_STREAM_CODEC.encode(b, v.cursor);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public Object decode(Object param1) {
            return this.decode((RegistryFriendlyByteBuf)var1);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public void encode(Object param1, Object nullx) {
            this.encode((RegistryFriendlyByteBuf)var1, (QuadHotbarContainerOverview.Frame)nullx);
         }
      };

      public Type<QuadHotbarContainerOverview.Frame> type() {
         return TYPE;
      }
   }

   public record Header(int parent, int token, int pages, int active, int height, boolean death) implements CustomPacketPayload {
      static final Type<QuadHotbarContainerOverview.Header> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("quadhotbar", "container_overview_header"));
      static final StreamCodec<RegistryFriendlyByteBuf, QuadHotbarContainerOverview.Header> CODEC = new StreamCodec<RegistryFriendlyByteBuf, QuadHotbarContainerOverview.Header>() {
         public QuadHotbarContainerOverview.Header decode(RegistryFriendlyByteBuf b) {
            return new QuadHotbarContainerOverview.Header(b.readVarInt(), b.readVarInt(), b.readVarInt(), b.readVarInt(), b.readVarInt(), b.readBoolean());
         }

         public void encode(RegistryFriendlyByteBuf b, QuadHotbarContainerOverview.Header v) {
            b.writeVarInt(v.parent);
            b.writeVarInt(v.token);
            b.writeVarInt(v.pages);
            b.writeVarInt(v.active);
            b.writeVarInt(v.height);
            b.writeBoolean(v.death);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public Object decode(Object param1) {
            return this.decode((RegistryFriendlyByteBuf)var1);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public void encode(Object param1, Object nullx) {
            this.encode((RegistryFriendlyByteBuf)var1, (QuadHotbarContainerOverview.Header)nullx);
         }
      };

      public Type<QuadHotbarContainerOverview.Header> type() {
         return TYPE;
      }
   }

   private static final class Session {
      final AbstractContainerMenu parent;
      final QuadHotbarOverviewMenu menu;
      final int token;
      final boolean editable;
      final List<ItemStack> previous = new ArrayList<>();
      ItemStack previousCursor = ItemStack.EMPTY;

      Session(AbstractContainerMenu parent, QuadHotbarOverviewMenu menu, int token, boolean editable) {
         this.parent = parent;
         this.menu = menu;
         this.token = token;
         this.editable = editable;

         for (int i = 0; i < menu.pageSlots; i++) {
            this.previous.add(ItemStack.EMPTY);
         }
      }

      boolean valid(ServerPlayer player) {
         return this.menu.stillValid(player) && !player.isSpectator();
      }

      void sync(ServerPlayer player, boolean force) {
         boolean cursorChanged = !ItemStack.matches(this.previousCursor, this.menu.getCarried());
         boolean sent = false;

         for (int page = 0; page < this.menu.pageCount; page++) {
            boolean changed = force;
            ArrayList<ItemStack> stacks = new ArrayList<>(36);

            for (int slot = 0; slot < 36; slot++) {
               int index = page * 36 + slot;
               ItemStack stack = ((Slot)this.menu.slots.get(index)).getItem();
               if (!ItemStack.matches(this.previous.get(index), stack)) {
                  changed = true;
               }

               stacks.add(stack.copy());
            }

            if (changed) {
               for (int slot = 0; slot < 36; slot++) {
                  this.previous.set(page * 36 + slot, stacks.get(slot).copy());
               }

               PacketDistributor.sendToPlayer(
                  player,
                  new QuadHotbarContainerOverview.Frame(this.parent.containerId, this.token, page, stacks, this.menu.getCarried().copy()),
                  new CustomPacketPayload[0]
               );
               sent = true;
            }
         }

         if (cursorChanged && !sent) {
            PacketDistributor.sendToPlayer(
               player,
               new QuadHotbarContainerOverview.Frame(this.parent.containerId, this.token, -1, List.of(), this.menu.getCarried().copy()),
               new CustomPacketPayload[0]
            );
         }

         this.previousCursor = this.menu.getCarried().copy();
      }
   }
}
