package dev.emi.emi.api.stack;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.render.EmiRender;
import dev.emi.emi.api.render.EmiTooltipComponents;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.tooltip.EmiTextTooltipWrapper;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class FluidEmiStack extends EmiStack {
   private final Fluid fluid;
   private final DataComponentPatch componentChanges;

   public FluidEmiStack(Fluid fluid) {
      this(fluid, DataComponentPatch.EMPTY);
   }

   public FluidEmiStack(Fluid fluid, DataComponentPatch componentChanges) {
      this(fluid, componentChanges, 0L);
   }

   public FluidEmiStack(Fluid fluid, DataComponentPatch componentChanges, long amount) {
      this.fluid = fluid;
      this.componentChanges = componentChanges;
      this.amount = amount;
   }

   @Override
   public EmiStack copy() {
      EmiStack e = new FluidEmiStack(this.fluid, this.componentChanges, this.amount);
      e.setChance(this.chance);
      e.setRemainder(this.getRemainder().copy());
      e.comparison = this.comparison;
      return e;
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   @Override
   public DataComponentPatch getComponentChanges() {
      return this.componentChanges;
   }

   @Override
   public Object getKey() {
      return this.fluid;
   }

   @Override
   public ResourceLocation getId() {
      return EmiPort.getFluidRegistry().getKey(this.fluid);
   }

   @Override
   public void render(GuiGraphics raw, int x, int y, float delta, int flags) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      if ((flags & 1) != 0) {
         context.push();
         context.matrices().translate(0.0F, 0.0F, 100.0F);
         EmiAgnos.renderFluid(this, context.matrices(), x, y, delta);
         context.pop();
      }

      if ((flags & 8) != 0) {
         EmiRender.renderRemainderIcon(this, context.raw(), x, y);
      }
   }

   @Override
   public List<Component> getTooltipText() {
      return EmiAgnos.getFluidTooltip(this.fluid, this.componentChanges);
   }

   @Override
   public List<ClientTooltipComponent> getTooltip() {
      List<ClientTooltipComponent> list = Lists.newArrayList();
      List<Component> text = this.getTooltipText();
      if (!text.isEmpty()) {
         list.add(new EmiTextTooltipWrapper(this, EmiPort.ordered(text.get(0))));
      }

      list.addAll(text.stream().skip(1L).map(EmiTooltipComponents::of).collect(Collectors.toList()));
      if (this.amount > 1L) {
         list.add(EmiTooltipComponents.getAmount(this));
      }

      String namespace = EmiPort.getFluidRegistry().getKey(this.fluid).getNamespace();
      EmiTooltipComponents.appendModName(list, namespace);
      list.addAll(super.getTooltip());
      return list;
   }

   @Override
   public Component getName() {
      return EmiAgnos.getFluidName(this.fluid, this.componentChanges);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public EmiIngredient copy() {
      return this.copy();
   }

   static class FluidEntry {
   }
}
