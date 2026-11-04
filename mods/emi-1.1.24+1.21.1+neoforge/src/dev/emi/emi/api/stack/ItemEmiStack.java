package dev.emi.emi.api.stack;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.render.EmiRender;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.StackBatcher;
import dev.emi.emi.screen.tooltip.EmiTextTooltipWrapper;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class ItemEmiStack extends EmiStack implements StackBatcher.Batchable {
   private static final Minecraft client = Minecraft.getInstance();
   private final Item item;
   private final DataComponentPatch componentChanges;
   private boolean unbatchable;

   public ItemEmiStack(ItemStack stack) {
      this(stack, stack.getCount());
   }

   public ItemEmiStack(ItemStack stack, long amount) {
      this(stack.getItem(), stack.getComponentsPatch(), amount);
   }

   public ItemEmiStack(Item item, DataComponentPatch components, long amount) {
      this.item = item;
      this.componentChanges = components;
      this.amount = amount;
   }

   @Override
   public ItemStack getItemStack() {
      return new ItemStack(EmiPort.getItemRegistry().wrapAsHolder(this.item), (int)this.amount, this.componentChanges);
   }

   @Override
   public EmiStack copy() {
      EmiStack e = new ItemEmiStack(this.item, this.componentChanges, this.amount);
      e.setChance(this.chance);
      e.setRemainder(this.getRemainder().copy());
      e.comparison = this.comparison;
      return e;
   }

   @Override
   public boolean isEmpty() {
      return this.amount == 0L || this.item == Items.AIR;
   }

   @Override
   public DataComponentPatch getComponentChanges() {
      return this.componentChanges;
   }

   @Nullable
   @Override
   public <T> T get(DataComponentType<? extends T> type) {
      Optional<? extends T> changedOpt = this.componentChanges.get(type);
      return (T)(changedOpt != null ? changedOpt.orElse(null) : this.item.components().get(type));
   }

   @Override
   public Object getKey() {
      return this.item;
   }

   @Override
   public ResourceLocation getId() {
      return EmiPort.getItemRegistry().getKey(this.item);
   }

   @Override
   public void render(GuiGraphics draw, int x, int y, float delta, int flags) {
      EmiDrawContext context = EmiDrawContext.wrap(draw);
      ItemStack stack = this.getItemStack();
      if ((flags & 1) != 0) {
         Lighting.setupFor3DItems();
         draw.renderFakeItem(stack, x, y);
         draw.renderItemDecorations(client.font, stack, x, y, "");
      }

      if ((flags & 2) != 0) {
         String count = "";
         if (this.amount != 1L) {
            count = count + this.amount;
         }

         EmiRenderHelper.renderAmount(context, x, y, EmiPort.literal(count));
      }

      if ((flags & 8) != 0) {
         EmiRender.renderRemainderIcon(this, context.raw(), x, y);
      }
   }

   @Override
   public boolean isSideLit() {
      return client.getItemRenderer().getModel(this.getItemStack(), null, null, 0).usesBlockLight();
   }

   @Override
   public boolean isUnbatchable() {
      ItemStack stack = this.getItemStack();
      return this.unbatchable
         || stack.hasFoil()
         || stack.isDamaged()
         || !EmiAgnos.canBatch(stack)
         || client.getItemRenderer().getModel(this.getItemStack(), null, null, 0).isCustomRenderer();
   }

   @Override
   public void setUnbatchable() {
      this.unbatchable = true;
   }

   @Override
   public void renderForBatch(MultiBufferSource vcp, GuiGraphics draw, int x, int y, int z, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(draw);
      ItemStack stack = this.getItemStack();
      ItemRenderer ir = client.getItemRenderer();
      BakedModel model = ir.getModel(stack, null, null, 0);
      context.push();

      try {
         context.matrices().translate(x, y, 100.0F + z + (model.isGui3d() ? 50 : 0));
         context.matrices().translate(8.0, 8.0, 0.0);
         context.matrices().scale(16.0F, -16.0F, 16.0F);
         ir.render(stack, ItemDisplayContext.GUI, false, context.matrices(), vcp, 15728880, OverlayTexture.NO_OVERLAY, model);
      } finally {
         context.pop();
      }
   }

   @Override
   public List<Component> getTooltipText() {
      return client.isSameThread()
         ? this.getItemStack().getTooltipLines(TooltipContext.of(client.level), client.player, TooltipFlag.NORMAL)
         : this.getItemStack().getTooltipLines(TooltipContext.of(client.level.registryAccess()), null, TooltipFlag.NORMAL);
   }

   @Override
   public List<ClientTooltipComponent> getTooltip() {
      ItemStack stack = this.getItemStack();
      List<ClientTooltipComponent> list = Lists.newArrayList();
      if (!this.isEmpty()) {
         list.addAll(EmiAgnos.getItemTooltip(stack));
         if (!list.isEmpty() && list.get(0) instanceof ClientTextTooltip ottc) {
            list.set(0, new EmiTextTooltipWrapper(this, ottc));
         }

         list.addAll(super.getTooltip());
      }

      return list;
   }

   @Override
   public Component getName() {
      return (Component)(this.isEmpty() ? EmiPort.literal("") : this.getItemStack().getHoverName());
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public EmiIngredient copy() {
      return this.copy();
   }

   static class ItemEntry {
   }
}
