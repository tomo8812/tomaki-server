package org.valkyrienskies.mod.mixin.feature.config_recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.class_1863;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.valkyrienskies.mod.common.config.RecipeOverrides;

@Mixin(class_1863.class)
public class MixinRecipeManager {
   @ModifyVariable(
      method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
      at = @At("HEAD"),
      argsOnly = true,
      require = 1
   )
   private Map<class_2960, JsonElement> vs$applyConfigRecipeOverrides(Map<class_2960, JsonElement> original) {
      try {
         RecipeOverrides.reload();
         Map<String, JsonObject> overrides = RecipeOverrides.getOverrides();
         Set<String> removals = RecipeOverrides.getRemovals();
         if (overrides.isEmpty() && removals.isEmpty()) {
            return original;
         }

         Map<class_2960, JsonElement> patched = new LinkedHashMap<>(original);

         for (String rid : removals) {
            try {
               patched.remove(class_2960.method_60654(rid));
            } catch (Exception var10) {
            }
         }

         int applied = 0;

         for (Entry<String, JsonObject> e : overrides.entrySet()) {
            try {
               patched.put(class_2960.method_60654(e.getKey()), (JsonElement)e.getValue());
               applied++;
            } catch (Exception ex) {
               RecipeOverrides.logError("Config recipe '" + e.getKey() + "' has a bad id", ex);
            }
         }

         RecipeOverrides.logInfo("[vs_eureka recipes] applied " + applied + " override(s), " + removals.size() + " removal(s).");
         return patched;
      } catch (Exception e) {
         RecipeOverrides.logError("Config recipe override pass failed; using built-in recipes", e);
         return original;
      }
   }
}
