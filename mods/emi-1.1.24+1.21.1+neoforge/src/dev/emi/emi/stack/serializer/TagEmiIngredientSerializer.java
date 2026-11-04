package dev.emi.emi.stack.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.TagEmiIngredient;
import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;

public class TagEmiIngredientSerializer implements EmiIngredientSerializer<TagEmiIngredient> {
   static final Pattern STACK_REGEX = Pattern.compile("^#([\\w_\\-.:]+):([\\w_\\-.]+):([\\w_\\-./]+)(\\{.*\\})?$");

   @Override
   public String getType() {
      return "tag";
   }

   @Override
   public EmiIngredient deserialize(JsonElement element) {
      if (GsonHelper.isStringValue(element)) {
         String s = element.getAsString();
         Matcher m = STACK_REGEX.matcher(s);
         if (m.matches()) {
            ResourceLocation registry = EmiPort.id(m.group(1));
            ResourceLocation id = EmiPort.id(m.group(2), m.group(3));
            return EmiIngredient.of(TagKey.create(ResourceKey.createRegistryKey(registry), id), 1L);
         }
      } else if (element.isJsonObject()) {
         JsonObject json = element.getAsJsonObject();
         ResourceLocation registry = EmiPort.id(json.get("registry").getAsString());
         ResourceLocation id = EmiPort.id(json.get("id").getAsString());
         long amount = GsonHelper.getAsLong(json, "amount", 1L);
         float chance = GsonHelper.getAsFloat(json, "chance", 1.0F);
         EmiIngredient stack = EmiIngredient.of(TagKey.create(ResourceKey.createRegistryKey(registry), id), amount);
         if (chance != 1.0F) {
            stack.setChance(chance);
         }

         return stack;
      }

      return EmiStack.EMPTY;
   }

   public JsonElement serialize(TagEmiIngredient stack) {
      if (stack.getAmount() == 1L && stack.getChance() == 1.0F) {
         String type = switch (stack.key.registry().location().toString()) {
            case "minecraft:item" -> "item";
            case "minecraft:fluid" -> "fluid";
            default -> null;
         };
         return new JsonPrimitive("#" + type + ":" + stack.key.location());
      } else {
         JsonObject json = new JsonObject();
         json.addProperty("type", "tag");
         json.addProperty("registry", stack.key.registry().location().toString());
         json.addProperty("id", stack.key.location().toString());
         if (stack.getAmount() != 1L) {
            json.addProperty("amount", stack.getAmount());
         }

         if (stack.getChance() != 1.0F) {
            json.addProperty("chance", stack.getChance());
         }

         return json;
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public JsonElement serialize(EmiIngredient param1) {
      return this.serialize((TagEmiIngredient)var1);
   }
}
