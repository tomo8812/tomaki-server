package dev.emi.emi.registry;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import dev.emi.emi.runtime.EmiLog;
import java.util.Map;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

public class EmiIngredientSerializers {
   public static final Map<Class<?>, EmiIngredientSerializer<?>> BY_CLASS = Maps.newHashMap();
   public static final Map<String, EmiIngredientSerializer<?>> BY_TYPE = Maps.newHashMap();

   public static void clear() {
      BY_CLASS.clear();
      BY_TYPE.clear();
   }

   @Nullable
   public static JsonElement serialize(EmiIngredient ingredient) {
      if (ingredient != null && BY_CLASS.containsKey(ingredient.getClass())) {
         try {
            return ((EmiIngredientSerializer<EmiIngredient>)BY_CLASS.get(ingredient.getClass())).serialize(ingredient);
         } catch (Exception e) {
            EmiLog.error("Exception serializing stack " + ingredient, e);
            return null;
         }
      } else {
         return null;
      }
   }

   public static EmiIngredient deserialize(JsonElement element) {
      if (element != null && !element.isJsonNull()) {
         try {
            String type;
            if (element.isJsonObject()) {
               JsonObject json = element.getAsJsonObject();
               type = json.get("type").getAsString();
               if (type.equals("emi:item")) {
                  json.addProperty("type", "item");
                  if (!json.has("id")) {
                     json.addProperty("id", GsonHelper.getAsString(json, "item", ""));
                  }
               } else if (type.equals("emi:fluid")) {
                  json.addProperty("type", "fluid");
                  if (!json.has("id")) {
                     json.addProperty("id", GsonHelper.getAsString(json, "fluid", ""));
                  }
               } else if (type.equals("emi:item_tag")) {
                  json.addProperty("type", "tag");
                  json.addProperty("registry", "minecraft:item");
                  if (!json.has("id")) {
                     json.addProperty("id", GsonHelper.getAsString(json, "tag", ""));
                  }
               }

               type = json.get("type").getAsString();
            } else if (element.isJsonArray()) {
               type = "list";
            } else {
               String[] split = element.getAsString().split(":");
               type = split[0];
               if (!BY_TYPE.containsKey(type) && type.startsWith("#")) {
                  type = "tag";
               }
            }

            return BY_TYPE.get(type).deserialize(element);
         } catch (Exception e) {
            EmiLog.error("Exception deserializing stack " + element, e);
            return EmiStack.EMPTY;
         }
      } else {
         return EmiStack.EMPTY;
      }
   }
}
