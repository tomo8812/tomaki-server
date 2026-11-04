package dev.emi.emi.config;

import dev.emi.emi.EmiPort;
import net.minecraft.network.chat.Component;

public enum RecipeBookAction implements ConfigEnum {
   DEFAULT("default"),
   TOGGLE_CRAFTABLES("toggle-craftables"),
   TOGGLE_VISIBILITY("toggle-visibility");

   public final String name;

   RecipeBookAction(String name) {
      this.name = name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public Component getText() {
      return EmiPort.translatable("emi.recipe_book_action." + this.name.replace("-", "_"));
   }

   // $VF: synthetic method
   private static RecipeBookAction[] $values() {
      return new RecipeBookAction[]{DEFAULT, TOGGLE_CRAFTABLES, TOGGLE_VISIBILITY};
   }
}
