package org.embeddedt.modernfix.neoforge.caps;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.neoforged.neoforge.capabilities.BaseCapability;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;

public class CapProviderGetter {
   private static final MethodHandle BLOCK_CAP_PROVIDERS;
   private static final MethodHandle ITEM_CAP_PROVIDERS;
   private static final MethodHandle ENTITY_CAP_PROVIDERS;
   private static final boolean FOUND_PROVIDERS;
   private static final Map<?, List<ICapabilityProvider<?, ?, ?>>> DUMMY_MAP;

   private static MethodHandle obtainForClass(Class<? extends BaseCapability> clz) throws ReflectiveOperationException {
      Field field = clz.getDeclaredField("providers");
      field.setAccessible(true);
      return MethodHandles.publicLookup().unreflectGetter(field);
   }

   public static <T extends BaseCapability> Map<?, List<ICapabilityProvider<?, ?, ?>>> getProviderMap(T cap) {
      if (!FOUND_PROVIDERS) {
         return DUMMY_MAP;
      }

      try {
         if (cap instanceof BlockCapability<?, ?> blockCap) {
            return (Map)BLOCK_CAP_PROVIDERS.invokeExact((BlockCapability)blockCap);
         } else if (cap instanceof ItemCapability<?, ?> itemCap) {
            return (Map)ITEM_CAP_PROVIDERS.invokeExact((ItemCapability)itemCap);
         } else {
            return cap instanceof EntityCapability<?, ?> entityCap ? (Map)ENTITY_CAP_PROVIDERS.invokeExact((EntityCapability)entityCap) : DUMMY_MAP;
         }
      } catch (Throwable e) {
         throw new RuntimeException("Couldn't get map", e);
      }
   }

   public static void deduplicateCap(BaseCapability<?, ?> cap) {
      Map<?, List<ICapabilityProvider<?, ?, ?>>> map = getProviderMap(cap);
      if (!map.isEmpty()) {
         ObjectOpenHashSet<List<ICapabilityProvider<?, ?, ?>>> capLists = new ObjectOpenHashSet();
         int hits = 0;

         for (Entry<?, List<ICapabilityProvider<?, ?, ?>>> entry : map.entrySet()) {
            List<ICapabilityProvider<?, ?, ?>> v = entry.getValue();
            List<ICapabilityProvider<?, ?, ?>> canonicalList = (List<ICapabilityProvider<?, ?, ?>>)capLists.get(v);
            if (canonicalList == null) {
               canonicalList = List.copyOf(v);
               capLists.add(canonicalList);
            } else {
               hits++;
            }

            entry.setValue(canonicalList);
         }
      }
   }

   static {
      MethodHandle bProvider;
      MethodHandle iProvider;
      MethodHandle eProvider;
      boolean found;
      try {
         bProvider = obtainForClass(BlockCapability.class);
         iProvider = obtainForClass(ItemCapability.class);
         eProvider = obtainForClass(EntityCapability.class);
         found = true;
      } catch (ReflectiveOperationException e) {
         e.printStackTrace();
         bProvider = null;
         iProvider = null;
         eProvider = null;
         found = false;
      }

      BLOCK_CAP_PROVIDERS = bProvider;
      ITEM_CAP_PROVIDERS = iProvider;
      ENTITY_CAP_PROVIDERS = eProvider;
      FOUND_PROVIDERS = found;
      DUMMY_MAP = new HashMap<>();
   }
}
