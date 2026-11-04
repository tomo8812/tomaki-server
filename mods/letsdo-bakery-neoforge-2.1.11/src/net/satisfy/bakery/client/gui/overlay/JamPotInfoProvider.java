package net.satisfy.bakery.client.gui.overlay;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.bakery.Bakery;
import net.satisfy.bakery.core.block.SmallCookingPotBlock;
import net.satisfy.bakery.core.block.entity.SmallCookingPotBlockEntity;
import net.satisfy.bakery.core.recipe.JamConsistency;
import net.satisfy.bakery.core.recipe.JamRecipe;
import net.satisfy.bakery.core.registry.RecipeTypeRegistry;
import net.satisfy.bakery.platform.PlatformHelper;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import net.satisfy.foundation.tooltip.TooltipBorder;
import org.jetbrains.annotations.Nullable;

public class JamPotInfoProvider implements BlockInfoProvider {
   private static final ResourceLocation STIR_ICON = Bakery.identifier("textures/gui/stir.png");
   private static final int STIR_ICON_SIZE = 20;
   private static final int STIR_START_COLOR = -10496;
   private static final int STIR_END_COLOR = -2084834;

   public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
      if (hit != null && PlatformHelper.showJamPotInfo() && BakerStationInfoProvider.wearsDungarees()) {
         if (level.getBlockEntity(pos) instanceof SmallCookingPotBlockEntity pot) {
            ArrayList var11 = new ArrayList();
            if (!(Boolean)state.getValue(SmallCookingPotBlock.LIT)) {
               var11.add(InfoSection.title(Component.translatable("hud.bakery.jam_pot.needs_heat").withStyle(ChatFormatting.GOLD)));
               return var11;
            }

            if (pot.isBurnt()) {
               var11.add(InfoSection.title(Component.translatable("hud.bakery.jam_pot.burnt").withStyle(ChatFormatting.RED)));
               return var11;
            }

            if (pot.isCooking()) {
               if (pot.isStirDue()) {
                  int color = ARGB32.lerp(pot.getStirUrgency(), -10496, -2084834) & 16777215;
                  var11.add(
                     InfoSection.image(
                        Component.translatable("hud.bakery.jam_pot.stir").withStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(color))),
                        STIR_ICON,
                        20
                     )
                  );
               } else {
                  var11.add(InfoSection.icons(cookingTitle(pot), List.of(pot.getResult().copyWithCount(1)), 6));
               }

               return var11;
            } else {
               if (pot.isBottling()) {
                  var11.add(
                     InfoSection.icons(
                        Component.translatable("hud.bakery.jam_pot.take").withStyle(ChatFormatting.GREEN),
                        List.of(pot.getResult().copyWithCount(pot.getJarsLeft())),
                        6
                     )
                  );
                  return var11;
               }

               if (!pot.getIngredients().isEmpty()) {
                  List<ItemStack> next = pot.getNextIngredients();
                  if (!next.isEmpty()) {
                     var11.add(InfoSection.icons(Component.translatable("hud.bakery.jam_pot.next"), next, 3));
                  }

                  return var11;
               } else {
                  List<ItemStack> start = new ArrayList<>();

                  for (RecipeHolder<JamRecipe> holder : level.getRecipeManager().getAllRecipesFor((RecipeType)RecipeTypeRegistry.JAM_COOKING_TYPE.get())) {
                     ItemStack[] choices = ((Ingredient)((JamRecipe)holder.value()).getIngredients().getFirst()).getItems();
                     if (choices.length > 0 && start.stream().noneMatch(known -> ItemStack.isSameItem(known, choices[0]))) {
                        start.add(choices[0]);
                     }
                  }

                  if (!start.isEmpty()) {
                     var11.add(InfoSection.icons(Component.translatable("hud.bakery.jam_pot.start"), start, 3));
                  }

                  return var11;
               }
            }
         } else {
            return List.of();
         }
      } else {
         return List.of();
      }
   }

   public void beforeBackground(Level level, BlockPos pos, BlockState state) {
      if (level.getBlockEntity(pos) instanceof SmallCookingPotBlockEntity pot && ((Boolean)state.getValue(SmallCookingPotBlock.LIT) || pot.isBottling())) {
         if (pot.isBurnt()) {
            TooltipBorder.mark(-259251180, -264237048);
         } else if (pot.isCooking() && pot.isStirDue()) {
            TooltipBorder.mark(-251668736, -256342517);
         } else if (pot.isBottling() && pot.getConsistency() != null) {
            markConsistency(pot.getConsistency());
         } else if (pot.isCooking() && pot.canBottle()) {
            markConsistency(pot.currentConsistency());
         }
      }
   }

   private static void markConsistency(JamConsistency consistency) {
      switch (consistency) {
         case RUNNY:
            TooltipBorder.mark(-260057872, -265388120);
            break;
         case PERFECT:
            TooltipBorder.mark(-261105558, -265385174);
            break;
         case CARAMELIZED:
            TooltipBorder.mark(-252665808, -257927142);
      }
   }

   private static Component cookingTitle(SmallCookingPotBlockEntity pot) {
      if (!pot.canBottle()) {
         return Component.translatable("hud.bakery.jam_pot.cooking");
      }

      return switch (pot.currentConsistency()) {
         case RUNNY -> Component.translatable("hud.bakery.jam_pot.runny").withStyle(ChatFormatting.YELLOW);
         case PERFECT -> Component.translatable("hud.bakery.jam_pot.ready").withStyle(ChatFormatting.GREEN);
         case CARAMELIZED -> Component.translatable("hud.bakery.jam_pot.caramelized").withStyle(ChatFormatting.GOLD);
      };
   }
}
