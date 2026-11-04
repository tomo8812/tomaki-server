package dev.emi.emi.api.recipe;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.data.EmiRecipeCategoryProperties;
import java.util.Comparator;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class EmiRecipeCategory implements EmiRenderable {
   public ResourceLocation id;
   public EmiRenderable icon;
   public EmiRenderable simplified;
   public Comparator<EmiRecipe> sorter;

   public EmiRecipeCategory(ResourceLocation id, EmiRenderable icon) {
      this(id, icon, icon);
   }

   public EmiRecipeCategory(ResourceLocation id, EmiRenderable icon, EmiRenderable simplified) {
      this(id, icon, simplified, EmiRecipeSorting.none());
   }

   public EmiRecipeCategory(ResourceLocation id, EmiRenderable icon, EmiRenderable simplified, Comparator<EmiRecipe> sorter) {
      this.id = id;
      this.icon = icon;
      this.simplified = simplified;
      this.sorter = sorter;
   }

   public Component getName() {
      return EmiPort.translatable(EmiUtil.translateId("emi.category.", this.getId()));
   }

   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public void render(GuiGraphics draw, int x, int y, float delta) {
      EmiRecipeCategoryProperties.getIcon(this).render(draw, x, y, delta);
   }

   public void renderSimplified(GuiGraphics draw, int x, int y, float delta) {
      EmiRecipeCategoryProperties.getSimplifiedIcon(this).render(draw, x, y, delta);
   }

   public List<ClientTooltipComponent> getTooltip() {
      List<ClientTooltipComponent> list = Lists.newArrayList();
      list.add(ClientTooltipComponent.create(EmiPort.ordered(this.getName())));
      if (EmiUtil.showAdvancedTooltips()) {
         list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.literal(this.id.toString(), ChatFormatting.DARK_GRAY))));
      }

      if (EmiConfig.appendModId) {
         list.add(
            ClientTooltipComponent.create(
               EmiPort.ordered(EmiPort.literal(EmiUtil.getModName(this.getId().getNamespace()), ChatFormatting.BLUE, ChatFormatting.ITALIC))
            )
         );
      }

      return list;
   }

   @Nullable
   public Comparator<EmiRecipe> getSort() {
      return this.sorter;
   }
}
