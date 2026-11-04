package dev.terminalmc.clientsort.client.compat.itemlocks;

import dev.terminalmc.clientsort.client.ClientSort;
import dev.terminalmc.clientsort.util.inject.ISlot;
import java.lang.reflect.Method;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class ItemLocksCompat {
   public static final String MOD_NAME = "ItemLocks";
   public static final String KEY_BINDINGS_CLASS = "com.kirdow.itemlocks.client.input.KeyBindings";
   public static final String IS_BYPASS_METHOD = "isBypass";
   public static final Class<?>[] IS_BYPASS_PARAMS = new Class[0];
   public static final String COMPONENTS_CLASS = "com.kirdow.itemlocks.proxy.Components";
   public static final String GET_COMPONENT_METHOD = "getComponent";
   public static final Class<?>[] GET_COMPONENT_PARAMS = new Class[]{Class.class};
   public static final String LOCK_MANAGER_CLASS = "com.kirdow.itemlocks.client.LockManager";
   public static final String IS_LOCKED_SLOT_RAW_METHOD = "isLockedSlotRaw";
   public static final Class<?>[] IS_LOCKED_SLOT_RAW_PARAMS = new Class[]{int.class};
   private static boolean hasFailed = false;
   private static Method isBypassMethod = null;
   private static Object lockManagerInstance = null;
   private static Method isLockedSlotRawMethod = null;

   public static boolean isLocked(Slot slot) {
      if (hasFailed) {
         return false;
      }

      if (!(slot.container instanceof Inventory)) {
         return false;
      }

      int index = adjustForInventory(((ISlot)slot).clientsort$getIndexInContainer());
      return checkStatic(index);
   }

   private static int adjustForInventory(int slot) {
      if (0 <= slot && slot <= 8) {
         return slot + 27;
      } else {
         return 9 <= slot && slot <= 35 ? slot - 9 : slot;
      }
   }

   public static boolean checkStatic(int index) {
      try {
         if (isBypassMethod == null) {
            Class<?> keyBindingsClass = Class.forName("com.kirdow.itemlocks.client.input.KeyBindings", false, Thread.currentThread().getContextClassLoader());
            isBypassMethod = keyBindingsClass.getMethod("isBypass", IS_BYPASS_PARAMS);
         }

         if (isBypassMethod.invoke(null) instanceof Boolean bypass) {
            if (bypass) {
               return false;
            }

            if (lockManagerInstance == null || isLockedSlotRawMethod == null) {
               Class<?> componentsClass = Class.forName("com.kirdow.itemlocks.proxy.Components", false, Thread.currentThread().getContextClassLoader());
               Method getComponentMethod = componentsClass.getMethod("getComponent", GET_COMPONENT_PARAMS);
               Class<?> lockManagerClass = Class.forName("com.kirdow.itemlocks.client.LockManager", false, Thread.currentThread().getContextClassLoader());
               lockManagerInstance = getComponentMethod.invoke(null, lockManagerClass);
               if (lockManagerInstance == null) {
                  throw new ClassCastException();
               }

               isLockedSlotRawMethod = lockManagerClass.getMethod("isLockedSlotRaw", IS_LOCKED_SLOT_RAW_PARAMS);
            }

            if (isLockedSlotRawMethod.invoke(lockManagerInstance, index) instanceof Boolean locked) {
               return locked;
            }

            throw new ClassCastException();
         }

         throw new ClassCastException();
      } catch (IllegalAccessException e) {
         ClientSort.LOG.info("{} could not be accessed - compat is now disabled: {}", "ItemLocks", e.getMessage());
      } catch (ClassNotFoundException e) {
         ClientSort.LOG.info("{} did not provide expected class - compat is now disabled: {}", "ItemLocks", e.getMessage());
      } catch (NoSuchMethodException e) {
         ClientSort.LOG.info("{} did not provide expected method - compat is now disabled: {}", "ItemLocks", e.getMessage());
      } catch (ClassCastException e) {
         ClientSort.LOG.info("{} did not provide expected return type - compat is now disabled: {}", "ItemLocks", e.getMessage());
      } catch (Throwable e) {
         ClientSort.LOG.info("{} threw an exception - compat is now disabled: {}", "ItemLocks", e.getMessage());
      }

      hasFailed = true;
      return false;
   }
}
