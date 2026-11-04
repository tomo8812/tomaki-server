package net.satisfy.bakery.core.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.satisfy.bakery.Bakery;
import net.satisfy.bakery.core.registry.ObjectRegistry;
import net.satisfy.bakery.core.registry.RecipeTypeRegistry;

@EmiEntrypoint
public class BakeryEMIPlugin implements EmiPlugin {
   public static final EmiStack BAKER_STATION = EmiStack.of((ItemLike)ObjectRegistry.BAKER_STATION.get());
   public static final EmiStack COOKING_POT = EmiStack.of((ItemLike)ObjectRegistry.SMALL_COOKING_POT_ITEM.get());
   public static final EmiRecipeCategory BAKING = new EmiRecipeCategory(Bakery.identifier("baking_station"), BAKER_STATION) {
      public Component getName() {
         return ((Block)ObjectRegistry.BAKER_STATION.get()).getName();
      }
   };
   public static final EmiRecipeCategory JAM_COOKING = new EmiRecipeCategory(Bakery.identifier("jam_cooking"), COOKING_POT) {
      public Component getName() {
         return ((Item)ObjectRegistry.SMALL_COOKING_POT_ITEM.get()).getDescription();
      }
   };

   public void register(EmiRegistry registry) {
      registry.addCategory(BAKING);
      registry.addCategory(JAM_COOKING);
      registry.addWorkstation(BAKING, BAKER_STATION);
      registry.addWorkstation(JAM_COOKING, COOKING_POT);
      RecipeManager rm = registry.getRecipeManager();
      rm.getAllRecipesFor((RecipeType)RecipeTypeRegistry.BAKING_STATION_RECIPE_TYPE.get())
         .forEach(holder -> registry.addRecipe(new BakingStationEmiRecipe(holder)));
      rm.getAllRecipesFor((RecipeType)RecipeTypeRegistry.JAM_COOKING_TYPE.get()).forEach(holder -> registry.addRecipe(new JamPotEmiRecipe(holder)));
      EmiStack hidden = EmiStack.of((ItemLike)ObjectRegistry.BAKED_SWEET_DOUGH.get());
      registry.removeEmiStacks(hidden);
      registry.removeRecipes(recipe -> recipe.getOutputs().stream().anyMatch(stack -> stack.isEqual(hidden)));
   }
}
