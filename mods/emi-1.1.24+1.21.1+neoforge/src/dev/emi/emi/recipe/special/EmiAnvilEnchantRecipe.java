package dev.emi.emi.recipe.special;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;

public class EmiAnvilEnchantRecipe implements EmiRecipe {
   private final Item tool;
   private final Enchantment enchantment;
   private final int level;
   private final ResourceLocation id;

   public EmiAnvilEnchantRecipe(Item tool, Enchantment enchantment, int level, ResourceLocation id) {
      this.tool = tool;
      this.enchantment = enchantment;
      this.level = level;
      this.id = id;
   }

   @Override
   public EmiRecipeCategory getCategory() {
      return VanillaEmiRecipeCategories.ANVIL_REPAIRING;
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public List<EmiIngredient> getInputs() {
      return List.of(EmiStack.of(this.tool), this.getBook());
   }

   @Override
   public List<EmiStack> getOutputs() {
      return List.of(EmiStack.of(this.tool));
   }

   @Override
   public boolean supportsRecipeTree() {
      return false;
   }

   @Override
   public int getDisplayWidth() {
      return 125;
   }

   @Override
   public int getDisplayHeight() {
      return 18;
   }

   @Override
   public void addWidgets(WidgetHolder widgets) {
      widgets.addTexture(EmiTexture.PLUS, 27, 3);
      widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
      widgets.addSlot(EmiStack.of(this.tool), 0, 0);
      widgets.addSlot(this.getBook(), 49, 0);
      widgets.addSlot(EmiStack.of(this.getTool()), 107, 0).recipeContext(this);
   }

   private ItemStack getTool() {
      ItemStack itemStack = this.tool.getDefaultInstance();
      itemStack.enchant(EmiPort.getEnchantmentRegistry().wrapAsHolder(this.enchantment), this.level);
      return itemStack;
   }

   private EmiStack getBook() {
      ItemStack item = new ItemStack(Items.ENCHANTED_BOOK);
      Mutable enchBuilder = new Mutable(ItemEnchantments.EMPTY);
      enchBuilder.upgrade(EmiPort.getEnchantmentRegistry().wrapAsHolder(this.enchantment), this.level);
      item.set(DataComponents.STORED_ENCHANTMENTS, enchBuilder.toImmutable());
      return EmiStack.of(item);
   }
}
