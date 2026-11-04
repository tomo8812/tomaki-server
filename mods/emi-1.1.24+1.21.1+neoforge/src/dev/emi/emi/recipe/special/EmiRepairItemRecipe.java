package dev.emi.emi.recipe.special;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class EmiRepairItemRecipe extends EmiPatternCraftingRecipe {
   public static final List<Item> TOOLS = EmiPort.getItemRegistry()
      .stream()
      .filter(i -> (Integer)i.components().getOrDefault(DataComponents.MAX_DAMAGE, 0) > 0)
      .collect(Collectors.toList());
   private final Item tool;

   public EmiRepairItemRecipe(Item tool, ResourceLocation id) {
      super(List.of(EmiStack.of(tool), EmiStack.of(tool)), EmiStack.of(tool), id);
      this.tool = tool;
   }

   @Override
   public SlotWidget getInputWidget(int slot, int x, int y) {
      return new GeneratedSlotWidget(r -> {
         List<ItemStack> items = this.getItems(r);
         return slot < 2 ? EmiStack.of(items.get(slot)) : EmiStack.EMPTY;
      }, this.unique, x, y);
   }

   @Override
   public SlotWidget getOutputWidget(int x, int y) {
      return new GeneratedSlotWidget(r -> EmiStack.of(this.getMergeItems(r)), this.unique, x, y);
   }

   private List<ItemStack> getItems(Random random) {
      List<ItemStack> items = Lists.newArrayList();
      items.add(this.getTool(random));
      items.add(this.getTool(random));
      return items;
   }

   private ItemStack getMergeItems(Random random) {
      List<ItemStack> items = this.getItems(random);
      ItemStack item = this.tool.getDefaultInstance();
      int maxDamage = item.getMaxDamage();
      int damage = items.get(0).getDamageValue() - 21 * maxDamage / 20 + items.get(1).getDamageValue();
      if (damage > 0) {
         item.setDamageValue(damage);
      }

      return item;
   }

   private ItemStack getTool(Random r) {
      ItemStack stack = this.tool.getDefaultInstance();
      if (stack.getMaxDamage() <= 0) {
         return stack;
      }

      int d = r.nextInt(stack.getMaxDamage());
      stack.setDamageValue(d);
      return stack;
   }
}
