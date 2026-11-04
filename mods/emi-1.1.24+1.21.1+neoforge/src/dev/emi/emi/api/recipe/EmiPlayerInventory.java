package dev.emi.emi.api.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.registry.EmiRecipeFiller;
import dev.emi.emi.registry.EmiStackList;
import dev.emi.emi.runtime.EmiFavorite;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;

public class EmiPlayerInventory {
   private final Comparison none = Comparison.DEFAULT_COMPARISON;
   private final Comparison strict = EmiPort.compareStrict();
   public Map<EmiStack, EmiStack> inventory = Maps.newHashMap();

   @Deprecated
   @Internal
   public EmiPlayerInventory(Player entity) {
      AbstractContainerScreen<?> screen = EmiApi.getHandledScreen();
      if (screen != null && screen.getMenu() != null) {
         if (screen.getMenu().getCarried() != null) {
            this.addStack(screen.getMenu().getCarried());
         }

         List<EmiRecipeHandler<?>> handlers = EmiRecipeFiller.getAllHandlers(screen);
         if (!handlers.isEmpty() && handlers.get(0) instanceof StandardRecipeHandler standard) {
            for (Slot slot : standard.getInputSources(screen.getMenu())) {
               if (slot.mayPickup(entity)) {
                  this.addStack(slot.getItem());
               }
            }

            return;
         }
      }

      Inventory pInv = entity.getInventory();

      for (int i = 0; i < pInv.items.size(); i++) {
         this.addStack((ItemStack)pInv.items.get(i));
      }
   }

   public EmiPlayerInventory(List<EmiStack> stacks) {
      for (EmiStack stack : stacks) {
         this.addStack(stack);
      }

      AbstractContainerScreen<?> screen = EmiApi.getHandledScreen();
      if (screen != null && screen.getMenu() != null && screen.getMenu().getCarried() != null) {
         this.addStack(screen.getMenu().getCarried());
      }
   }

   public static EmiPlayerInventory of(Player entity) {
      AbstractContainerScreen<?> screen = EmiApi.getHandledScreen();
      if (screen != null) {
         List<EmiRecipeHandler<?>> handlers = EmiRecipeFiller.getAllHandlers(screen);
         if (!handlers.isEmpty()) {
            return handlers.get(0).getInventory(screen);
         }
      }

      return entity == null ? new EmiPlayerInventory(List.of()) : new EmiPlayerInventory(entity);
   }

   private void addStack(ItemStack is) {
      EmiStack stack = EmiStack.of(is).comparison(c -> this.none);
      this.addStack(stack);
   }

   private void addStack(EmiStack stack) {
      if (!stack.isEmpty()) {
         this.inventory.merge(stack, stack, (a, b) -> a.setAmount(a.getAmount() + b.getAmount()));
      }
   }

   public Predicate<EmiRecipe> getPredicate() {
      AbstractContainerScreen screen = EmiApi.getHandledScreen();
      List<EmiRecipeHandler> handlers = EmiRecipeFiller.getAllHandlers(screen);
      if (!handlers.isEmpty()) {
         EmiCraftContext context = new EmiCraftContext(screen, this, EmiCraftContext.Type.CRAFTABLE);
         return r -> {
            for (int i = 0; i < handlers.size(); i++) {
               EmiRecipeHandler handler = handlers.get(i);
               if (handler.supportsRecipe(r)) {
                  return handler.canCraft(r, context);
               }
            }

            return false;
         };
      } else {
         return null;
      }
   }

   public List<EmiIngredient> getCraftables() {
      Predicate<EmiRecipe> predicate = this.getPredicate();
      if (predicate == null) {
         return List.of();
      }

      Set<EmiRecipe> set = Sets.newHashSet();

      for (EmiStack stack : this.inventory.keySet()) {
         set.addAll(EmiApi.getRecipeManager().getRecipesByInput(stack));
      }

      return set.stream()
         .filter(r -> !r.hideCraftable() && predicate.test(r) && r.getOutputs().size() > 0)
         .map(r -> new EmiFavorite.Craftable(r))
         .sorted((a, b) -> {
            int i = Integer.compare(EmiStackList.getIndex(a.getStack()), EmiStackList.getIndex(b.getStack()));
            return i != 0 ? i : Long.compare(a.getAmount(), b.getAmount());
         })
         .collect(Collectors.toList());
   }

   public List<Boolean> getCraftAvailability(EmiRecipe recipe) {
      Object2LongMap<EmiStack> used = new Object2LongOpenHashMap();
      List<Boolean> states = Lists.newArrayList();

      label25:
      for (EmiIngredient ingredient : recipe.getInputs()) {
         for (EmiStack stack : ingredient.getEmiStacks()) {
            long desired = stack.getAmount();
            if (this.inventory.containsKey(stack)) {
               EmiStack identity = this.inventory.get(stack);
               long alreadyUsed = used.getOrDefault(identity, 0L);
               long available = identity.getAmount() - alreadyUsed;
               if (available >= desired) {
                  used.put(identity, desired + alreadyUsed);
                  states.add(true);
                  continue label25;
               }
            }
         }

         states.add(false);
      }

      return states;
   }

   public boolean canCraft(EmiRecipe recipe) {
      return this.canCraft(recipe, 1L);
   }

   public boolean canCraft(EmiRecipe recipe, long amount) {
      Object2LongMap<EmiStack> used = new Object2LongOpenHashMap();

      label31:
      for (EmiIngredient ingredient : recipe.getInputs()) {
         if (!ingredient.isEmpty()) {
            for (EmiStack stack : ingredient.getEmiStacks()) {
               long desired = stack.getAmount() * amount;
               if (this.inventory.containsKey(stack)) {
                  EmiStack identity = this.inventory.get(stack);
                  long alreadyUsed = used.getOrDefault(identity, 0L);
                  long available = identity.getAmount() - alreadyUsed;
                  if (available >= desired) {
                     used.put(identity, desired + alreadyUsed);
                     continue label31;
                  }
               }
            }

            return false;
         }
      }

      return true;
   }

   public boolean isEqual(EmiPlayerInventory other) {
      if (other == null) {
         return false;
      }

      Comparison comparison = Comparison.of((a, b) -> this.strict.compare(a, b) && a.getAmount() == b.getAmount());
      if (other.inventory.size() != this.inventory.size()) {
         return false;
      }

      for (EmiStack stack : this.inventory.keySet()) {
         if (!other.inventory.containsKey(stack) || !other.inventory.get(stack).isEqual(stack, comparison)) {
            return false;
         }
      }

      return true;
   }
}
