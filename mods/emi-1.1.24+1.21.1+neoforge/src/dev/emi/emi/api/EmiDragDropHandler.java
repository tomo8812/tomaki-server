package dev.emi.emi.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.mixin.accessor.HandledScreenAccessor;
import dev.emi.emi.runtime.EmiDrawContext;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public interface EmiDragDropHandler<T extends Screen> {
   boolean dropStack(T var1, EmiIngredient var2, int var3, int var4);

   default void render(T screen, EmiIngredient dragged, GuiGraphics draw, int mouseX, int mouseY, float delta) {
   }

   class BoundsBased<T extends Screen> implements EmiDragDropHandler<T> {
      private final Function<T, Map<Bounds, Consumer<EmiIngredient>>> bounds;

      public BoundsBased(Function<T, Map<Bounds, Consumer<EmiIngredient>>> bounds) {
         this.bounds = bounds;
      }

      public BoundsBased(BiConsumer<T, BiConsumer<Bounds, Consumer<EmiIngredient>>> bounds) {
         this.bounds = screen -> {
            Map<Bounds, Consumer<EmiIngredient>> map = Maps.newHashMap();
            bounds.accept(screen, (b, consumer) -> map.put(b, consumer));
            return map;
         };
      }

      @Override
      public boolean dropStack(T screen, EmiIngredient stack, int x, int y) {
         Map<Bounds, Consumer<EmiIngredient>> bounds = this.bounds.apply(screen);

         for (Bounds b : bounds.keySet()) {
            if (b.contains(x, y)) {
               bounds.get(b).accept(stack);
               return true;
            }
         }

         return false;
      }

      @Override
      public void render(T screen, EmiIngredient dragged, GuiGraphics draw, int mouseX, int mouseY, float delta) {
         EmiDrawContext context = EmiDrawContext.wrap(draw);

         for (Bounds b : this.bounds.apply(screen).keySet()) {
            context.fill(b.x(), b.y(), b.width(), b.height(), -2010989773);
         }
      }
   }

   class SlotBased<T extends AbstractContainerScreen<?>> extends EmiDragDropHandler.BoundsBased<T> {
      public SlotBased(Function<T, Collection<Slot>> slots, EmiDragDropHandler.SlotBased.TriConsumer<T, Slot, EmiIngredient> consumer) {
         super(t -> map(t, slots, consumer));
      }

      public SlotBased(BiPredicate<T, Slot> slotFilter, EmiDragDropHandler.SlotBased.TriConsumer<T, Slot, EmiIngredient> consumer) {
         super(t -> map(t, screen -> filter(screen, slotFilter), consumer));
      }

      private static <T extends AbstractContainerScreen<?>> Collection<Slot> filter(T t, BiPredicate<T, Slot> slotFilter) {
         List<Slot> slots = Lists.newArrayList();
         AbstractContainerMenu handler = t.getMenu();

         for (Slot slot : handler.slots) {
            if (slotFilter.test(t, slot)) {
               slots.add(slot);
            }
         }

         return slots;
      }

      private static <T extends AbstractContainerScreen<?>> Map<Bounds, Consumer<EmiIngredient>> map(
         T t, Function<T, Collection<Slot>> slots, EmiDragDropHandler.SlotBased.TriConsumer<T, Slot, EmiIngredient> consumer
      ) {
         Map<Bounds, Consumer<EmiIngredient>> map = Maps.newHashMap();

         for (Slot slot : slots.apply(t)) {
            map.put(
               new Bounds(((HandledScreenAccessor)t).getX() + slot.x - 1, ((HandledScreenAccessor)t).getY() + slot.y - 1, 18, 18),
               i -> consumer.accept(t, slot, i)
            );
         }

         return map;
      }

      public interface TriConsumer<A, B, C> {
         void accept(A var1, B var2, C var3);
      }
   }
}
