package com.chaosthedude.naturescompass.gui;

import com.chaosthedude.naturescompass.NaturesCompass;
import com.chaosthedude.naturescompass.sorting.DimensionSorting;
import com.chaosthedude.naturescompass.sorting.NameSorting;
import com.chaosthedude.naturescompass.sorting.SourceSorting;
import com.chaosthedude.naturescompass.sorting.TagsSorting;
import com.chaosthedude.naturescompass.util.BiomeUtils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList.Entry;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BiomeSearchEntry extends Entry<BiomeSearchEntry> {
   private static final ResourceLocation[] ENABLED_LEVEL_SPRITES = new ResourceLocation[]{
      ResourceLocation.withDefaultNamespace("container/enchanting_table/level_1"),
      ResourceLocation.withDefaultNamespace("container/enchanting_table/level_2"),
      ResourceLocation.withDefaultNamespace("container/enchanting_table/level_3")
   };
   private static final ResourceLocation[] DISABLED_LEVEL_SPRITES = new ResourceLocation[]{
      ResourceLocation.withDefaultNamespace("container/enchanting_table/level_1_disabled"),
      ResourceLocation.withDefaultNamespace("container/enchanting_table/level_2_disabled"),
      ResourceLocation.withDefaultNamespace("container/enchanting_table/level_3_disabled")
   };
   private final Minecraft mc;
   private final NaturesCompassScreen parentScreen;
   private final Biome biome;
   private final ResourceLocation biomeId;
   private final Player player;
   private final BiomeSearchList biomesList;
   private final String tags;
   private int xpLevels;
   private long lastClickTime;

   public BiomeSearchEntry(BiomeSearchList biomesList, Biome biome, Player player) {
      this.biomesList = biomesList;
      this.biome = biome;
      this.player = player;
      this.parentScreen = biomesList.getParentScreen();
      this.mc = Minecraft.getInstance();
      this.tags = BiomeUtils.getBiomeTags(this.parentScreen.level, biome);
      this.biomeId = BiomeUtils.getKeyForBiome(this.parentScreen.level, biome).orElse(null);
      this.xpLevels = 0;
      if (this.biomeId != null && NaturesCompass.xpLevelsForAllowedBiomes.containsKey(this.biomeId)) {
         int levels = NaturesCompass.xpLevelsForAllowedBiomes.get(this.biomeId);
         if (levels > 3) {
            levels = 3;
         }

         this.xpLevels = levels;
      }
   }

   public void render(
      GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovering, float partialTick
   ) {
      String title = this.parentScreen.getSortingCategory().getLocalizedName();
      Object value = this.parentScreen.getSortingCategory().getValue(this.biome);
      if (this.parentScreen.getSortingCategory() instanceof NameSorting
         || this.parentScreen.getSortingCategory() instanceof SourceSorting
         || this.parentScreen.getSortingCategory() instanceof TagsSorting
         || this.parentScreen.getSortingCategory() instanceof DimensionSorting) {
         title = I18n.get("string.naturescompass.dimension", new Object[0]);
         if (this.biomeId != null) {
            value = BiomeUtils.dimensionKeysToString(NaturesCompass.dimensionKeysForAllowedBiomeKeys.get(this.biomeId));
         } else {
            value = "";
         }
      }

      int maxTextWidth = width - 10;
      if (this.xpLevels > 0) {
         int itemHeight = height + 4;
         int spriteSize = (int)(itemHeight * 0.4F);
         int spriteBorder = (itemHeight - spriteSize) / 2;
         int spriteIndex = this.xpLevels - 1;
         ResourceLocation spriteId = this.isEnabled() ? ENABLED_LEVEL_SPRITES[spriteIndex] : DISABLED_LEVEL_SPRITES[spriteIndex];
         guiGraphics.blitSprite(spriteId, left + width - spriteSize - spriteBorder, top + spriteBorder - 2, spriteSize, spriteSize);
         maxTextWidth = width - itemHeight - 5;
      }

      String tagsLine = I18n.get("string.naturescompass.tags", new Object[0]) + ": " + this.tags;
      if (this.mc.font.width(tagsLine) > maxTextWidth) {
         tagsLine = this.mc.font.plainSubstrByWidth(tagsLine + "...", maxTextWidth) + "...";
      }

      int nameColor = this.isEnabled() ? -1 : -8355712;
      int infoColor = this.isEnabled() ? -8355712 : -11184811;
      guiGraphics.drawString(
         this.mc.font,
         Component.literal(BiomeUtils.getBiomeNameForDisplay(this.parentScreen.level, this.biome)),
         left + 5,
         top + height / 2 - (9 + 2) * 2,
         nameColor
      );
      guiGraphics.drawString(this.mc.font, Component.literal(title + ": " + value), left + 5, top + height / 2 - (9 + 2) * 1, infoColor);
      guiGraphics.drawString(this.mc.font, Component.literal(tagsLine), left + 5, top + height / 2 + (9 + 2) * 0, infoColor);
      guiGraphics.drawString(
         this.mc.font,
         Component.translatable("string.naturescompass.source")
            .append(Component.literal(": " + BiomeUtils.getBiomeSource(this.parentScreen.level, this.biome))),
         left + 5,
         top + height / 2 + (9 + 2) * 1,
         infoColor
      );
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0 && this.isEnabled()) {
         this.biomesList.setSelected(this);
         if (Util.getMillis() - this.lastClickTime < 250L) {
            this.parentScreen.searchForBiome(this.biomeId);
         }

         this.lastClickTime = Util.getMillis();
         return true;
      } else {
         return false;
      }
   }

   public boolean isEnabled() {
      return NaturesCompass.infiniteXp || this.player.experienceLevel >= this.xpLevels;
   }

   public ResourceLocation getBiomeId() {
      return this.biomeId;
   }

   public Component getNarration() {
      return Component.literal(BiomeUtils.getBiomeNameForDisplay(this.parentScreen.level, this.biome));
   }
}
