package dev.emi.emi.recipe.special;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import java.util.List;
import java.util.Random;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class EmiBannerDuplicateRecipe extends EmiPatternCraftingRecipe {
   public static final List<Item> BANNERS = List.of(
      Items.WHITE_BANNER,
      Items.ORANGE_BANNER,
      Items.MAGENTA_BANNER,
      Items.LIGHT_BLUE_BANNER,
      Items.YELLOW_BANNER,
      Items.LIME_BANNER,
      Items.PINK_BANNER,
      Items.GRAY_BANNER,
      Items.LIGHT_GRAY_BANNER,
      Items.CYAN_BANNER,
      Items.PURPLE_BANNER,
      Items.BLUE_BANNER,
      Items.BROWN_BANNER,
      Items.GREEN_BANNER,
      Items.RED_BANNER,
      Items.BLACK_BANNER
   );
   private final Item banner;

   public EmiBannerDuplicateRecipe(Item banner, ResourceLocation id) {
      super(List.of(EmiStack.of(banner), EmiStack.of(banner).setRemainder(EmiStack.of(banner))), EmiStack.of(banner), id);
      this.banner = banner;
   }

   @Override
   public SlotWidget getInputWidget(int slot, int x, int y) {
      if (slot == 0) {
         return new SlotWidget(EmiStack.of(this.banner), x, y);
      } else {
         return slot == 1 ? new GeneratedSlotWidget(r -> this.getPattern(r, true), this.unique, x, y) : new SlotWidget(EmiStack.EMPTY, x, y);
      }
   }

   @Override
   public SlotWidget getOutputWidget(int x, int y) {
      return new GeneratedSlotWidget(r -> this.getPattern(r, false), this.unique, x, y);
   }

   public EmiStack getPattern(Random random, boolean reminder) {
      ItemStack stack = new ItemStack(this.banner);
      int patterns = 1 + Math.max(random.nextInt(5), random.nextInt(3));
      BannerPatternLayers pattern = BannerPatternLayers.EMPTY;

      for (int i = 0; i < patterns; i++) {
         pattern = EmiPort.addRandomBanner(pattern, random);
      }

      stack.set(DataComponents.BANNER_PATTERNS, pattern);
      EmiStack emiStack = EmiStack.of(stack);
      if (reminder) {
         emiStack.setRemainder(EmiStack.of(stack));
      }

      return emiStack;
   }
}
