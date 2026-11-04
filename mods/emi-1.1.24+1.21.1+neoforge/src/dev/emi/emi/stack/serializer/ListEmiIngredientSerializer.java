package dev.emi.emi.stack.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.ListEmiIngredient;
import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import dev.emi.emi.registry.EmiIngredientSerializers;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.GsonHelper;

public class ListEmiIngredientSerializer implements EmiIngredientSerializer<ListEmiIngredient> {
   @Override
   public String getType() {
      return "list";
   }

   @Override
   public EmiIngredient deserialize(JsonElement element) {
      JsonArray ingredientsArray;
      long amount;
      float chance;
      if (element.isJsonObject()) {
         JsonObject json = element.getAsJsonObject();
         amount = GsonHelper.getAsLong(json, "amount", 1L);
         chance = GsonHelper.getAsFloat(json, "chance", 1.0F);
         ingredientsArray = GsonHelper.getAsJsonArray(json, "ingredients");
      } else {
         if (!element.isJsonArray()) {
            return EmiStack.EMPTY;
         }

         ingredientsArray = element.getAsJsonArray();
         amount = 1L;
         chance = 1.0F;
      }

      List<EmiIngredient> ingredients = new ArrayList<>();

      for (JsonElement ingredientElement : ingredientsArray) {
         ingredients.add(EmiIngredientSerializers.deserialize(ingredientElement));
      }

      EmiIngredient ingredient = EmiIngredient.of(ingredients, amount);
      if (chance != 1.0F) {
         ingredient.setChance(chance);
      }

      return ingredient;
   }

   public JsonElement serialize(ListEmiIngredient stack) {
      if (stack.getAmount() == 1L && stack.getChance() == 1.0F) {
         JsonArray array = new JsonArray();

         for (EmiIngredient inner : stack.getIngredients()) {
            array.add(EmiIngredientSerializers.serialize(inner));
         }

         return array;
      } else {
         JsonObject json = new JsonObject();
         json.addProperty("type", this.getType());
         if (stack.getAmount() != 1L) {
            json.addProperty("amount", stack.getAmount());
         }

         if (stack.getChance() != 1.0F) {
            json.addProperty("chance", stack.getChance());
         }

         JsonArray ingredients = new JsonArray();

         for (EmiIngredient inner : stack.getIngredients()) {
            ingredients.add(EmiIngredientSerializers.serialize(inner));
         }

         json.add("ingredients", ingredients);
         return json;
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public JsonElement serialize(EmiIngredient param1) {
      return this.serialize((ListEmiIngredient)var1);
   }
}
