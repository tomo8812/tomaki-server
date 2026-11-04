package dev.terminalmc.clientsort;

import dev.terminalmc.clientsort.config.ServerConfig;
import dev.terminalmc.clientsort.network.handler.validate.PolicyManager;
import dev.terminalmc.clientsort.util.Logging;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.ticks.ContainerSingleItem;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ClientSort {
   public static final String MOD_ID = "clientsort";
   public static final String MOD_NAME = "ClientSort";
   public static final Logger LOG = Logging.getLogger("clientsort");
   public static boolean debugEnabled;

   private ClientSort() {
      throw new UnsupportedOperationException("This class cannot be instantiated.");
   }

   public static boolean debug() {
      return debugEnabled;
   }

   public static void init() {
      ServerConfig.getAndSave();
   }

   public static void onConfigSaved(ServerConfig config) {
      ServerConfig.Options options = config.options;
      PolicyManager.reloadPolicyClasses(options.classPolicies.keySet());
   }

   @Nullable
   public static Object getObj(Slot slot, AbstractContainerMenu menu) {
      return getObj(slot.container, menu);
   }

   @Nullable
   public static Object getObj(@Nullable Container container, AbstractContainerMenu menu) {
      return switch (container) {
         case null -> null;
         case ContainerSingleItem ignored -> menu;
         case SimpleContainer ignored -> menu;
         default -> container;
      };
   }
}
