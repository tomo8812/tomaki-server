package net.satisfy.farm_and_charm.core.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.client.gui.CookingPotGui;
import net.satisfy.farm_and_charm.client.gui.RoasterGui;
import net.satisfy.farm_and_charm.client.gui.StoveGui;
import net.satisfy.farm_and_charm.core.block.entity.CookingPotBlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.RoasterBlockEntity;
import net.satisfy.farm_and_charm.core.recipe.CookingPotRecipe;
import net.satisfy.farm_and_charm.core.recipe.CraftingBowlRecipe;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardAssemblyRecipe;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardRecipe;
import net.satisfy.farm_and_charm.core.recipe.MincerRecipe;
import net.satisfy.farm_and_charm.core.recipe.RoasterRecipe;
import net.satisfy.farm_and_charm.core.recipe.SiloRecipe;
import net.satisfy.farm_and_charm.core.recipe.StoveRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ScreenhandlerTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import net.satisfy.farm_and_charm.core.util.Strippables;
import net.satisfy.foundation.compat.RecipeViewerLayout;
import net.satisfy.foundation.compat.RecipeViewerLayout.AssemblyLayout;
import net.satisfy.foundation.compat.RecipeViewerLayout.Pos;

@EmiEntrypoint
public class FarmAndCharmEmiPlugin implements EmiPlugin {
   public static final FarmAndCharmEmiCategory COOKING_POT = new FarmAndCharmEmiCategory(
      "cooking_pot", (ItemLike)ObjectRegistry.COOKING_POT.get(), "rei.farm_and_charm.cooking_pot_category"
   );
   public static final FarmAndCharmEmiCategory STOVE = new FarmAndCharmEmiCategory(
      "stove", (ItemLike)ObjectRegistry.STOVE.get(), "rei.farm_and_charm.stove_category"
   );
   public static final FarmAndCharmEmiCategory DOUGHING = new FarmAndCharmEmiCategory(
      "doughing", (ItemLike)ObjectRegistry.CRAFTING_BOWL.get(), "rei.farm_and_charm.bowl_category"
   );
   public static final FarmAndCharmEmiCategory ROASTER = new FarmAndCharmEmiCategory(
      "roaster", (ItemLike)ObjectRegistry.ROASTER.get(), "rei.farm_and_charm.roaster_category"
   );
   public static final FarmAndCharmEmiCategory DRYING = new FarmAndCharmEmiCategory(
      "drying", (ItemLike)ObjectRegistry.SILO_WOOD.get(), "rei.farm_and_charm.silo_category"
   );
   public static final FarmAndCharmEmiCategory MINCING = new FarmAndCharmEmiCategory(
      "mincer", (ItemLike)ObjectRegistry.MINCER.get(), "rei.farm_and_charm.mincer_category"
   );
   public static final FarmAndCharmEmiCategory CUTTING_BOARD = new FarmAndCharmEmiCategory(
      "cutting_board", (ItemLike)ObjectRegistry.CUTTING_BOARD.get(), "category.farm_and_charm.cutting_board"
   );
   public static final FarmAndCharmEmiCategory ASSEMBLY = new FarmAndCharmEmiCategory(
      "cutting_board_assembly", (ItemLike)ObjectRegistry.CUTTING_BOARD.get(), "category.farm_and_charm.assembly"
   );
   public static final FarmAndCharmEmiCategory LOG_STRIPPING = new FarmAndCharmEmiCategory(
      "log_stripping", Items.IRON_AXE, "category.farm_and_charm.log_stripping"
   );
   private static final int MACHINE_WIDTH = 124;
   private static final int MACHINE_HEIGHT = 60;
   private static final int OFFSET_X = 26;
   private static final int OFFSET_Y = 13;
   private static final int SIMPLE_WIDTH = 150;
   private static final int SIMPLE_HEIGHT = 50;
   private static final int SIMPLE_INPUT_X = 39;
   private static final int SIMPLE_ROW_Y = 16;
   private static final int ROW_INPUT_X = 10;
   private static final int ROW_ARROW_X = 34;
   private static final int ROW_OUTPUT_X = 64;
   private static final int ROW_Y = 6;
   private static final ResourceLocation CRAFTING_BOWL_TEXTURE = FarmAndCharm.identifier("textures/gui/crafting_bowl.png");

   public void register(EmiRegistry registry) {
      registry.addCategory(COOKING_POT);
      registry.addCategory(STOVE);
      registry.addCategory(DOUGHING);
      registry.addCategory(ROASTER);
      registry.addCategory(DRYING);
      registry.addCategory(MINCING);
      registry.addCategory(CUTTING_BOARD);
      registry.addCategory(ASSEMBLY);
      registry.addCategory(LOG_STRIPPING);
      registry.addWorkstation(COOKING_POT, EmiStack.of((ItemLike)ObjectRegistry.COOKING_POT.get()));
      registry.addWorkstation(STOVE, EmiStack.of((ItemLike)ObjectRegistry.STOVE.get()));
      registry.addWorkstation(DOUGHING, EmiStack.of((ItemLike)ObjectRegistry.CRAFTING_BOWL.get()));
      registry.addWorkstation(ROASTER, EmiStack.of((ItemLike)ObjectRegistry.ROASTER.get()));
      registry.addWorkstation(DRYING, EmiStack.of((ItemLike)ObjectRegistry.SILO_WOOD.get()));
      registry.addWorkstation(DRYING, EmiStack.of((ItemLike)ObjectRegistry.SILO_COPPER.get()));
      registry.addWorkstation(MINCING, EmiStack.of((ItemLike)ObjectRegistry.MINCER.get()));
      registry.addWorkstation(CUTTING_BOARD, EmiStack.of((ItemLike)ObjectRegistry.CUTTING_BOARD.get()));
      registry.addWorkstation(CUTTING_BOARD, EmiStack.of((ItemLike)ObjectRegistry.IRON_CLEAVER.get()));
      registry.addWorkstation(CUTTING_BOARD, EmiStack.of((ItemLike)ObjectRegistry.DIAMOND_CLEAVER.get()));
      registry.addWorkstation(CUTTING_BOARD, EmiStack.of((ItemLike)ObjectRegistry.NETHERITE_CLEAVER.get()));
      registry.addWorkstation(ASSEMBLY, EmiStack.of((ItemLike)ObjectRegistry.CUTTING_BOARD.get()));
      registry.addWorkstation(LOG_STRIPPING, EmiStack.of((ItemLike)ObjectRegistry.CUTTING_BOARD.get()));
      registry.addWorkstation(LOG_STRIPPING, EmiStack.of(Items.IRON_AXE));
      registry.addRecipeHandler((MenuType)ScreenhandlerTypeRegistry.COOKING_POT_SCREEN_HANDLER.get(), new FarmAndCharmEmiRecipeHandler(COOKING_POT, 1, 6, 0, 8));
      registry.addRecipeHandler((MenuType)ScreenhandlerTypeRegistry.ROASTER_SCREEN_HANDLER.get(), new FarmAndCharmEmiRecipeHandler(ROASTER, 1, 6, 0, 8));
      registry.addRecipeHandler((MenuType)ScreenhandlerTypeRegistry.STOVE_SCREEN_HANDLER.get(), new FarmAndCharmEmiRecipeHandler(STOVE, 1, 3, -1, 5));
      RecipeManager manager = registry.getRecipeManager();
      RegistryAccess access = Objects.requireNonNull(Minecraft.getInstance().level).registryAccess();

      for (RecipeHolder<CookingPotRecipe> holder : manager.getAllRecipesFor((RecipeType)RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.get())) {
         registry.addRecipe(cookingPot(holder, access));
      }

      for (RecipeHolder<RoasterRecipe> holder : manager.getAllRecipesFor((RecipeType)RecipeTypeRegistry.ROASTER_RECIPE_TYPE.get())) {
         registry.addRecipe(roaster(holder, access));
      }

      for (RecipeHolder<StoveRecipe> holder : manager.getAllRecipesFor((RecipeType)RecipeTypeRegistry.STOVE_RECIPE_TYPE.get())) {
         registry.addRecipe(stove(holder, access));
      }

      for (RecipeHolder<CraftingBowlRecipe> holder : manager.getAllRecipesFor((RecipeType)RecipeTypeRegistry.CRAFTING_BOWL_RECIPE_TYPE.get())) {
         registry.addRecipe(craftingBowl(holder, access));
      }

      for (RecipeHolder<SiloRecipe> holder : manager.getAllRecipesFor((RecipeType)RecipeTypeRegistry.SILO_RECIPE_TYPE.get())) {
         registry.addRecipe(simple(DRYING, holder.id(), ((SiloRecipe)holder.value()).getIngredients(), ((SiloRecipe)holder.value()).getOutput()));
      }

      for (RecipeHolder<MincerRecipe> holder : manager.getAllRecipesFor((RecipeType)RecipeTypeRegistry.MINCER_RECIPE_TYPE.get())) {
         registry.addRecipe(simple(MINCING, holder.id(), ((MincerRecipe)holder.value()).getIngredients(), ((MincerRecipe)holder.value()).getOutput()));
      }

      for (RecipeHolder<CuttingBoardRecipe> holder : manager.getAllRecipesFor((RecipeType)RecipeTypeRegistry.CUTTING_BOARD_RECIPE_TYPE.get())) {
         registry.addRecipe(cuttingBoard(holder));
      }

      for (RecipeHolder<CuttingBoardAssemblyRecipe> holder : manager.getAllRecipesFor((RecipeType)RecipeTypeRegistry.CUTTING_BOARD_ASSEMBLY_RECIPE_TYPE.get())) {
         registry.addRecipe(assembly(holder));
      }

      for (Strippables.Entry entry : Strippables.all()) {
         registry.addRecipe(stripping(entry));
      }

      registry.removeEmiStacks(stack -> stack.getItemStack().is(TagRegistry.CAMPFIRE_MEATS));
      registry.removeRecipes(
         recipe -> recipe.getCategory() == VanillaEmiRecipeCategories.CAMPFIRE_COOKING
            && recipe.getOutputs().stream().anyMatch(output -> output.getItemStack().is(TagRegistry.CAMPFIRE_MEATS))
      );
   }

   private static FarmAndCharmEmiRecipe cookingPot(RecipeHolder<CookingPotRecipe> holder, RegistryAccess access) {
      CookingPotRecipe recipe = (CookingPotRecipe)holder.value();
      List<EmiIngredient> inputs = ingredients(recipe.getIngredients());
      EmiStack container = recipe.isContainerRequired() ? EmiStack.of(recipe.getContainerItem()) : EmiStack.EMPTY;
      List<EmiIngredient> all = new ArrayList<>(inputs);
      if (!container.isEmpty()) {
         all.add(container);
      }

      EmiStack output = EmiStack.of(recipe.getResultItem(access));
      return new FarmAndCharmEmiRecipe(COOKING_POT, holder.id(), 124, 60, all, inputs.size(), List.of(output), (self, widgets) -> {
         widgets.addTexture(CookingPotGui.BACKGROUND, 0, 0, 124, 60, 26, 13);
         widgets.addAnimatedTexture(CookingPotGui.BACKGROUND, 69, 1, 18, 30, 178, 15, CookingPotBlockEntity.getMaxCookingTime() * 50, true, false, false);
         potGrid(widgets, inputs);
         if (!container.isEmpty()) {
            widgets.addSlot(container, 68, 41).drawBack(false);
         }

         widgets.addSlot(output, 97, 14).drawBack(false).recipeContext(self);
      });
   }

   private static FarmAndCharmEmiRecipe roaster(RecipeHolder<RoasterRecipe> holder, RegistryAccess access) {
      RoasterRecipe recipe = (RoasterRecipe)holder.value();
      List<EmiIngredient> inputs = ingredients(recipe.getIngredients());
      EmiStack container = EmiStack.of(recipe.getContainer());
      List<EmiIngredient> all = new ArrayList<>(inputs);
      all.add(container);
      EmiStack output = EmiStack.of(recipe.getResultItem(access));
      return new FarmAndCharmEmiRecipe(ROASTER, holder.id(), 124, 60, all, inputs.size(), List.of(output), (self, widgets) -> {
         widgets.addTexture(RoasterGui.BACKGROUND, 0, 0, 124, 60, 26, 13);
         widgets.addAnimatedTexture(RoasterGui.BACKGROUND, 69, 1, 18, 30, 178, 15, RoasterBlockEntity.getMaxRoastingTime() * 50, true, false, false);
         potGrid(widgets, inputs);
         widgets.addSlot(container, 68, 41).drawBack(false);
         widgets.addSlot(output, 97, 14).drawBack(false).recipeContext(self);
      });
   }

   private static void potGrid(WidgetHolder widgets, List<EmiIngredient> inputs) {
      for (int i = 0; i < inputs.size() && i < 6; i++) {
         int x = 30 + i % 3 * 18 - 26 - 1;
         int y = 17 + i / 3 * 18 - 13 - 1;
         widgets.addSlot(inputs.get(i), x, y).drawBack(false);
      }
   }

   private static FarmAndCharmEmiRecipe stove(RecipeHolder<StoveRecipe> holder, RegistryAccess access) {
      StoveRecipe recipe = (StoveRecipe)holder.value();
      List<EmiIngredient> inputs = ingredients(recipe.getIngredients());
      EmiStack output = EmiStack.of(recipe.getResultItem(access));
      return new FarmAndCharmEmiRecipe(STOVE, holder.id(), 124, 60, inputs, List.of(output), (self, widgets) -> {
         widgets.addTexture(StoveGui.BACKGROUND, 0, 0, 124, 60, 26, 13);
         widgets.addAnimatedTexture(StoveGui.BACKGROUND, 67, 19, 18, 25, 178, 20, 12000, true, false, false);
         widgets.addTexture(StoveGui.BACKGROUND, 36, 36, 17, 15, 176, 0);

         for (int i = 0; i < inputs.size() && i < 3; i++) {
            widgets.addSlot(inputs.get(i), 29 + i * 18 - 26 - 1, 4).drawBack(false);
         }

         widgets.addSlot(output, 99, 28).drawBack(false).recipeContext(self);
         float experience = recipe.getExperience();
         if (experience > 0.0F) {
            Component text = Component.translatable("emi.cooking.experience", new Object[]{experience});
            widgets.addText(text, 124 - Minecraft.getInstance().font.width(text), 0, -8355712, false);
         }
      });
   }

   private static FarmAndCharmEmiRecipe craftingBowl(RecipeHolder<CraftingBowlRecipe> holder, RegistryAccess access) {
      CraftingBowlRecipe recipe = (CraftingBowlRecipe)holder.value();
      List<EmiIngredient> inputs = ingredients(recipe.getIngredients());
      EmiStack output = EmiStack.of(recipe.getResultItem(access));
      int[][] positions = new int[][]{{50, 25}, {50, 43}, {32, 25}, {32, 43}};
      return new FarmAndCharmEmiRecipe(DOUGHING, holder.id(), 176, 85, inputs, List.of(output), (self, widgets) -> {
         widgets.addTexture(CRAFTING_BOWL_TEXTURE, 0, 0, 176, 85, 0, 0);

         for (int i = 0; i < inputs.size() && i < positions.length; i++) {
            widgets.addSlot(inputs.get(i), positions[i][0] - 1, positions[i][1] - 1).drawBack(false);
         }

         widgets.addSlot(output, 109, 34).drawBack(false).recipeContext(self);
      });
   }

   private static FarmAndCharmEmiRecipe simple(FarmAndCharmEmiCategory category, ResourceLocation id, List<Ingredient> ingredients, ItemStack result) {
      List<EmiIngredient> inputs = ingredients.isEmpty() ? List.of() : List.of(EmiIngredient.of(ingredients.get(0)));
      EmiStack output = EmiStack.of(result);
      return new FarmAndCharmEmiRecipe(category, id, 150, 50, inputs, List.of(output), (self, widgets) -> {
         widgets.addSlot((EmiIngredient)(inputs.isEmpty() ? EmiStack.EMPTY : inputs.get(0)), 39, 16);
         arrow(widgets, 63, 17);
         widgets.addSlot(output, 93, 16).recipeContext(self);
      });
   }

   private static FarmAndCharmEmiRecipe cuttingBoard(RecipeHolder<CuttingBoardRecipe> holder) {
      CuttingBoardRecipe recipe = (CuttingBoardRecipe)holder.value();
      EmiIngredient input = EmiIngredient.of(recipe.getIngredient());
      List<EmiStack> outputs = new ArrayList<>();
      outputs.add(EmiStack.of(recipe.getResult()));
      recipe.getByproducts().forEach(stack -> outputs.add(EmiStack.of(stack)));
      int width = 54 + outputs.size() * 18;
      int offset = (130 - width) / 2 - 10;
      return new FarmAndCharmEmiRecipe(CUTTING_BOARD, holder.id(), 130, 36, List.of(input), outputs, (self, widgets) -> {
         widgets.addSlot(input, offset + 10, 6);
         arrow(widgets, offset + 34, 7);
         int x = offset + 64;

         for (EmiStack output : outputs) {
            widgets.addSlot(output, x, 6).recipeContext(self);
            x += 18;
         }
      });
   }

   private static FarmAndCharmEmiRecipe assembly(RecipeHolder<CuttingBoardAssemblyRecipe> holder) {
      CuttingBoardAssemblyRecipe recipe = (CuttingBoardAssemblyRecipe)holder.value();
      List<EmiIngredient> inputs = ingredients(recipe.getIngredients());
      EmiStack output = EmiStack.of(recipe.getResult());
      AssemblyLayout layout = RecipeViewerLayout.assembly(inputs.size(), recipe.isOrdered(), 3);
      return new FarmAndCharmEmiRecipe(ASSEMBLY, holder.id(), RecipeViewerLayout.assemblyWidth(3), 54, inputs, List.of(output), (self, widgets) -> {
         for (int i = 0; i < layout.slots().size(); i++) {
            Pos pos = (Pos)layout.slots().get(i);
            widgets.addSlot((EmiIngredient)(i < inputs.size() ? inputs.get(i) : EmiStack.EMPTY), pos.x(), pos.y());
         }

         for (Pos pos : layout.arrows()) {
            arrow(widgets, pos.x(), pos.y());
         }

         widgets.addSlot(output, layout.output().x(), layout.output().y()).recipeContext(self);
      });
   }

   private static FarmAndCharmEmiRecipe stripping(Strippables.Entry entry) {
      EmiStack input = EmiStack.of(entry.log());
      EmiStack output = EmiStack.of(entry.stripped());
      ResourceLocation logId = entry.log().getItem().builtInRegistryHolder().key().location();
      ResourceLocation id = FarmAndCharm.identifier("/log_stripping/" + logId.getNamespace() + "/" + logId.getPath());
      return new FarmAndCharmEmiRecipe(LOG_STRIPPING, id, 130, 36, List.of(input), List.of(output), (self, widgets) -> {
         widgets.addSlot(input, 10, 6);
         arrow(widgets, 34, 7);
         widgets.addSlot(output, 64, 6).recipeContext(self);
      });
   }

   private static void arrow(WidgetHolder widgets, int x, int y) {
      widgets.addDrawable(x, y, 24, 16, (graphics, mouseX, mouseY, delta) -> RecipeViewerLayout.drawRightArrow(graphics, 0, 0));
   }

   private static List<EmiIngredient> ingredients(List<Ingredient> ingredients) {
      return ingredients.stream().<EmiIngredient>map(EmiIngredient::of).toList();
   }
}
