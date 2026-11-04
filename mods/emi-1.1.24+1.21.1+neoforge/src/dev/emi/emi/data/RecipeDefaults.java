package dev.emi.emi.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeManager;
import dev.emi.emi.api.recipe.EmiResolutionRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class RecipeDefaults {
   public final List<ResourceLocation> added = Lists.newArrayList();
   public final List<RecipeDefaults.Resolution> resolutions = Lists.newArrayList();
   public final List<RecipeDefaults.Tag> tags = Lists.newArrayList();

   public void add(ResourceLocation id) {
      this.added.add(id);
   }

   public void add(ResourceLocation id, JsonArray arr) {
      this.resolutions.add(new RecipeDefaults.Resolution(id, arr.asList()));
   }

   public void addTag(JsonElement tag, JsonElement stack) {
      this.tags.add(new RecipeDefaults.Tag(tag, stack));
   }

   public void remove(ResourceLocation id) {
      this.added.remove(id);
      this.resolutions.removeIf(r -> r.recipe.equals(id));
   }

   public void clear() {
      this.added.clear();
      this.resolutions.clear();
      this.tags.clear();
   }

   public Map<EmiIngredient, EmiRecipe> bake() {
      Map<EmiIngredient, EmiRecipe> map = Maps.newHashMap();
      Minecraft client = Minecraft.getInstance();
      if (client.level == null) {
         return map;
      }

      EmiRecipeManager manager = EmiApi.getRecipeManager();

      for (ResourceLocation id : this.added) {
         EmiRecipe recipe = manager.getRecipe(id);
         if (recipe != null) {
            for (EmiIngredient stack : recipe.getOutputs()) {
               map.put(stack, recipe);
            }
         }
      }

      for (RecipeDefaults.Resolution r : this.resolutions) {
         EmiRecipe recipe = manager.getRecipe(r.recipe);
         if (recipe != null) {
            for (JsonElement el : r.stacks) {
               EmiIngredient stack = EmiIngredientSerializer.getDeserialized(el);
               if (!stack.isEmpty()) {
                  map.put(stack, recipe);
               }
            }
         }
      }

      for (RecipeDefaults.Tag t : this.tags) {
         EmiIngredient tag = EmiIngredientSerializer.getDeserialized(t.tag);
         EmiIngredient stack = EmiIngredientSerializer.getDeserialized(t.stack);
         if (!tag.isEmpty() && !stack.isEmpty() && stack.getEmiStacks().size() == 1 && tag.getEmiStacks().containsAll(stack.getEmiStacks())) {
            map.put(tag, new EmiResolutionRecipe(tag, stack.getEmiStacks().get(0)));
         }
      }

      return map;
   }

   public record Resolution(ResourceLocation recipe, List<JsonElement> stacks) {
   }

   public record Tag(JsonElement tag, JsonElement stack) {
   }
}
