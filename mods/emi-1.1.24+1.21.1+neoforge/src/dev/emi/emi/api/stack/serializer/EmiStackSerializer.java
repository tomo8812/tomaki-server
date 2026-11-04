package dev.emi.emi.api.stack.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.runtime.EmiLog;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public interface EmiStackSerializer<T extends EmiStack> extends EmiIngredientSerializer<T> {
   Pattern STACK_REGEX = Pattern.compile("^([\\w_\\-./]+):([\\w_\\-.]+):([\\w_\\-./]+)(\\{.*\\})?$");

   EmiStack create(ResourceLocation var1, DataComponentPatch var2, long var3);

   private static <T> DynamicOps<T> withRegistryAccess(DynamicOps<T> ops) {
      Minecraft instance = Minecraft.getInstance();
      return (DynamicOps<T>)(instance != null && instance.level != null ? instance.level.registryAccess().createSerializationContext(ops) : ops);
   }

   @Override
   default EmiIngredient deserialize(JsonElement element) {
      ResourceLocation id = null;
      String nbt = null;
      JsonObject changesJson = null;
      long amount = 1L;
      float chance = 1.0F;
      EmiStack remainder = EmiStack.EMPTY;
      if (GsonHelper.isStringValue(element)) {
         String s = element.getAsString();
         Matcher m = STACK_REGEX.matcher(s);
         if (m.matches()) {
            id = EmiPort.id(m.group(2), m.group(3));
            nbt = m.group(4);
         }
      } else if (element.isJsonObject()) {
         JsonObject json = element.getAsJsonObject();
         id = EmiPort.id(GsonHelper.getAsString(json, "id"));
         nbt = GsonHelper.getAsString(json, "nbt", null);
         changesJson = GsonHelper.getAsJsonObject(json, "componentChanges", null);
         amount = GsonHelper.getAsLong(json, "amount", 1L);
         chance = GsonHelper.getAsFloat(json, "chance", 1.0F);
         if (GsonHelper.isValidNode(json, "remainder") && EmiIngredientSerializer.getDeserialized(json.get("remainder")) instanceof EmiStack stack) {
            remainder = stack;
         }
      }

      if (id != null) {
         try {
            DataComponentPatch changes = DataComponentPatch.EMPTY;
            if (changesJson != null) {
               changes = (DataComponentPatch)((Pair)DataComponentPatch.CODEC.decode(withRegistryAccess(JsonOps.INSTANCE), changesJson).getOrThrow()).getFirst();
            } else if (nbt != null) {
               changes = (DataComponentPatch)((Pair)DataComponentPatch.CODEC.decode(withRegistryAccess(NbtOps.INSTANCE), TagParser.parseTag(nbt)).getOrThrow())
                  .getFirst();
            }

            EmiStack stack = this.create(id, changes, amount);
            if (chance != 1.0F) {
               stack.setChance(chance);
            }

            if (!remainder.isEmpty()) {
               stack.setRemainder(remainder);
            }

            return stack;
         } catch (Exception e) {
            EmiLog.error("Error parsing NBT in deserialized stack", e);
            return EmiStack.EMPTY;
         }
      } else {
         return EmiStack.EMPTY;
      }
   }

   default JsonElement serialize(T stack) {
      String nbt = null;
      DataComponentPatch componentChanges = stack.getComponentChanges();
      if (componentChanges != DataComponentPatch.EMPTY) {
         nbt = ((Tag)DataComponentPatch.CODEC.encodeStart(withRegistryAccess(NbtOps.INSTANCE), componentChanges).getOrThrow()).getAsString();
      }

      if (stack.getAmount() == 1L && stack.getChance() == 1.0F && stack.getRemainder().isEmpty()) {
         String s = this.getType() + ":" + stack.getId();
         if (nbt != null) {
            s = s + nbt;
         }

         return new JsonPrimitive(s);
      } else {
         JsonObject json = new JsonObject();
         json.addProperty("type", this.getType());
         json.addProperty("id", stack.getId().toString());
         if (nbt != null) {
            json.addProperty("nbt", nbt);
         }

         if (stack.getAmount() != 1L) {
            json.addProperty("amount", stack.getAmount());
         }

         if (stack.getChance() != 1.0F) {
            json.addProperty("chance", stack.getChance());
         }

         if (!stack.getRemainder().isEmpty()) {
            EmiStack remainder = stack.getRemainder();
            if (!remainder.getRemainder().isEmpty()) {
               remainder = remainder.copy().setRemainder(EmiStack.EMPTY);
            }

            if (remainder.getRemainder().isEmpty()) {
               JsonElement remainderElement = EmiIngredientSerializer.getSerialized(remainder);
               if (remainderElement != null) {
                  json.add("remainder", remainderElement);
               }
            }
         }

         return json;
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   default JsonElement serialize(EmiIngredient param1) {
      return this.serialize((T)var1);
   }
}
