package dev.emi.emi.jemi;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.jemi.impl.JemiTooltipBuilder;
import dev.emi.emi.runtime.EmiDrawContext;
import java.util.List;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;

public class JemiStack<T> extends EmiStack {
   private final IIngredientType<T> type;
   private final IIngredientHelper<T> helper;
   public final Object base;
   public final T ingredient;
   public IIngredientRenderer<T> renderer;

   public JemiStack(IIngredientType<T> type, IIngredientHelper<T> helper, IIngredientRenderer<T> renderer, T ingredient) {
      this.type = type;
      this.helper = helper;
      this.renderer = renderer;
      this.ingredient = ingredient;
      if (type instanceof IIngredientTypeWithSubtypes<?, T> iitws) {
         this.base = iitws.getBase(ingredient);
      } else {
         this.base = helper.getUniqueId(ingredient, UidContext.Recipe);
      }
   }

   public String getJeiUid() {
      return this.helper.getUniqueId(this.ingredient, UidContext.Ingredient);
   }

   @Override
   public void render(GuiGraphics raw, int x, int y, float delta, int flags) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      int xOff = (16 - this.renderer.getWidth()) / 2;
      int yOff = (16 - this.renderer.getHeight()) / 2;
      context.push();
      context.matrices().translate(x + xOff, y + yOff, 0.0F);
      this.renderer.render(context.raw(), this.ingredient);
      context.pop();
   }

   public JemiStack<T> copy() {
      return new JemiStack<>(this.type, this.helper, this.renderer, (T)this.helper.copyIngredient(this.ingredient));
   }

   @Override
   public boolean isEmpty() {
      return !this.helper.isValidIngredient(this.ingredient);
   }

   @Override
   public DataComponentPatch getComponentChanges() {
      return DataComponentPatch.EMPTY;
   }

   @Override
   public Object getKey() {
      return this.base;
   }

   @Override
   public ResourceLocation getId() {
      return this.helper.getResourceLocation(this.ingredient);
   }

   @Override
   public List<Component> getTooltipText() {
      return this.renderer.getTooltip(this.ingredient, TooltipFlag.NORMAL);
   }

   @Override
   public List<ClientTooltipComponent> getTooltip() {
      List<ClientTooltipComponent> list = Lists.newArrayList();
      Minecraft client = Minecraft.getInstance();
      JemiTooltipBuilder builder = new JemiTooltipBuilder();
      this.renderer.getTooltip(builder, this.ingredient, client.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
      list.addAll(builder.tooltip);
      ResourceLocation id = this.getId();
      if (EmiConfig.appendModId && id != null) {
         String mod = EmiUtil.getModName(id.getNamespace());
         list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.literal(mod, ChatFormatting.BLUE, ChatFormatting.ITALIC))));
      }

      list.addAll(super.getTooltip());
      return list;
   }

   @Override
   public Component getName() {
      return EmiPort.literal(this.helper.getDisplayName(this.ingredient));
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public EmiStack copy() {
      return this.copy();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public EmiIngredient copy() {
      return this.copy();
   }
}
