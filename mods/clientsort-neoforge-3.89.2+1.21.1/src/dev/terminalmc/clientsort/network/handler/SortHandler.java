package dev.terminalmc.clientsort.network.handler;

import dev.terminalmc.clientsort.ClientSort;
import dev.terminalmc.clientsort.exception.PayloadHandlerException;
import dev.terminalmc.clientsort.network.handler.validate.PolicyManager;
import dev.terminalmc.clientsort.network.handler.validate.SchemaValidator;
import dev.terminalmc.clientsort.network.payload.SortPayload;
import dev.terminalmc.clientsort.network.payload.SortResultPayload;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SortHandler extends PayloadHandler {
   private SortHandler() {
   }

   public static void handle(SortPayload payload, MinecraftServer server, ServerPlayer player) {
      server.execute(
         () -> processPayload(
            server,
            player,
            payload.containerId(),
            menu -> checkPolicy(player, menu, payload.slotMapping()),
            menu -> SchemaValidator.validateSlotMapping(player, menu, payload.slotMapping()),
            menu -> sort(server, menu, payload.slotMapping()),
            SortPayload.TYPE,
            SortResultPayload.TYPE,
            (result, message) -> new SortResultPayload(result.code, message)
         )
      );
   }

   private static void sort(MinecraftServer server, AbstractContainerMenu menu, int[] slotMapping) throws PayloadHandlerException {
      Int2IntMap map = new Int2IntOpenHashMap();

      for (int i = 0; i < slotMapping.length - 1; i += 2) {
         map.put(slotMapping[i + 1], slotMapping[i]);
      }

      Container tmpContainer = new SimpleContainer(1) {
         public int getMaxStackSize() {
            return Integer.MAX_VALUE;
         }

         public int getMaxStackSize(@NotNull ItemStack ignored) {
            return this.getMaxStackSize();
         }
      };
      Slot tmpSlot = new Slot(tmpContainer, 0, 0, 0) {
         public int getMaxStackSize(@NotNull ItemStack ignored) {
            return this.getMaxStackSize();
         }
      };
      List<Slot> undoChain = new ArrayList<>();

      for (int i = 1; i < slotMapping.length; i += 2) {
         undoChain.clear();
         int startSlotId = slotMapping[i];
         if (map.containsKey(startSlotId)) {
            Slot startSlot = menu.getSlot(startSlotId);
            tmpSlot.setByPlayer(startSlot.getItem());
            int srcSlotId = slotMapping[i - 1];
            Slot srcSlot = startSlot;

            while (map.containsKey(map.getOrDefault(srcSlotId, Integer.MIN_VALUE))) {
               Slot dstSlot = srcSlot;
               srcSlot = (Slot)menu.slots.get(srcSlotId);
               srcSlotId = map.remove(srcSlotId);
               if (!dstSlot.mayPlace(srcSlot.getItem())) {
                  for (int j = undoChain.size() - 1; j > 0; j--) {
                     Slot recSrcSlot = undoChain.get(j);
                     Slot recDstSlot = undoChain.get(j - 1);
                     recSrcSlot.setByPlayer(recDstSlot.getItem());
                  }

                  startSlot.setByPlayer(tmpSlot.getItem());
                  throw new PayloadHandlerException.InconsistentStateException(
                     String.format(
                        "Item %s from slot %d cannot be placed in slot %d. Has the inventory changed?",
                        srcSlot.getItem().getDisplayName().getString(),
                        srcSlot.index,
                        dstSlot.index
                     )
                  );
               }

               dstSlot.setByPlayer(srcSlot.getItem());
               undoChain.add(dstSlot);
               undoChain.add(srcSlot);
            }

            srcSlot.setByPlayer(tmpSlot.getItem());
         }
      }
   }

   private static void checkPolicy(ServerPlayer player, AbstractContainerMenu menu, int[] slotIds) throws PayloadHandlerException.UnsupportedOpException {
      Container container = slotIds.length > 0 ? ((Slot)menu.slots.get(slotIds[0])).container : null;
      Object object = ClientSort.getObj(container, menu);
      if (object == null) {
         throw new PayloadHandlerException.UnsupportedOpException(
            "Reference object is null for inputs '%s', '%s'!"
               .formatted(container == null ? "null" : container.getClass().getName(), menu == null ? "null" : menu.getClass().getName())
         );
      }

      if (container != player.getInventory()) {
         PolicyManager.checkPolicy(object.getClass(), bl -> bl.sortEnabled);
      }
   }
}
