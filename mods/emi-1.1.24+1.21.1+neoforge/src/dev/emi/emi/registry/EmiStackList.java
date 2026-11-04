package dev.emi.emi.registry;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiRegistryAdapter;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.config.IndexSource;
import dev.emi.emi.data.EmiAlias;
import dev.emi.emi.data.EmiData;
import dev.emi.emi.data.IndexStackData;
import dev.emi.emi.runtime.EmiHidden;
import dev.emi.emi.runtime.EmiLog;
import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Type;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class EmiStackList {
   private static final TagKey<Item> ITEM_HIDDEN = TagKey.create(EmiPort.getItemRegistry().key(), EmiTags.HIDDEN_FROM_RECIPE_VIEWERS);
   private static final TagKey<Block> BLOCK_HIDDEN = TagKey.create(EmiPort.getBlockRegistry().key(), EmiTags.HIDDEN_FROM_RECIPE_VIEWERS);
   private static final TagKey<Fluid> FLUID_HIDDEN = TagKey.create(EmiPort.getFluidRegistry().key(), EmiTags.HIDDEN_FROM_RECIPE_VIEWERS);
   public static List<EmiAlias.Baked> registryAliases = Lists.newArrayList();
   public static List<Predicate<EmiStack>> invalidators = Lists.newArrayList();
   public static List<EmiStack> stacks = List.of();
   public static List<EmiStack> filteredStacks = List.of();
   private static Object2IntMap<EmiStack> strictIndices = new Object2IntOpenCustomHashMap(new EmiStackList.StrictHashStrategy());
   private static Object2IntMap<Object> keyIndices = new Object2IntOpenHashMap();

   public static void clear() {
      invalidators.clear();
      registryAliases.clear();
      stacks = List.of();
      strictIndices.clear();
      keyIndices.clear();
   }

   public static void reload() {
      Minecraft client = Minecraft.getInstance();
      ItemDisplayParameters context = new ItemDisplayParameters(client.player.connection.enabledFeatures(), false, client.level.registryAccess());
      List<EmiStackList.IndexGroup> groups = Lists.newArrayList();
      Map<String, EmiStackList.IndexGroup> namespaceGroups = new LinkedHashMap<>();

      for (Item item : EmiPort.getItemRegistry()) {
         String itemName = "null";

         try {
            itemName = item.toString();
            EmiStack stack = EmiStack.of(item);
            namespaceGroups.computeIfAbsent(stack.getId().getNamespace(), k -> new EmiStackList.IndexGroup()).stacks.add(stack);
         } catch (Exception e) {
            EmiLog.error("Item " + itemName + " threw while EMI was attempting to construct the index, items may be missing.", e);
         }
      }

      if (EmiConfig.indexSource != IndexSource.REGISTERED) {
         long groupReloadStart = System.currentTimeMillis();
         EmiLog.info("Reloading item groups on client thread...");
         Map<CreativeModeTab, Collection<ItemStack>> itemGroupToStacksMap = (Map<CreativeModeTab, Collection<ItemStack>>)client.submit(() -> {
            Map<CreativeModeTab, Collection<ItemStack>> map = new Reference2ReferenceOpenHashMap();
            Consumer<CreativeModeTab> itemGroupConsumer = groupx -> {
               String groupName = "null";

               try {
                  groupName = groupx.getDisplayName().toString();
                  groupx.buildContents(context);
                  map.put(groupx, groupx.getSearchTabDisplayItems());
               } catch (Exception e) {
                  EmiLog.error("Creative item group " + groupName + " threw while EMI was attempting to construct the index, items may be missing.", ex);
               }
            };
            List<CreativeModeTab> itemGroups = CreativeModeTabs.allTabs();
            itemGroups.stream().filter(g -> g.getType() == Type.CATEGORY).forEach(itemGroupConsumer);
            itemGroups.stream().filter(g -> g.getType() != Type.CATEGORY).forEach(itemGroupConsumer);
            return map;
         }).join();
         EmiLog.info("Reloading item groups on client thread took " + (System.currentTimeMillis() - groupReloadStart) + "ms");

         for (CreativeModeTab group : CreativeModeTabs.allTabs()) {
            String groupName = "null";

            try {
               groupName = group.getDisplayName().getString();
               Object2IntMap<String> usedNamespaces = new Object2IntOpenHashMap();
               EmiStackList.IndexGroup ig = new EmiStackList.IndexGroup();
               Collection<ItemStack> searchStacks = itemGroupToStacksMap.get(group);

               for (ItemStack stack : searchStacks) {
                  EmiStack es = EmiStack.of(stack);
                  String namespace = es.getId().getNamespace();
                  usedNamespaces.put(namespace, usedNamespaces.getOrDefault(namespace, 0) + 1);
                  ig.stacks.add(es);
               }

               if (EmiConfig.indexSource == IndexSource.CREATIVE) {
                  ObjectIterator var34 = usedNamespaces.keySet().iterator();

                  while (var34.hasNext()) {
                     String namespace = (String)var34.next();
                     if (namespaceGroups.containsKey(namespace)) {
                        EmiStackList.IndexGroup ng = namespaceGroups.get(namespace);
                        if (usedNamespaces.getInt(namespace) * 3 >= searchStacks.size() || usedNamespaces.getInt(namespace) * 3 >= ng.stacks.size()) {
                           ng.suppressedBy.add(ig);
                        }
                     }
                  }
               }

               groups.add(ig);
            } catch (Exception e) {
               EmiLog.error("Creative item group " + groupName + " threw while EMI was attempting to construct the index, items may be missing.", e);
            }
         }
      }

      groups.addAll(namespaceGroups.values());
      EmiStackList.IndexGroup fluidGroup = new EmiStackList.IndexGroup();

      for (Fluid fluid : EmiPort.getFluidRegistry()) {
         String fluidName = null;

         try {
            fluidName = fluid.toString();
            if (fluid.isSource(fluid.defaultFluidState()) || fluid instanceof FlowingFluid ff && ff.getSource() == Fluids.EMPTY) {
               EmiStack fs = EmiStack.of(fluid);
               fluidGroup.stacks.add(fs);
            }
         } catch (Exception e) {
            EmiLog.error("Fluid  " + fluidName + " threw while EMI was attempting to construct the index, stack may be missing.", e);
         }
      }

      groups.add(fluidGroup);
      Set<EmiStack> added = new ObjectOpenCustomHashSet(new EmiStackList.StrictHashStrategy());
      stacks = Lists.newLinkedList();

      for (EmiStackList.IndexGroup group : groups) {
         if (group.shouldDisplay()) {
            for (EmiStack stack : group.stacks) {
               if (!added.contains(stack)) {
                  stacks.add(stack);
                  added.add(stack);
               }
            }
         }
      }
   }

   private static <T> boolean isHiddenFromRecipeViewers(T key) {
      if (key instanceof Item i) {
         if (i instanceof BlockItem bi && bi.getBlock().defaultBlockState().is(BLOCK_HIDDEN)) {
            return true;
         }

         if (i.builtInRegistryHolder().is(ITEM_HIDDEN)) {
            return true;
         }
      } else if (key instanceof Fluid f) {
         if (f.is(FLUID_HIDDEN)) {
            return true;
         }
      } else {
         EmiRegistryAdapter<T> adapter = (EmiRegistryAdapter<T>)EmiTags.ADAPTERS_BY_CLASS.get(key.getClass());
         if (adapter != null) {
            return adapter.getRegistry().wrapAsHolder(key).is(TagKey.create(adapter.getRegistry().key(), EmiTags.HIDDEN_FROM_RECIPE_VIEWERS));
         }
      }

      return false;
   }

   public static void bake() {
      stacks.removeIf(s -> {
         try {
            if (s.isEmpty()) {
               return true;
            }

            for (Predicate<EmiStack> invalidator : invalidators) {
               if (invalidator.test(s)) {
                  return true;
               }
            }

            return isHiddenFromRecipeViewers(s.getKey());
         } catch (Throwable t) {
            EmiLog.error("Stack threw error while baking", t);
            return true;
         }
      });

      for (Supplier<IndexStackData> supplier : EmiData.stackData) {
         IndexStackData ssd = supplier.get();
         if (!ssd.removed().isEmpty()) {
            Set<EmiStack> removed = Sets.newHashSet();

            for (EmiIngredient invalidator : ssd.removed()) {
               for (EmiStack stack : invalidator.getEmiStacks()) {
                  removed.add(stack.copy().comparison(c -> EmiPort.compareStrict()));
               }
            }

            stacks.removeAll(removed);
         }

         if (!ssd.filters().isEmpty()) {
            stacks.removeIf(s -> {
               String id = s.getId() + "";

               for (IndexStackData.Filter filter : ssd.filters()) {
                  if (filter.filter().test(id)) {
                     return true;
                  }
               }

               return false;
            });
         }

         for (IndexStackData.Added added : ssd.added()) {
            if (!added.added().isEmpty()) {
               if (added.after().isEmpty()) {
                  stacks.add(added.added().getEmiStacks().get(0));
               } else {
                  int i = stacks.indexOf(added.after());
                  if (i == -1) {
                     i = stacks.size() - 1;
                  }

                  stacks.add(i + 1, added.added().getEmiStacks().get(0));
               }
            }
         }
      }

      stacks = stacks.stream().filter(stackx -> {
         String name = "Unknown";
         String id = "unknown";

         try {
            if (stackx.isEmpty()) {
               return false;
            }

            name = stackx.toString();
            id = stackx.getId().toString();
            if (name != null && stackx.getKey() != null && stackx.getName() != null) {
               return true;
            }

            EmiLog.warn("Hiding stack " + name + " with id " + id + " from index due to returning dangerous values");
            return false;
         } catch (Throwable t) {
            EmiLog.error("Hiding stack " + name + " with id " + id + " from index due to throwing errors", t);
            return false;
         }
      }).toList();

      for (int i = 0; i < stacks.size(); i++) {
         EmiStack stack = stacks.get(i);
         strictIndices.put(stack, i);
         keyIndices.put(stack.getKey(), i);
      }

      bakeFiltered();
   }

   public static void bakeFiltered() {
      filteredStacks = stacks.stream().filter(s -> !EmiHidden.isDisabled(s) && !EmiHidden.isHidden(s)).toList();
   }

   public static int getIndex(EmiIngredient ingredient) {
      EmiStack stack = ingredient.getEmiStacks().get(0);
      int ret = strictIndices.getOrDefault(stack, Integer.MAX_VALUE);
      if (ret == Integer.MAX_VALUE) {
         ret = keyIndices.getOrDefault(stack, ret);
      }

      return ret;
   }

   public static class ComparisonHashStrategy implements Strategy<EmiStack> {
      public boolean equals(EmiStack a, EmiStack b) {
         if (a == b) {
            return true;
         } else if (a == null || b == null) {
            return false;
         } else {
            return a.isEmpty() && b.isEmpty() ? true : a.isEqual(b, EmiComparisonDefaults.get(a.getKey()));
         }
      }

      public int hashCode(EmiStack stack) {
         if (stack != null) {
            int i = 31 + stack.getKey().hashCode();
            return 31 * i + EmiComparisonDefaults.get(stack.getKey()).getHash(stack);
         } else {
            return 0;
         }
      }

      // $VF: synthetic method
      // $VF: bridge method
      public boolean equals(Object param1, Object nullx) {
         return this.equals((EmiStack)var1, (EmiStack)nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      public int hashCode(Object param1) {
         return this.hashCode((EmiStack)var1);
      }
   }

   public static class IndexGroup {
      public List<EmiStack> stacks = Lists.newArrayList();
      public Set<EmiStackList.IndexGroup> suppressedBy = Sets.newHashSet();

      public boolean shouldDisplay() {
         for (EmiStackList.IndexGroup suppressor : this.suppressedBy) {
            if (suppressor.shouldDisplay()) {
               return false;
            }
         }

         return true;
      }
   }

   public static class StrictHashStrategy implements Strategy<EmiStack> {
      public boolean equals(EmiStack a, EmiStack b) {
         if (a == b) {
            return true;
         } else if (a == null || b == null) {
            return false;
         } else {
            return a.isEmpty() && b.isEmpty() ? true : a.isEqual(b, EmiPort.compareStrict());
         }
      }

      public int hashCode(EmiStack stack) {
         if (stack != null) {
            DataComponentPatch changes = stack.getComponentChanges();
            int i = 31 + stack.getKey().hashCode();
            return 31 * i + changes.hashCode();
         } else {
            return 0;
         }
      }

      // $VF: synthetic method
      // $VF: bridge method
      public boolean equals(Object param1, Object nullx) {
         return this.equals((EmiStack)var1, (EmiStack)nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      public int hashCode(Object param1) {
         return this.hashCode((EmiStack)var1);
      }
   }
}
