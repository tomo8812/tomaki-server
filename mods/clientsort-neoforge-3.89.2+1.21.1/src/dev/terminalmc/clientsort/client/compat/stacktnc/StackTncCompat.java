package dev.terminalmc.clientsort.client.compat.stacktnc;

import dev.terminalmc.clientsort.client.ClientSort;
import java.lang.reflect.Method;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class StackTncCompat {
   public static final String MOD_NAME = "Stack to Nearby Chests";
   public static final String LOCKED_SLOTS_CLASS = "io.github.xiaocihua.stacktonearbychests.LockedSlots";
   public static final String IS_LOCKED_METHOD = "isLocked";
   public static final Class<?>[] IS_LOCKED_PARAMS = new Class[]{Slot.class};
   private static boolean hasFailed = false;
   private static Method isLockedMethod = null;

   public static boolean isLocked(Slot slot) {
      if (hasFailed) {
         return false;
      } else {
         return !(slot.container instanceof Inventory) ? false : checkStatic(slot);
      }
   }

   public static boolean checkStatic(Slot slot) {
      try {
         if (isLockedMethod == null) {
            Class<?> clazz = Class.forName("io.github.xiaocihua.stacktonearbychests.LockedSlots", false, Thread.currentThread().getContextClassLoader());
            isLockedMethod = clazz.getMethod("isLocked", IS_LOCKED_PARAMS);
         }

         if (isLockedMethod.invoke(null, slot) instanceof Boolean locked) {
            return locked;
         }

         throw new ClassCastException();
      } catch (IllegalAccessException e) {
         ClientSort.LOG.info("{} could not be accessed - compat is now disabled: {}", "Stack to Nearby Chests", e.getMessage());
      } catch (ClassNotFoundException e) {
         ClientSort.LOG.info("{} did not provide expected class - compat is now disabled: {}", "Stack to Nearby Chests", e.getMessage());
      } catch (NoSuchMethodException e) {
         ClientSort.LOG.info("{} did not provide expected method - compat is now disabled: {}", "Stack to Nearby Chests", e.getMessage());
      } catch (ClassCastException e) {
         ClientSort.LOG.info("{} did not provide expected return type - compat is now disabled: {}", "Stack to Nearby Chests", e.getMessage());
      } catch (Throwable e) {
         ClientSort.LOG.info("{} threw an exception - compat is now disabled: {}", "Stack to Nearby Chests", e.getMessage());
      }

      hasFailed = true;
      return false;
   }
}
