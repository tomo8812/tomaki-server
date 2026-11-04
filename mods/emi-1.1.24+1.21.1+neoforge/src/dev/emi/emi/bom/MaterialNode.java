package dev.emi.emi.bom;

import com.google.common.collect.Lists;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class MaterialNode {
   public final EmiIngredient ingredient;
   public final EmiStack remainder;
   @Nullable
   public EmiRecipe recipe;
   @Nullable
   public List<MaterialNode> children;
   public float consumeChance = 1.0F;
   public float produceChance = 1.0F;
   public long amount = 1L;
   public long divisor = 1L;
   public long remainderAmount = 0L;
   public boolean catalyst = false;
   public FoldState state = FoldState.EXPANDED;
   public ProgressState progress = ProgressState.UNSTARTED;
   public long neededBatches = 0L;
   public long totalNeeded = 0L;

   public MaterialNode(EmiIngredient ingredient) {
      this.amount = ingredient.getAmount();
      this.ingredient = ingredient.copy().setAmount(1L).setChance(1.0F);
      if (this.ingredient.getEmiStacks().size() == 1) {
         this.remainder = this.ingredient.getEmiStacks().get(0).getRemainder();
         this.remainderAmount = this.remainder.getAmount();
         this.remainder.setAmount(1L);
      } else {
         this.remainder = EmiStack.EMPTY;
      }

      Iterator var2 = ingredient.getEmiStacks().iterator();

      EmiStack stack;
      do {
         if (!var2.hasNext()) {
            this.catalyst = true;
            break;
         }

         stack = (EmiStack)var2.next();
      } while (stack.getRemainder().equals(stack));
   }

   public MaterialNode(MaterialNode node) {
      this.ingredient = node.ingredient;
      this.remainder = node.remainder;
      this.recipe = node.recipe;
      this.amount = node.amount;
      this.divisor = node.divisor;
      this.remainderAmount = node.remainderAmount;
   }

   public void recalculate(MaterialTree tree) {
      this.recalculate(tree, Lists.newArrayList());
   }

   private void recalculate(MaterialTree tree, List<EmiRecipe> used) {
      EmiRecipe recipe = this.recipe;
      if (!used.isEmpty()) {
         recipe = tree.getRecipe(this.ingredient);
      }

      if (recipe != null) {
         if (used.contains(recipe)) {
            return;
         }

         used.add(recipe);
         this.defineRecipe(recipe);

         for (MaterialNode node : this.children) {
            node.recalculate(tree, used);
         }

         used.remove(used.size() - 1);
      }
   }

   public void defineRecipe(EmiRecipe recipe) {
      this.produceChance = 1.0F;
      if (recipe != null) {
         this.recipe = recipe;
         this.divisor = 0L;

         for (EmiStack stack : recipe.getOutputs()) {
            if (stack.equals(this.ingredient)) {
               if (this.divisor > 0L) {
                  if (this.produceChance != 1.0F || stack.getChance() != 1.0F) {
                     this.produceChance = ((float)stack.getAmount() * stack.getChance() + (float)this.divisor * this.produceChance)
                        / (float)(this.divisor + stack.getAmount());
                  }

                  this.divisor = this.divisor + stack.getAmount();
               } else {
                  this.divisor = stack.getAmount();
                  this.produceChance = stack.getChance();
               }
            }
         }

         if (this.divisor <= 0L) {
            this.divisor = 1L;
         }

         this.children = Lists.newArrayList();

         label50:
         for (EmiIngredient i : recipe.getInputs()) {
            EmiStack remainder = EmiStack.EMPTY;
            if (i.getEmiStacks().size() == 1) {
               remainder = i.getEmiStacks().get(0).getRemainder();
            }

            for (MaterialNode node : this.children) {
               if (EmiIngredient.areEqual(i, node.ingredient) && EmiIngredient.areEqual(remainder, node.remainder)) {
                  node.amount = node.amount + i.getAmount();
                  node.remainderAmount = node.remainderAmount + remainder.getAmount();
                  continue label50;
               }
            }

            if (!i.isEmpty()) {
               MaterialNode node = new MaterialNode(i);
               node.consumeChance = i.getChance();
               this.children.add(node);
            }
         }
      }
   }
}
