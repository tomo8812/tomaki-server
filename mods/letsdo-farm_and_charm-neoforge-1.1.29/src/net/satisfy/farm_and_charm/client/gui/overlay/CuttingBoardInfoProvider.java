package net.satisfy.farm_and_charm.client.gui.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.farm_and_charm.core.block.entity.CuttingBoardBlockEntity;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardAssemblyRecipe;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardRecipe;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.util.Strippables;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import org.jetbrains.annotations.Nullable;

public class CuttingBoardInfoProvider implements BlockInfoProvider {
   public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
      if (InfoTooltips.isHidden(PlatformHelper.showCuttingBoardInfo())) {
         return List.of();
      } else if (!(level.getBlockEntity(pos) instanceof CuttingBoardBlockEntity board)) {
         return List.of();
      } else {
         ArrayList var17 = new ArrayList();
         if (board.hasKnife()) {
            boolean axe = Strippables.isAxe(board.getKnife());
            String prefix = axe ? "hud.farm_and_charm.strip" : "hud.farm_and_charm.chop";
            var17.add(
               InfoSection.icons(
                  Component.translatable(board.isCutting() ? prefix + "ping" : "hud.farm_and_charm.ready_to_" + (axe ? "strip" : "chop")),
                  board.isCutting() ? List.of(board.getPending()) : List.of(board.getKnife()),
                  6
               )
            );
            if (board.isCutting()) {
               if (axe) {
                  var17.add(
                     InfoSection.title(
                        Component.translatable("hud.farm_and_charm.stripped", new Object[]{board.getPendingChops(), 2}).withStyle(ChatFormatting.GRAY)
                     )
                  );
               } else {
                  level.getRecipeManager()
                     .getRecipeFor((RecipeType)RecipeTypeRegistry.CUTTING_BOARD_RECIPE_TYPE.get(), new SingleRecipeInput(board.getPending()), level)
                     .ifPresent(
                        recipex -> var17.add(
                           InfoSection.title(
                              Component.translatable(
                                    "hud.farm_and_charm.chop",
                                    new Object[]{
                                       board.getPendingChops(), CuttingBoardBlockEntity.requiredChops(board.getKnife(), (CuttingBoardRecipe)recipex.value())
                                    }
                                 )
                                 .withStyle(ChatFormatting.GRAY)
                           )
                        )
                     );
               }
            }

            return var17;
         } else {
            List<ItemStack> items = board.getItems();
            if (items.isEmpty()) {
               return var17;
            }

            Optional<RecipeHolder<CuttingBoardAssemblyRecipe>> assembled = level.getRecipeManager()
               .getRecipeFor(
                  (RecipeType)RecipeTypeRegistry.CUTTING_BOARD_ASSEMBLY_RECIPE_TYPE.get(), new CuttingBoardAssemblyRecipe.Input(List.copyOf(items)), level
               );
            if (assembled.isPresent()) {
               var17.add(
                  InfoSection.icons(
                     Component.translatable("hud.farm_and_charm.assembly_ready").withStyle(ChatFormatting.GREEN),
                     List.of(((CuttingBoardAssemblyRecipe)assembled.get().value()).getResult()),
                     6
                  )
               );
               var17.add(InfoSection.title(Component.translatable("hud.farm_and_charm.assemble_hint").withStyle(ChatFormatting.GRAY)));
            }

            var17.add(InfoSection.icons(Component.translatable("hud.farm_and_charm.on_board"), items, 6).withDecorations());
            if (items.size() == 1) {
               Optional<RecipeHolder<CuttingBoardRecipe>> chop = level.getRecipeManager()
                  .getRecipeFor((RecipeType)RecipeTypeRegistry.CUTTING_BOARD_RECIPE_TYPE.get(), new SingleRecipeInput(items.getFirst()), level);
               chop.ifPresent(
                  recipex -> var17.add(
                     InfoSection.title(
                        Component.translatable(
                              "hud.farm_and_charm.chop",
                              new Object[]{
                                 board.getChops(),
                                 CuttingBoardBlockEntity.requiredChops(Minecraft.getInstance().player.getMainHandItem(), (CuttingBoardRecipe)recipex.value())
                              }
                           )
                           .withStyle(ChatFormatting.GRAY)
                     )
                  )
               );
            }

            List<ItemStack> additions = new ArrayList<>();

            for (RecipeHolder<CuttingBoardAssemblyRecipe> holder : level.getRecipeManager()
               .getAllRecipesFor((RecipeType)RecipeTypeRegistry.CUTTING_BOARD_ASSEMBLY_RECIPE_TYPE.get())) {
               CuttingBoardAssemblyRecipe recipe = (CuttingBoardAssemblyRecipe)holder.value();
               if (recipe.accepts(items)) {
                  for (Ingredient ingredient : recipe.getIngredients()) {
                     ItemStack[] choices = ingredient.getItems();
                     if (choices.length != 0) {
                        List<ItemStack> candidate = new ArrayList<>(items);
                        candidate.add(choices[0]);
                        if (recipe.accepts(candidate) && additions.stream().noneMatch(stack -> ItemStack.isSameItem(stack, choices[0]))) {
                           additions.add(choices[0]);
                        }
                     }
                  }
               }
            }

            if (!additions.isEmpty()) {
               var17.add(InfoSection.icons(Component.translatable("hud.farm_and_charm.add"), additions, 3));
            }

            return var17;
         }
      }
   }
}
