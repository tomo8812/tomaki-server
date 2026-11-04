package dev.emi.emi.jemi.impl;

import com.google.common.collect.Lists;
import dev.emi.emi.runtime.EmiLog;
import java.util.Collection;
import java.util.List;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class JemiTooltipBuilder implements ITooltipBuilder {
   public final List<ClientTooltipComponent> tooltip = Lists.newArrayList();
   private final List<Component> legacyText = Lists.newArrayList();

   public void add(FormattedText component) {
      if (component instanceof Component text) {
         this.tooltip.add(ClientTooltipComponent.create(text.getVisualOrderText()));
         this.legacyText.add(text);
      }
   }

   public void addAll(Collection<? extends FormattedText> components) {
      for (FormattedText v : components) {
         this.add(v);
      }
   }

   public void add(TooltipComponent data) {
      try {
         this.tooltip.add(ClientTooltipComponent.create(data));
      } catch (Exception e) {
         EmiLog.error("Error converting TooltipComponent", e);
      }
   }

   public void setIngredient(ITypedIngredient<?> typedIngredient) {
   }

   public void clear() {
   }

   public List<Component> toLegacyToComponents() {
      return this.legacyText;
   }

   public void removeAll(List<Component> components) {
   }
}
