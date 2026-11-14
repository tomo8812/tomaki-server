package com.chaosthedude.naturescompass.gui;

import com.chaosthedude.naturescompass.util.RenderUtils;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.AbstractSelectionList.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BiomeSearchList extends ObjectSelectionList<BiomeSearchEntry> {
   private final NaturesCompassScreen parentScreen;
   private final Player player;

   public BiomeSearchList(
      NaturesCompassScreen parentScreen, Minecraft mc, Player player, ResourceLocation biomeIdToSelect, int x, int y, int width, int height, int itemHeight
   ) {
      super(mc, width, height, y, itemHeight);
      this.parentScreen = parentScreen;
      this.player = player;
      this.setX(x);
      this.refreshList(biomeIdToSelect);
   }

   protected int getScrollbarPosition() {
      return this.getX() + this.getWidth();
   }

   public int getRowWidth() {
      return this.getWidth();
   }

   public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      this.enableScissor(guiGraphics);

      for (int i = 0; i < this.getItemCount(); i++) {
         if (this.getRowBottom(i) >= this.getY() && this.getRowTop(i) <= this.getBottom()) {
            BiomeSearchEntry entry = (BiomeSearchEntry)this.getEntry(i);
            int fillColor = RenderUtils.getBackgroundColor(entry.isEnabled(), entry == this.getSelected());
            guiGraphics.fill(this.getRowLeft(), this.getRowTop(i), this.getRight(), this.getRowBottom(i), fillColor);
         }
      }

      for (int i = 0; i < this.getItemCount(); i++) {
         int top = this.getRowTop(i);
         int bottom = this.getRowBottom(i);
         if (bottom >= this.getY() && top <= this.getBottom()) {
            BiomeSearchEntry entry = (BiomeSearchEntry)this.getEntry(i);
            boolean isHovering = this.isMouseOver(mouseX, mouseY) && Objects.equals(this.getEntryAtPosition(mouseX, mouseY), entry);
            entry.render(guiGraphics, i, top, this.getRowLeft(), this.getRowWidth(), this.itemHeight, mouseX, mouseY, isHovering, partialTicks);
         }
      }

      guiGraphics.disableScissor();
      if (this.getMaxScroll() > 0) {
         int left = this.getScrollbarPosition();
         int right = left + 6;
         int height = (int)((float)((this.getBottom() - this.getY()) * (this.getBottom() - this.getY())) / this.getMaxPosition());
         height = Mth.clamp(height, 32, this.getBottom() - this.getY() - 8);
         int top = (int)this.getScrollAmount() * (this.getBottom() - this.getY() - height) / this.getMaxScroll() + this.getY();
         if (top < this.getY()) {
            top = this.getY();
         }

         guiGraphics.fill(left, this.getY(), right, this.getBottom(), RenderUtils.getBackgroundColor(false, false));
         guiGraphics.fill(left, top, right, top + height, RenderUtils.getBackgroundColor(true, true));
      }
   }

   public void setSelected(BiomeSearchEntry entry) {
      if (entry == null || entry.isEnabled()) {
         super.setSelected(entry);
      }
   }

   public void refreshList(ResourceLocation biomeIdToSelect) {
      this.clearEntries();

      for (Biome biome : this.parentScreen.sortBiomes()) {
         BiomeSearchEntry entry = new BiomeSearchEntry(this, biome, this.player);
         this.addEntry(entry);
         ResourceLocation entryId = entry.getBiomeId();
         if (biomeIdToSelect != null && biomeIdToSelect.equals(entryId) && entry.isEnabled()) {
            super.setSelected(entry);
         }
      }

      this.setScrollAmount(0.0);
   }

   public void refreshList(boolean maintainSelection) {
      ResourceLocation select = maintainSelection && this.hasSelection() ? ((BiomeSearchEntry)this.getSelected()).getBiomeId() : null;
      this.refreshList(select);
   }

   public boolean hasSelection() {
      return this.getSelected() != null;
   }

   public NaturesCompassScreen getParentScreen() {
      return this.parentScreen;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void setSelected(Entry param1) {
      this.setSelected((BiomeSearchEntry)var1);
   }
}
