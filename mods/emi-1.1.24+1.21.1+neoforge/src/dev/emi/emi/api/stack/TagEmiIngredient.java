package dev.emi.emi.api.stack;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.render.EmiRender;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.mixin.accessor.ItemRendererAccessor;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.registry.EmiTags;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiTagKey;
import dev.emi.emi.screen.tooltip.EmiTextTooltipWrapper;
import dev.emi.emi.screen.tooltip.RemainderTooltipComponent;
import dev.emi.emi.screen.tooltip.TagTooltipComponent;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix4f;

@Internal
public class TagEmiIngredient implements EmiIngredient {
   private final ResourceLocation id;
   private List<EmiStack> stacks;
   public final TagKey<?> key;
   private final EmiTagKey<?> tagKey;
   private long amount;
   private float chance = 1.0F;

   @Internal
   public TagEmiIngredient(TagKey<?> key, long amount) {
      this(EmiTagKey.of(key), amount);
   }

   @Internal
   public TagEmiIngredient(TagKey<?> key, List<EmiStack> stacks, long amount) {
      this(EmiTagKey.of(key), stacks, amount);
   }

   @Internal
   public TagEmiIngredient(EmiTagKey<?> key, long amount) {
      this(key, EmiTags.getValues(key), amount);
   }

   private TagEmiIngredient(EmiTagKey<?> key, List<EmiStack> stacks, long amount) {
      this.id = key.id();
      this.key = key.raw();
      this.tagKey = key;
      this.stacks = stacks;
      this.amount = amount;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof TagEmiIngredient tag && tag.key.equals(this.key);
   }

   @Override
   public int hashCode() {
      return this.id.hashCode();
   }

   @Override
   public EmiIngredient copy() {
      EmiIngredient stack = new TagEmiIngredient(this.tagKey, this.amount);
      stack.setChance(this.chance);
      return stack;
   }

   @Override
   public List<EmiStack> getEmiStacks() {
      return this.stacks;
   }

   @Override
   public long getAmount() {
      return this.amount;
   }

   @Override
   public EmiIngredient setAmount(long amount) {
      this.amount = amount;
      return this;
   }

   @Override
   public float getChance() {
      return this.chance;
   }

   @Override
   public EmiIngredient setChance(float chance) {
      this.chance = chance;
      return this;
   }

   @Override
   public void render(GuiGraphics draw, int x, int y, float delta, int flags) {
      EmiDrawContext context = EmiDrawContext.wrap(draw);
      Minecraft client = Minecraft.getInstance();
      if ((flags & 1) != 0) {
         if (!this.tagKey.hasCustomModel()) {
            if (this.stacks.size() > 0) {
               this.stacks.get(0).render(context.raw(), x, y, delta, -3);
            }
         } else {
            BakedModel model = EmiAgnos.getBakedTagModel(this.tagKey.getCustomModel());
            context.matrices().pushPose();
            context.matrices().translate(x + 8, y + 8, 150.0F);
            context.matrices().mulPose(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
            context.matrices().scale(16.0F, 16.0F, 16.0F);
            model.getTransforms().getTransform(ItemDisplayContext.GUI).apply(false, context.matrices());
            context.matrices().translate(-0.5F, -0.5F, -0.5F);
            if (!model.usesBlockLight()) {
               Lighting.setupForFlatItems();
            }

            BufferSource immediate = context.raw().bufferSource();
            ((ItemRendererAccessor)client.getItemRenderer())
               .invokeRenderBakedItemModel(
                  model,
                  ItemStack.EMPTY,
                  15728880,
                  OverlayTexture.NO_OVERLAY,
                  context.matrices(),
                  ItemRenderer.getFoilBufferDirect(immediate, Sheets.translucentCullBlockSheet(), true, false)
               );
            immediate.endBatch();
            if (!model.usesBlockLight()) {
               Lighting.setupFor3DItems();
            }

            context.matrices().popPose();
         }
      }

      if ((flags & 2) != 0 && !this.tagKey.isOf(EmiPort.getFluidRegistry())) {
         String count = "";
         if (this.amount != 1L) {
            count = count + this.amount;
         }

         EmiRenderHelper.renderAmount(context, x, y, EmiPort.literal(count));
      }

      if ((flags & 4) != 0) {
         EmiRender.renderTagIcon(this, context.raw(), x, y);
      }

      if ((flags & 8) != 0) {
         EmiRender.renderRemainderIcon(this, context.raw(), x, y);
      }
   }

   @Override
   public List<ClientTooltipComponent> getTooltip() {
      List<ClientTooltipComponent> list = Lists.newArrayList();
      list.add(new EmiTextTooltipWrapper(this, EmiPort.ordered(this.tagKey.getTagName())));
      if (EmiUtil.showAdvancedTooltips()) {
         list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.literal("#" + this.id, ChatFormatting.DARK_GRAY))));
      }

      if (this.tagKey.isOf(EmiPort.getFluidRegistry()) && this.amount > 1L) {
         list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiRenderHelper.getAmountText(this, this.amount))));
      }

      if (EmiConfig.appendModId) {
         String mod = EmiUtil.getModName(this.id.getNamespace());
         list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.literal(mod, ChatFormatting.BLUE, ChatFormatting.ITALIC))));
      }

      list.add(new TagTooltipComponent(this.stacks));

      for (EmiStack stack : this.stacks) {
         if (!stack.getRemainder().isEmpty()) {
            list.add(new RemainderTooltipComponent(this));
            break;
         }
      }

      return list;
   }
}
