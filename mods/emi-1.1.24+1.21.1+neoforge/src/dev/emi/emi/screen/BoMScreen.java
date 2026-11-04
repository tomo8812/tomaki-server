package dev.emi.emi.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiResolutionRecipe;
import dev.emi.emi.api.render.EmiTooltipComponents;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.bom.ChanceMaterialCost;
import dev.emi.emi.bom.ChanceState;
import dev.emi.emi.bom.FlatMaterialCost;
import dev.emi.emi.bom.FoldState;
import dev.emi.emi.bom.MaterialNode;
import dev.emi.emi.bom.ProgressState;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.data.EmiRecipeCategoryProperties;
import dev.emi.emi.input.EmiBind;
import dev.emi.emi.input.EmiInput;
import dev.emi.emi.registry.EmiStackList;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiFavorites;
import dev.emi.emi.runtime.EmiHistory;
import dev.emi.emi.screen.tooltip.EmiTooltip;
import dev.emi.emi.screen.tooltip.RecipeTooltipComponent;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.joml.Matrix4fStack;

public class BoMScreen extends Screen {
   private static final int NODE_WIDTH = 30;
   private static final int NODE_HORIZONTAL_SPACING = 8;
   private static final int NODE_VERTICAL_SPACING = 20;
   private static final int COST_HORIZONTAL_SPACING = 8;
   private static StackBatcher batcher = new StackBatcher();
   private static int zoom = 0;
   private Bounds batches = new Bounds(-24, -50, 48, 26);
   private Bounds mode = new Bounds(-24, -50, 16, 16);
   private Bounds help = new Bounds(0, 0, 16, 16);
   private double offX;
   private double offY;
   private List<BoMScreen.Node> nodes = Lists.newArrayList();
   private List<BoMScreen.Cost> costs = Lists.newArrayList();
   private EmiPlayerInventory playerInv;
   private boolean hasRemainders = false;
   public AbstractContainerScreen<?> old;
   private int nodeWidth = 0;
   private int nodeHeight = 0;
   private int lastMouseX;
   private int lastMouseY;
   private double scrollAcc = 0.0;

   public BoMScreen(AbstractContainerScreen<?> old) {
      super(EmiPort.translatable("screen.emi.recipe_tree"));
      this.old = old;
   }

   public void init() {
      if (BoM.tree != null) {
         this.offY = this.height / -3;
      } else {
         this.offY = 0.0;
      }

      this.recalculateTree();
   }

   public void recalculateTree() {
      this.help = new Bounds(this.width - 18, this.height - 18, 16, 16);
      if (BoM.tree != null) {
         BoMScreen.TreeVolume volume = this.addNewNodes(BoM.tree.goal, BoM.tree.batches, 1L, 0, ChanceState.DEFAULT);
         this.nodes = volume.nodes;
         int horizontalOffset = (volume.getMaxRight() + volume.getMinLeft()) / 2;

         for (BoMScreen.Node node : volume.nodes) {
            node.x -= horizontalOffset;
         }

         if (!volume.nodes.isEmpty()) {
            BoMScreen.Node node = volume.nodes.get(0);
            int width = this.font.width("x" + BoM.tree.batches);
            this.batches = new Bounds(node.x + node.width / 2 + 6, node.y - 10, width + 12, 22);
         }

         this.nodeWidth = volume.getMaxRight() - volume.getMinLeft();
         this.nodeHeight = this.getNodeHeight(BoM.tree.goal);
         this.playerInv = EmiPlayerInventory.of(this.minecraft.player);
         BoM.tree.calculateProgress(this.playerInv);
         Map<EmiIngredient, FlatMaterialCost> progressCosts = BoM.tree
            .cost
            .costs
            .values()
            .stream()
            .collect(Collectors.toMap(c -> c.ingredient, c -> (FlatMaterialCost)c));
         Map<EmiIngredient, ChanceMaterialCost> chanceProgressCosts = BoM.tree
            .cost
            .chanceCosts
            .values()
            .stream()
            .collect(Collectors.toMap(c -> c.ingredient, c -> (ChanceMaterialCost)c));
         this.costs.clear();
         BoM.tree.calculateCost();
         List<FlatMaterialCost> treeCosts = Stream.concat(BoM.tree.cost.costs.values().stream(), BoM.tree.cost.chanceCosts.values().stream())
            .sorted(
               (a, b) -> Integer.compare(EmiStackList.getIndex(a.ingredient.getEmiStacks().get(0)), EmiStackList.getIndex(b.ingredient.getEmiStacks().get(0)))
            )
            .toList();
         int cy = this.nodeHeight * 20 * 2;
         int costX = 0;

         for (FlatMaterialCost node : treeCosts) {
            BoMScreen.Cost cost = new BoMScreen.Cost(node, costX, cy, false);
            if (BoM.craftingMode) {
               if (node instanceof ChanceMaterialCost cmc) {
                  if (!chanceProgressCosts.containsKey(node.ingredient)) {
                     cost.alreadyDone = node.getEffectiveAmount();
                  } else {
                     ChanceMaterialCost progress = chanceProgressCosts.get(node.ingredient);
                     cost.alreadyDone = (long)Math.ceil((float)cmc.amount * cmc.chance - (float)progress.amount * progress.chance);
                  }
               } else if (!progressCosts.containsKey(node.ingredient)) {
                  cost.alreadyDone = node.amount;
               } else {
                  FlatMaterialCost progress = progressCosts.get(node.ingredient);
                  cost.alreadyDone = node.amount - progress.amount;
               }
            }

            this.costs.add(cost);
            costX += 24 + EmiRenderHelper.getAmountOverflow(cost.getAmountText());
         }

         int costOffset = (costX - 8) / 2;

         for (BoMScreen.Cost cost : this.costs) {
            cost.x -= costOffset;
         }

         int totalCostWidth = this.font.width(EmiPort.translatable("emi.total_cost"));
         this.mode = new Bounds(totalCostWidth / 2 + 4, cy - 20, 16, 16);
         List<BoMScreen.Cost> remainders = Lists.newArrayList();
         List<FlatMaterialCost> remainderCosts = Stream.concat(BoM.tree.cost.remainders.values().stream(), BoM.tree.cost.chanceRemainders.values().stream())
            .sorted(
               (a, b) -> Integer.compare(EmiStackList.getIndex(a.ingredient.getEmiStacks().get(0)), EmiStackList.getIndex(b.ingredient.getEmiStacks().get(0)))
            )
            .toList();
         cy += 40;
         int remainderX = 0;

         for (FlatMaterialCost node : remainderCosts) {
            if (node.getEffectiveAmount() > 0L) {
               BoMScreen.Cost cost = new BoMScreen.Cost(node, remainderX, cy, true);
               remainders.add(cost);
               remainderX += 24 + EmiRenderHelper.getAmountOverflow(cost.getAmountText());
            }
         }

         costOffset = (remainderX - 8) / 2;

         for (BoMScreen.Cost cost : remainders) {
            cost.x -= costOffset;
         }

         this.costs.addAll(remainders);
         this.hasRemainders = !remainders.isEmpty();
      } else {
         this.nodes = Lists.newArrayList();
      }

      batcher.repopulate();
   }

   public void render(GuiGraphics raw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      context.fill(0, 0, this.width, this.height, -587202560);
      this.renderMenuBackground(context.raw());
      this.lastMouseX = mouseX;
      this.lastMouseY = mouseY;
      float scale = this.getScale();
      int scaledWidth = (int)(this.width / scale);
      int scaledHeight = (int)(this.height / scale);
      int contentWidth = this.nodeWidth * 30;
      int contentHeight = this.nodeHeight * 20 + 80;
      int xBound = scaledWidth / 2 + contentWidth - 100;
      int topBound = scaledHeight * 1 / -2 + 20;
      int bottomBound = contentHeight + scaledHeight / 2 - 20;
      this.offX = Mth.clamp(this.offX, -xBound, xBound);
      this.offY = Mth.clamp(this.offY, -bottomBound, -topBound);
      int mx = (int)((mouseX - this.width / 2) / scale - this.offX);
      int my = (int)((mouseY - this.height / 2) / scale - this.offY);
      Matrix4fStack view = RenderSystem.getModelViewStack();
      view.pushMatrix();
      view.translate(this.width / 2, this.height / 2, 0.0F);
      view.scale(scale, scale, 1.0F);
      view.translate((float)this.offX, (float)this.offY, 0.0F);
      EmiPort.applyModelViewMatrix();
      if (BoM.tree != null) {
         batcher.begin(0, 0, 0);
         int cy = this.nodeHeight * 20 * 2;
         context.drawCenteredText(EmiPort.translatable("emi.total_cost"), 0, cy - 16);
         if (this.hasRemainders) {
            context.drawCenteredText(EmiPort.translatable("emi.leftovers"), 0, cy - 16 + 40);
         }

         for (BoMScreen.Cost cost : this.costs) {
            cost.render(context);
         }

         for (BoMScreen.Node node : this.nodes) {
            node.render(context, mx, my, delta);
         }

         int color = -1;
         if (this.batches.contains(mx, my)) {
            color = -8349185;
         }

         context.drawTextWithShadow(EmiPort.literal("x" + BoM.tree.batches), this.batches.x() + 6, this.batches.y() + this.batches.height() / 2 - 4, color);
         if (this.mode.contains(mx, my)) {
            context.setColor(0.5F, 0.6F, 1.0F, 1.0F);
         }

         context.drawTexture(EmiRenderHelper.WIDGETS, this.mode.x(), this.mode.y(), BoM.craftingMode ? 16 : 0, 146, this.mode.width(), this.mode.height());
         context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         batcher.draw();
      } else {
         context.drawCenteredText(EmiPort.translatable("emi.tree_welcome", EmiRenderHelper.getEmiText()), 0, -72);
         context.drawCenteredText(EmiPort.translatable("emi.no_tree"), 0, -48);
         context.drawCenteredText(EmiPort.translatable("emi.random_tree"), 0, -24);
         context.drawCenteredText(EmiPort.translatable("emi.random_tree_input"), 0, 0);
      }

      view.popMatrix();
      EmiPort.applyModelViewMatrix();
      if (this.help.contains(mouseX, mouseY)) {
         context.setColor(0.5F, 0.6F, 1.0F, 1.0F);
      }

      context.drawTexture(EmiRenderHelper.WIDGETS, this.help.x(), this.help.y(), 0, 200, this.help.width(), this.help.height());
      context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      BoMScreen.Hover hover = this.getHoveredStack(mouseX, mouseY);
      if (hover != null) {
         hover.drawTooltip(this, context, mouseX, mouseY);
      } else if (BoM.tree != null && this.batches.contains(mx, my)) {
         List<ClientTooltipComponent> list = Lists.newArrayList();
         list.addAll(EmiTooltip.splitTranslate("tooltip.emi.bom.batch_size", BoM.tree.batches));
         list.add(EmiTooltipComponents.of(EmiPort.translatable("tooltip.emi.bom.batch_size.ideal", EmiBind.LEFT_CLICK.getBindText())));
         EmiRenderHelper.drawTooltip(this, context, list, mouseX, mouseY);
      } else if (BoM.tree != null && this.mode.contains(mx, my)) {
         String key = BoM.craftingMode ? "tooltip.emi.bom.mode.craft" : "tooltip.emi.bom.mode.view";
         List<ClientTooltipComponent> list = EmiTooltip.splitTranslate(key, BoM.tree.batches);
         EmiRenderHelper.drawTooltip(this, context, list, mouseX, mouseY);
      } else if (this.help.contains(mouseX, mouseY)) {
         List<ClientTooltipComponent> list = EmiTooltip.splitTranslate("tooltip.emi.bom.help");
         EmiRenderHelper.drawTooltip(this, context, list, this.width - 18, this.height - 18, this.width);
      }
   }

   public BoMScreen.Hover getHoveredStack(int mx, int my) {
      float scale = this.getScale();
      mx = (int)((mx - this.width / 2) / scale - this.offX);
      my = (int)((my - this.height / 2) / scale - this.offY);

      for (BoMScreen.Cost cost : this.costs) {
         if (mx >= cost.x && mx < cost.x + 16 && my >= cost.y && my < cost.y + 16) {
            return new BoMScreen.Hover(cost.cost.ingredient);
         }
      }

      for (BoMScreen.Node node : this.nodes) {
         BoMScreen.Hover hover = node.getHover(mx, my);
         if (hover != null) {
            return hover;
         }
      }

      return null;
   }

   public int getNodeHeight(MaterialNode node) {
      if (node.recipe != null && node.state == FoldState.EXPANDED) {
         int i = 1;

         for (MaterialNode n : node.children) {
            i = Math.max(i, this.getNodeHeight(n));
         }

         return node.recipe instanceof EmiResolutionRecipe ? i : i + 1;
      } else {
         return 1;
      }
   }

   public BoMScreen.TreeVolume addNewNodes(MaterialNode node, long multiplier, long divisor, int depth, ChanceState chance) {
      if (node.catalyst) {
         multiplier = node.amount;
      } else {
         multiplier = node.amount * (int)Math.ceil((float)multiplier / (float)divisor);
      }

      if (node.recipe != null && node.children.size() > 0 && node.state == FoldState.EXPANDED) {
         ChanceState produced = chance.produce(node.produceChance);
         if (node.recipe instanceof EmiResolutionRecipe) {
            BoMScreen.TreeVolume volume = this.addNewNodes(node.children.get(0), multiplier, node.divisor, depth, produced);
            volume.nodes.get(0).resolution = node;
            return volume;
         }

         BoMScreen.TreeVolume left = null;

         for (int i = 0; i < node.children.size(); i++) {
            ChanceState consumed = produced.consume(node.children.get(i).consumeChance);
            BoMScreen.TreeVolume volume = this.addNewNodes(node.children.get(i), multiplier, node.divisor, depth + 1, consumed);
            if (left == null) {
               left = volume;
            } else {
               left.addToRight(volume);
            }
         }

         left.addHead(node, multiplier, depth * 20, chance);
         return left;
      } else {
         return new BoMScreen.TreeVolume(node, multiplier, depth * 20, chance);
      }
   }

   private static void drawLine(EmiDrawContext context, int x1, int y1, int x2, int y2) {
      if (x2 < x1) {
         drawLine(context, x2, y1, x1, y2);
      } else if (y2 < y1) {
         drawLine(context, x1, y2, x2, y1);
      } else {
         context.fill(x1, y1, x2 - x1 + 1, y2 - y1 + 1, -1);
      }
   }

   public float getScale() {
      zoom = Mth.clamp(zoom, -6, 4);
      int scale = (int)this.minecraft.getWindow().getGuiScale();
      int desired = scale + zoom;
      if (desired < 1) {
         zoom -= desired - 1;
         desired = 1;
      }

      return (float)desired / scale;
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         this.onClose();
         return true;
      }

      if (this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
         this.onClose();
         return true;
      }

      Function<EmiBind, Boolean> function = bind -> bind.matchesKey(keyCode, scanCode);
      if (function.apply(EmiConfig.back)) {
         EmiHistory.pop();
         return true;
      }

      BoMScreen.Hover hover = this.getHoveredStack(this.lastMouseX, this.lastMouseY);
      if (hover != null && hover.stack != null && !hover.stack.isEmpty() && function.apply(EmiConfig.favorite)) {
         EmiFavorites.addFavorite(hover.stack, hover.node == null ? null : hover.node.recipe);
      }

      if (EmiInput.isControlDown() && keyCode == 82) {
         List<EmiRecipe> recipes = EmiApi.getRecipeManager().getRecipes();
         if (recipes.size() > 0) {
            for (int i = 0; i < 100000; i++) {
               EmiRecipe recipe = recipes.get(EmiUtil.RANDOM.nextInt(recipes.size()));
               if (recipe.supportsRecipeTree()) {
                  BoM.setGoal(recipe);
                  this.init();
                  return true;
               }
            }
         }
      } else if (EmiInput.isControlDown() && keyCode == 67) {
         BoM.tree = null;
         this.init();
      }

      return super.keyPressed(keyCode, scanCode, modifiers);
   }

   private boolean getAutoResolutions(BoMScreen.Hover hover, BiConsumer<EmiIngredient, EmiRecipe> consumer) {
      EmiPlayerInventory inv = this.playerInv;
      if (inv != null) {
         List<EmiStack> stacks = hover.stack.getEmiStacks();
         if (stacks.size() > 1) {
            for (EmiStack stack : stacks) {
               if (inv.inventory.containsKey(stack)) {
                  consumer.accept(hover.stack, new EmiResolutionRecipe(hover.stack, stack));
                  return true;
               }
            }

            for (EmiStack stack : stacks) {
               for (BoMScreen.Cost cost : this.costs) {
                  if (cost.cost.ingredient.equals(stack)) {
                     consumer.accept(hover.stack, new EmiResolutionRecipe(hover.stack, stack));
                     return true;
                  }
               }
            }

            consumer.accept(hover.stack, new EmiResolutionRecipe(hover.stack, stacks.get(0)));
            return true;
         }

         EmiRecipe recipe = EmiUtil.getRecipeResolution(hover.stack, inv);
         if (recipe != null) {
            consumer.accept(hover.stack, recipe);
            return true;
         }
      }

      return false;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      BoMScreen.Hover hover = this.getHoveredStack((int)mouseX, (int)mouseY);
      float scale = this.getScale();
      int mx = (int)((mouseX - this.width / 2) / scale - this.offX);
      int my = (int)((mouseY - this.height / 2) / scale - this.offY);
      if (hover != null) {
         if (button == 1 && hover.node != null && hover.node.recipe != null) {
            if (EmiInput.isShiftDown()) {
               BoM.tree.addResolution(hover.node.ingredient, null);
            } else if (!(hover.node.recipe instanceof EmiResolutionRecipe)) {
               if (hover.node.state == FoldState.EXPANDED) {
                  hover.node.state = FoldState.COLLAPSED;
               } else {
                  hover.node.state = FoldState.EXPANDED;
               }
            }

            this.recalculateTree();
            return true;
         }

         if (hover.stack != null) {
            if (EmiInput.isShiftDown() && button == 0) {
               if (this.getAutoResolutions(hover, BoM.tree::addResolution)) {
                  this.recalculateTree();
               }

               return true;
            }

            if (button == 0) {
               EmiApi.displayRecipes(hover.stack);
               RecipeScreen.resolve = hover.stack;
               Minecraft client = Minecraft.getInstance();
               client.screen.init(client, client.screen.width, client.screen.height);
               if (hover.node != null && hover.node.recipe != null) {
                  EmiApi.focusRecipe(hover.node.recipe);
               }

               return true;
            }
         }
      } else if (this.mode.contains(mx, my)) {
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         BoM.craftingMode = !BoM.craftingMode;
         this.recalculateTree();
      } else if (this.batches.contains(mx, my) && BoM.tree != null) {
         long ideal = BoM.tree.cost.getIdealBatch(BoM.tree.goal, 1L, 1L);
         if (ideal != BoM.tree.batches) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            BoM.tree.batches = ideal;
            this.recalculateTree();
         }
      }

      Function<EmiBind, Boolean> function = bind -> bind.matchesMouse(button);
      if (function.apply(EmiConfig.back)) {
         EmiHistory.pop();
         return true;
      } else {
         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double amount) {
      this.scrollAcc += amount;
      amount = (int)this.scrollAcc;
      this.scrollAcc %= 1.0;
      float scale = this.getScale();
      int mx = (int)((mouseX - this.width / 2) / scale - this.offX);
      int my = (int)((mouseY - this.height / 2) / scale - this.offY);
      if (BoM.tree != null && this.batches.contains(mx, my)) {
         long adjustment = (long)amount;
         if (EmiInput.isShiftDown()) {
            adjustment *= 16L;
         } else if (EmiInput.isControlDown()) {
            if (amount > 0.0) {
               adjustment = BoM.tree.batches;
            } else {
               adjustment = -BoM.tree.batches / 2L;
            }
         }

         if (BoM.tree.batches == 1L && adjustment > 1L) {
            BoM.tree.batches = adjustment;
         } else {
            BoM.tree.batches += adjustment;
         }

         BoM.tree.batches = Math.max(1L, BoM.tree.batches);
         this.recalculateTree();
         return true;
      } else {
         zoom += (int)amount;
         return true;
      }
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (button != 0 && button != 2) {
         return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
      }

      float scale = this.getScale();
      this.offX += deltaX / scale;
      this.offY += deltaY / scale;
      return true;
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void onClose() {
      Minecraft.getInstance().setScreen(this.old);
   }

   private class Cost {
      public FlatMaterialCost cost;
      public int x;
      public int y;
      public long alreadyDone = 0L;
      public boolean remainder;

      public Cost(FlatMaterialCost cost, int x, int y, boolean remainder) {
         this.cost = cost;
         this.x = x;
         this.y = y;
         this.remainder = remainder;
      }

      public void render(EmiDrawContext context) {
         BoMScreen.batcher.render(this.cost.ingredient, context.raw(), this.x, this.y, 0.0F, -11);
         EmiRenderHelper.renderAmount(context, this.x, this.y, this.getAmountText());
      }

      public Component getAmountText() {
         long adjusted = this.cost.getEffectiveAmount();
         Component totalText;
         if (this.cost instanceof ChanceMaterialCost cmc) {
            totalText = EmiPort.append(EmiPort.literal("≈"), EmiRenderHelper.getAmountText(this.cost.ingredient, adjusted)).withStyle(ChatFormatting.GOLD);
         } else {
            totalText = EmiRenderHelper.getAmountText(this.cost.ingredient, adjusted);
         }

         if (!this.remainder && BoM.craftingMode) {
            long amount = this.alreadyDone;
            if (amount < adjusted) {
               Component amountText = (Component)(amount == 0L ? EmiPort.literal("0") : EmiRenderHelper.getAmountText(this.cost.ingredient, amount));
               MutableComponent text = EmiPort.append(EmiPort.literal("", ChatFormatting.RED), amountText);
               text = EmiPort.append(text, EmiPort.literal("/"));
               return EmiPort.append(text, totalText);
            }
         }

         return totalText;
      }
   }

   private class Hover {
      public EmiIngredient stack;
      public MaterialNode node;
      public MaterialNode resolve;
      public EmiRecipeCategory category;

      public Hover(EmiIngredient stack) {
         this.stack = stack;
      }

      public Hover(EmiIngredient stack, MaterialNode node, MaterialNode resolve) {
         this.stack = stack;
         this.node = node;
         this.resolve = resolve;
      }

      public Hover(EmiRecipeCategory category, MaterialNode node) {
         this.category = category;
         this.node = node;
      }

      public Hover(MaterialNode node) {
         this.node = node;
      }

      public boolean drawTooltip(Screen screen, EmiDrawContext context, int mouseX, int mouseY) {
         if (this.stack != null) {
            List<ClientTooltipComponent> list = Lists.newArrayList();
            list.addAll(this.stack.getTooltip());
            if (EmiInput.isShiftDown()) {
               BoMScreen.this.getAutoResolutions(this, (stack, recipe) -> {
                  if (this.node != null && recipe == this.node.recipe) {
                     list.add(new RecipeTooltipComponent(recipe));
                  } else {
                     list.add(new RecipeTooltipComponent(recipe, 1149829034));
                  }
               });
            } else if (this.node != null && this.node.recipe != null) {
               list.add(new RecipeTooltipComponent(this.node.recipe));
            }

            if (this.node != null) {
               if (this.node.consumeChance != 1.0F) {
                  list.add(EmiTooltip.chance("consume", this.node.consumeChance));
               } else if (this.resolve != null && this.resolve.consumeChance != 1.0F) {
                  list.add(EmiTooltip.chance("consume", this.resolve.consumeChance));
               }

               if (this.node.produceChance != 1.0F) {
                  list.add(EmiTooltip.chance("produce", this.node.produceChance));
               }
            }

            EmiRenderHelper.drawTooltip(screen, context, list, mouseX, mouseY);
            return true;
         } else if (this.category != null) {
            EmiRenderHelper.drawTooltip(screen, context, this.category.getTooltip(), mouseX, mouseY);
            return true;
         } else {
            return false;
         }
      }
   }

   private class Node {
      public BoMScreen.Node parent = null;
      public MaterialNode resolution = null;
      public MaterialNode node;
      public int width;
      public int x;
      public int y;
      public int midOffset;
      public long amount;
      public ChanceState chance;

      public Node(MaterialNode node, long amount, int x, int y, ChanceState chance) {
         this.node = node;
         if (node.recipe != null) {
            this.width = 42;
         } else {
            this.width = 16;
         }

         this.amount = amount;
         this.x = x;
         this.y = y;
         this.chance = chance;
         int tw = EmiRenderHelper.getAmountOverflow(this.getAmountText());
         this.width += tw;
         this.midOffset = tw / -2;
      }

      public void render(EmiDrawContext context, int mouseX, int mouseY, float delta) {
         if (this.parent != null) {
            context.push();
            this.setColor(context, this.parent.node, this.node.consumeChance != 1.0F || this.resolution != null && this.resolution.consumeChance != 1.0F, false);
            int nx = this.x;
            int ny = this.y;
            int px = this.parent.x;
            int py = this.parent.y;
            int off = 19;
            if (this.resolution != null) {
               context.drawTexture(EmiRenderHelper.WIDGETS, this.x - 3, this.y - 19, 9, 192, 7, 7);
               BoMScreen.drawLine(context, nx, this.y - 12, nx, ny - 11);
               BoMScreen.drawLine(context, nx, py + off, nx, this.y - 19);
            } else {
               BoMScreen.drawLine(context, nx, ny - 11, nx, py + off);
            }

            this.setColor(context, this.parent.node, false, false);
            BoMScreen.drawLine(context, px, py + off, nx, py + off);
            context.pop();
         }

         int xo = 0;
         if (this.node.recipe != null) {
            int lx = this.x - this.width / 2;
            int ly = this.y - 11;
            int hx = this.x + this.width / 2;
            int hy = this.y + 10;
            context.push();
            this.setColor(context, this.node, this.node.produceChance != 1.0F, false);
            if (this.node.state != FoldState.EXPANDED) {
               BoMScreen.drawLine(context, this.x, hy + 1, this.x, hy + 3);
            } else {
               BoMScreen.drawLine(context, this.x, hy + 1, this.x, hy + 8);
            }

            boolean hovered = mouseX >= lx && mouseY >= ly && mouseX <= hx && mouseY <= hy;
            this.setColor(context, this.node, this.node.produceChance != 1.0F, hovered);
            BoMScreen.drawLine(context, lx, ly, lx, hy);
            BoMScreen.drawLine(context, hx, ly, hx, hy);
            BoMScreen.drawLine(context, lx, ly, hx, ly);
            BoMScreen.drawLine(context, lx, hy, hx, hy);
            EmiRecipeCategory cat = this.node.recipe.getCategory();
            if (StackBatcher.isEnabled() && EmiRecipeCategoryProperties.getSimplifiedIcon(cat) instanceof StackBatcher.Batchable b) {
               BoMScreen.batcher.render(b, context.raw(), this.x - 18 + this.midOffset, this.y - 8, delta);
            } else {
               cat.renderSimplified(context.raw(), this.x - 18 + this.midOffset, this.y - 8, delta);
            }

            xo = 11;
            context.pop();
         }

         context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         BoMScreen.batcher.render(this.node.ingredient, context.raw(), this.x + xo - 8 + this.midOffset, this.y - 8, 0.0F);
         EmiRenderHelper.renderAmount(context, this.x + xo - 8 + this.midOffset, this.y - 8, this.getAmountText());
      }

      public void setColor(EmiDrawContext context, MaterialNode node, boolean chanced, boolean hovered) {
         context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         if (chanced) {
            context.setColor(0.8F, 0.6F, 0.1F, 1.0F);
         }

         if (BoM.craftingMode) {
            if (node.progress == ProgressState.COMPLETED) {
               context.setColor(0.1F, 0.8F, 0.5F, 1.0F);
            } else if (node.progress == ProgressState.PARTIAL) {
               context.setColor(0.8F, 0.2F, 0.9F, 1.0F);
            }
         }

         if (hovered) {
            context.setColor(0.5F, 0.6F, 1.0F, 1.0F);
         }
      }

      public Component getAmountText() {
         if (this.chance.chanced()) {
            long a = Math.round((float)this.amount * this.chance.chance());
            a = Math.max(a, this.node.amount);
            return EmiPort.append(EmiPort.literal("≈"), EmiRenderHelper.getAmountText(this.node.ingredient, a)).withStyle(ChatFormatting.GOLD);
         } else {
            return EmiRenderHelper.getAmountText(this.node.ingredient, this.amount);
         }
      }

      public BoMScreen.Hover getHover(int mouseX, int mouseY) {
         if (this.resolution != null && mouseX >= this.x - 4 && mouseX < this.x + 4 && mouseY >= this.y - 19 && mouseY < this.y - 11) {
            return BoMScreen.this.new Hover(this.resolution.ingredient, this.resolution, null);
         }

         int imx = mouseX;
         if (this.node.recipe != null) {
            if (mouseX >= this.x - 18 + this.midOffset && mouseX < this.x - 2 + this.midOffset && mouseY >= this.y - 8 && mouseY < this.y + 8) {
               return BoMScreen.this.new Hover(this.node.recipe.getCategory(), this.node);
            }

            imx -= 11;
         }

         if (imx >= this.x - 8 + this.midOffset && imx < this.x + 8 + this.midOffset && mouseY >= this.y - 8 && mouseY < this.y + 8) {
            return BoMScreen.this.new Hover(this.node.ingredient, this.node, this.resolution);
         }

         int lx = this.x - this.width / 2;
         int ly = this.y - 11;
         int hx = this.x + this.width / 2;
         int hy = this.y + 10;
         return mouseX >= lx && mouseY >= ly && mouseX <= hx && mouseY <= hy ? BoMScreen.this.new Hover(this.node) : null;
      }
   }

   private class TreeVolume {
      public List<BoMScreen.TreeVolume.Width> widths = Lists.newArrayList();
      public List<BoMScreen.Node> nodes = Lists.newArrayList();

      public TreeVolume(MaterialNode node, long amount, int y, ChanceState chance) {
         BoMScreen.Node head = BoMScreen.this.new Node(node, amount, 0, y, chance);
         int l = head.width / 2;
         this.widths.add(new BoMScreen.TreeVolume.Width(-l, head.width - l));
         this.nodes.add(head);
      }

      public void addHead(MaterialNode node, long amount, int y, ChanceState chance) {
         int x = (this.getLeft(0) + this.getRight(0)) / 2;
         BoMScreen.Node newNode = BoMScreen.this.new Node(node, amount, x, y, chance);

         for (BoMScreen.Node n : this.nodes) {
            if (n.parent == null) {
               n.parent = newNode;
            }

            n.y += 20;
         }

         int l = newNode.width / 2;
         this.widths.add(0, new BoMScreen.TreeVolume.Width(x - l, x + newNode.width - l));
         this.nodes.add(0, newNode);
      }

      public int getDepth() {
         return this.widths.size();
      }

      public int getMinLeft() {
         int m = this.getLeft(0);

         for (int i = 1; i < this.getDepth(); i++) {
            m = Math.min(m, this.getLeft(i));
         }

         return m;
      }

      public int getMaxRight() {
         int m = this.getRight(0);

         for (int i = 1; i < this.getDepth(); i++) {
            m = Math.max(m, this.getRight(i));
         }

         return m;
      }

      public int getLeft(int depth) {
         return this.widths.get(depth).left;
      }

      public int getRight(int depth) {
         return this.widths.get(depth).right;
      }

      public void addToRight(BoMScreen.TreeVolume other) {
         int rOff = this.getRight(0) - other.getLeft(0) + 8;

         for (int i = 1; i < this.getDepth() && i < other.getDepth(); i++) {
            rOff = Math.max(rOff, this.getRight(i) - other.getLeft(i) + 8);
         }

         for (int i = 0; i < other.getDepth(); i++) {
            if (i < this.getDepth()) {
               this.widths.get(i).right = other.getRight(i) + rOff;
            } else {
               this.widths.add(new BoMScreen.TreeVolume.Width(other.getLeft(i) + rOff, other.getRight(i) + rOff));
            }
         }

         for (BoMScreen.Node node : other.nodes) {
            node.x += rOff;
            this.nodes.add(node);
         }
      }

      private static class Width {
         private int left;
         private int right;

         public Width(int left, int right) {
            this.left = left;
            this.right = right;
         }
      }
   }
}
