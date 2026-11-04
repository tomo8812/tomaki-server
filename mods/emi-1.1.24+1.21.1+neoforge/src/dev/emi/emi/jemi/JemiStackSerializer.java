package dev.emi.emi.jemi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.util.GsonHelper;

public class JemiStackSerializer implements EmiIngredientSerializer<JemiStack> {
   private final IIngredientManager manager;

   public JemiStackSerializer(IIngredientManager manager) {
      this.manager = manager;
   }

   @Override
   public String getType() {
      return "jemi";
   }

   public EmiStack create(String uid, long amount) {
      for (IIngredientType<?> type : this.manager.getRegisteredIngredientTypes()) {
         if (type != VanillaTypes.ITEM_STACK && type != JemiUtil.getFluidType()) {
            Optional<EmiStack> opt = this.manager.getTypedIngredientByUid(type, uid).map(JemiUtil::getStack);
            if (opt.isPresent()) {
               return opt.get().setAmount(amount);
            }
         }
      }

      return EmiStack.EMPTY;
   }

   @Override
   public EmiIngredient deserialize(JsonElement element) {
      JsonObject json = element.getAsJsonObject();
      String uid = GsonHelper.getAsString(json, "uid");
      long amount = GsonHelper.getAsLong(json, "amount", 1L);
      float chance = GsonHelper.getAsFloat(json, "chance", 1.0F);
      EmiStack remainder = EmiStack.EMPTY;
      if (GsonHelper.isValidNode(json, "remainder") && EmiIngredientSerializer.getDeserialized(json.get("remainder")) instanceof EmiStack stack) {
         remainder = stack;
      }

      EmiStack stack = this.create(uid, amount);
      if (chance != 1.0F) {
         stack.setChance(chance);
      }

      if (!remainder.isEmpty()) {
         stack.setRemainder(remainder);
      }

      return stack;
   }

   public JsonElement serialize(JemiStack stack) {
      JsonObject json = new JsonObject();
      json.addProperty("type", this.getType());
      json.addProperty("uid", stack.getJeiUid());
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

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public JsonElement serialize(EmiIngredient param1) {
      return this.serialize((JemiStack)var1);
   }
}
