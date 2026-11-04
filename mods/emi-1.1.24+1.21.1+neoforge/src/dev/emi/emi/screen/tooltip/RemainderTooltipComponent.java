package dev.emi.emi.screen.tooltip;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.runtime.EmiDrawContext;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class RemainderTooltipComponent implements EmiTooltipComponent {
   public List<RemainderTooltipComponent.Remainder> remainders = Lists.newArrayList();

   public RemainderTooltipComponent(EmiIngredient ingredient) {
      Map<Integer, List<EmiStack>> tools = Maps.newHashMap();

      label39:
      for (EmiStack stack : ingredient.getEmiStacks()) {
         int damage = this.getDamageDelta(stack, stack.getRemainder());
         if (damage != 0) {
            tools.computeIfAbsent(damage, i -> Lists.newArrayList()).add(stack);
         } else {
            for (RemainderTooltipComponent.Remainder remainder : this.remainders) {
               if (remainder.remainder.isEqual(stack.getRemainder())) {
                  remainder.inputs.add(stack);
                  continue label39;
               }
            }

            if (!stack.getRemainder().isEmpty()) {
               this.remainders.add(new RemainderTooltipComponent.Remainder(stack, stack.getRemainder()));
            }
         }
      }

      for (Entry<Integer, List<EmiStack>> entry : tools.entrySet()) {
         this.remainders.add(new RemainderTooltipComponent.Remainder(entry.getValue(), entry.getKey()));
      }
   }

   public int getHeight() {
      return 18 * this.remainders.size();
   }

   public int getWidth(Font var1) {
      return 54;
   }

   @Override
   public void drawTooltip(EmiDrawContext context, EmiTooltipComponent.TooltipRenderData render) {
      for (int i = 0; i < this.remainders.size(); i++) {
         RemainderTooltipComponent.Remainder remainder = this.remainders.get(i);
         EmiIngredient input = EmiIngredient.of(remainder.inputs);
         context.drawStack(input, 0, 18 * i, -11);
         if (remainder.damage == 0) {
            context.drawStack(remainder.remainder, 36, 18 * i, -9);
         } else {
            context.drawStack(input, 36, 18 * i, 3);
            ItemStack is = input.getEmiStacks().get(0).getItemStack().copy();
            is.setDamageValue(is.getDamageValue() - remainder.damage);
            context.raw().renderItemDecorations(render.text, is, 36, 18 * i, "");
            context.drawStack(input, 36, 18 * i, -12);
            Component t = remainder.damage > 0
               ? EmiPort.literal("+" + remainder.damage, ChatFormatting.GREEN)
               : EmiPort.literal(remainder.damage + "", ChatFormatting.RED);
            int width = render.text.width(t);
            context.push();
            context.matrices().translate(0.0F, 0.0F, 200.0F);
            context.drawText(t, 42 - width, i * 18);
            context.pop();
         }
      }
   }

   @Override
   public void drawTooltipText(EmiTooltipComponent.TextRenderData text) {
      for (int i = 0; i < this.remainders.size(); i++) {
         RemainderTooltipComponent.Remainder remainder = this.remainders.get(i);
         boolean chanced = remainder.chance != 1.0F;
         text.draw(EmiPort.literal("->"), 20, 5 + i * 18 - (chanced ? 4 : 0), 16777215, true);
         if (chanced) {
            Component t = EmiPort.literal(EmiTooltip.TEXT_FORMAT.format(remainder.chance * 100.0F) + "%");
            int tx = text.renderer.width(t);
            text.draw(t, 27 - tx / 2, 9 + i * 18, ChatFormatting.GOLD.getColor(), false);
         }
      }
   }

   private int getDamageDelta(EmiStack stack, EmiStack remainder) {
      return remainder.isEqual(stack) ? stack.getItemStack().getDamageValue() - remainder.getItemStack().getDamageValue() : 0;
   }

   private static class Remainder {
      public final List<EmiStack> inputs = Lists.newArrayList();
      public final EmiStack remainder;
      public int damage = 0;
      public float chance = 1.0F;

      public Remainder(EmiStack input, EmiStack remainder) {
         this.inputs.add(input);
         this.remainder = remainder;
         this.chance = remainder.getChance();
      }

      public Remainder(List<EmiStack> inputs, int damage) {
         this.inputs.addAll(inputs);
         this.remainder = EmiStack.EMPTY;
         this.damage = damage;
      }
   }
}
