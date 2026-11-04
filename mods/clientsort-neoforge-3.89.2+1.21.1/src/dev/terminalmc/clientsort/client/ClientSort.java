package dev.terminalmc.clientsort.client;

import dev.terminalmc.clientsort.client.config.Config;
import dev.terminalmc.clientsort.client.interaction.InteractionManager;
import dev.terminalmc.clientsort.client.order.SortOrder;
import dev.terminalmc.clientsort.client.util.KeybindManager;
import dev.terminalmc.clientsort.client.util.PolicyManager;
import dev.terminalmc.clientsort.client.util.TaskManager;
import dev.terminalmc.clientsort.mixin.client.accessor.AbstractContainerScreenAccessor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ClientSort {
   public static final String MOD_ID = "clientsort";
   public static final String MOD_NAME = "ClientSort";
   public static final Logger LOG = dev.terminalmc.clientsort.ClientSort.LOG;
   public static final TaskManager taskManager = new TaskManager();
   @Nullable
   public static MultiLineTextWidget overlayMessage = null;
   private static Runnable clearOverlayMessage = null;
   public static volatile boolean searchOrderUpdated;
   public static volatile boolean emiReloading = false;
   public static volatile boolean updateBlockedByEmi = false;
   public static volatile boolean operatingClient = false;
   public static BlockingQueue<Runnable> clientOpQueue = new ArrayBlockingQueue<>(2);

   private ClientSort() {
      throw new UnsupportedOperationException("This class cannot be instantiated.");
   }

   public static boolean debug() {
      return dev.terminalmc.clientsort.ClientSort.debug();
   }

   public static void init() {
      Config.getAndSave();
   }

   public static void afterClientTick(Minecraft mc) {
      taskManager.tick();
   }

   public static void afterConfigSaved(Config config) {
      Minecraft mc = Minecraft.getInstance();
      Config.Options options = config.options;
      options.sortOrder = SortOrder.SORT_ORDERS.get(options.sortOrderStr);
      options.shiftSortOrder = SortOrder.SORT_ORDERS.get(options.shiftSortOrderStr);
      options.ctrlSortOrder = SortOrder.SORT_ORDERS.get(options.ctrlSortOrderStr);
      options.altSortOrder = SortOrder.SORT_ORDERS.get(options.altSortOrderStr);
      options.sortSoundLoc = ResourceLocation.tryParse(options.interactionSound);
      InteractionManager.setTickRate(options.interactionInterval);
      PolicyManager.reloadPolicyClasses(options.classPolicies.keySet());
      if (mc != null && mc.getConnection() != null && mc.getConnection().isAcceptingMessages()) {
         updateItemTags(options);
         updateItemSets(options);
      }

      if (Config.options().isolateKeybinds) {
         KeybindManager.isolateKeybinds();
      }
   }

   public static void updateItemTags(Config.Options options) {
      options.typeMatchItemCache.clear();
      BuiltInRegistries.ITEM.getTags().forEach(pair -> {
         if (options.typeMatchTags.contains(((TagKey)pair.getFirst()).location().getPath())) {
            ((Named)pair.getSecond()).forEach(itemHolder -> options.typeMatchItemCache.add((Item)itemHolder.value()));
         }
      });
   }

   public static void updateItemSets(Config.Options options) {
      options.startOverrideMap.clear();
      int i = 0;

      for (String s : options.startOverrideItems) {
         int idx = i++;
         BuiltInRegistries.ITEM.getOptional(ResourceLocation.tryParse(s)).ifPresent(item -> options.startOverrideMap.put(item, idx));
      }

      options.endOverrideMap.clear();
      i = 0;

      for (String s : options.endOverrideItems) {
         int idx;
         int var9 = idx = i++;
         BuiltInRegistries.ITEM.getOptional(ResourceLocation.tryParse(s)).ifPresent(item -> options.endOverrideMap.put(item, idx));
      }
   }

   public static void afterGameStart() {
      if (Config.options().isolateKeybinds) {
         KeybindManager.isolateKeybinds();
      }
   }

   public static void setOverlayMessage(AbstractContainerScreen<?> screen, Component message, int clearAfterTicks) {
      if (overlayMessage != null) {
         clearOverlayMessage.run();
      }

      Minecraft mc = Minecraft.getInstance();
      MultiLineTextWidget newMessage = new MultiLineTextWidget(message, mc.font);
      newMessage.setMaxWidth(((AbstractContainerScreenAccessor)screen).clientsort$getImageWidth());
      newMessage.setCentered(true);
      newMessage.setX(screen.width / 2 - newMessage.getWidth() / 2);
      newMessage.setY(screen.height / 2 - newMessage.getHeight() / 2);
      overlayMessage = newMessage;
      clearOverlayMessage = () -> {
         if (overlayMessage == newMessage) {
            overlayMessage = null;
            clearOverlayMessage = null;
         }
      };
      taskManager.schedule(clearAfterTicks, clearOverlayMessage);
   }
}
