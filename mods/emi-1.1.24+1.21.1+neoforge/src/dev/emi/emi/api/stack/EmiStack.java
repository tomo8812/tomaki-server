package dev.emi.emi.api.stack;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.registry.EmiComparisonDefaults;
import dev.emi.emi.screen.tooltip.RemainderTooltipComponent;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public abstract class EmiStack implements EmiIngredient {
   public static final EmiStack EMPTY = new EmptyEmiStack();
   private EmiStack remainder = EMPTY;
   protected Comparison comparison = Comparison.DEFAULT_COMPARISON;
   protected long amount = 1L;
   protected float chance = 1.0F;

   @Override
   public List<EmiStack> getEmiStacks() {
      return List.of(this);
   }

   public EmiStack getRemainder() {
      return this.remainder;
   }

   public EmiStack setRemainder(EmiStack stack) {
      if (stack == this) {
         stack = stack.copy();
      }

      this.remainder = stack;
      return this;
   }

   public EmiStack comparison(Function<Comparison, Comparison> comparison) {
      this.comparison = comparison.apply(this.comparison);
      return this;
   }

   public EmiStack comparison(Comparison comparison) {
      this.comparison = comparison;
      return this;
   }

   public abstract EmiStack copy();

   @Override
   public abstract boolean isEmpty();

   @Override
   public long getAmount() {
      return this.amount;
   }

   public EmiStack setAmount(long amount) {
      this.amount = amount;
      return this;
   }

   @Override
   public float getChance() {
      return this.chance;
   }

   public EmiStack setChance(float chance) {
      this.chance = chance;
      return this;
   }

   public abstract DataComponentPatch getComponentChanges();

   @Nullable
   public <T> T get(DataComponentType<? extends T> type) {
      Optional<? extends T> opt = this.getComponentChanges().get(type);
      return (T)(opt != null ? opt.orElse(null) : null);
   }

   public <T> T getOrDefault(DataComponentType<? extends T> type, T fallback) {
      T componentValue = this.get(type);
      return componentValue != null ? componentValue : fallback;
   }

   public abstract Object getKey();

   @Nullable
   public <T> T getKeyOfType(Class<T> clazz) {
      Object o = this.getKey();
      return (T)(clazz.isAssignableFrom(o.getClass()) ? o : null);
   }

   public abstract ResourceLocation getId();

   public ItemStack getItemStack() {
      return ItemStack.EMPTY;
   }

   public boolean isEqual(EmiStack stack) {
      if (!this.getKey().equals(stack.getKey())) {
         return false;
      }

      Comparison a = this.comparison == Comparison.DEFAULT_COMPARISON ? EmiComparisonDefaults.get(this.getKey()) : this.comparison;
      Comparison b = stack.comparison == Comparison.DEFAULT_COMPARISON ? EmiComparisonDefaults.get(stack.getKey()) : stack.comparison;
      return a == b ? a.compare(this, stack) : a.compare(this, stack) && b.compare(this, stack);
   }

   public boolean isEqual(EmiStack stack, Comparison comparison) {
      return this.getKey().equals(stack.getKey()) && comparison.compare(this, stack);
   }

   public abstract List<Component> getTooltipText();

   @Override
   public List<ClientTooltipComponent> getTooltip() {
      List<ClientTooltipComponent> list = Lists.newArrayList();
      if (!this.getRemainder().isEmpty()) {
         list.add(new RemainderTooltipComponent(this));
      }

      return list;
   }

   public abstract Component getName();

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof EmiStack stack) {
         return this.isEqual(stack);
      } else {
         return obj instanceof EmiIngredient stack ? EmiIngredient.areEqual(this, stack) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.getKey().hashCode();
   }

   @Override
   public String toString() {
      String s = this.getKey() + "";
      DataComponentPatch changes = this.getComponentChanges();
      if (changes != DataComponentPatch.EMPTY) {
         s = s + changes;
      }

      return s + " x" + this.getAmount();
   }

   public static EmiStack of(ItemStack stack) {
      return stack.isEmpty() ? EMPTY : new ItemEmiStack(stack);
   }

   public static EmiStack of(ItemStack stack, long amount) {
      return stack.isEmpty() ? EMPTY : new ItemEmiStack(stack, amount);
   }

   public static EmiStack of(ItemLike item) {
      return of(item.asItem().getDefaultInstance(), 1L);
   }

   public static EmiStack of(ItemLike item, long amount) {
      return of(item.asItem().getDefaultInstance(), amount);
   }

   public static EmiStack of(ItemLike item, DataComponentPatch componentChanges) {
      return of(item, componentChanges, 1L);
   }

   public static EmiStack of(ItemLike item, DataComponentPatch componentChanges, long amount) {
      return new ItemEmiStack(item.asItem(), componentChanges, amount);
   }

   public static EmiStack of(Fluid fluid) {
      return of(fluid, EmiPort.emptyExtraData());
   }

   public static EmiStack of(Fluid fluid, long amount) {
      return of(fluid, EmiPort.emptyExtraData(), amount);
   }

   public static EmiStack of(Fluid fluid, DataComponentPatch componentChanges) {
      return of(fluid, componentChanges, 0L);
   }

   public static EmiStack of(Fluid fluid, DataComponentPatch componentChanges, long amount) {
      if (fluid instanceof FlowingFluid ff && ff.getSource() != Fluids.EMPTY) {
         fluid = ff.getSource();
      }

      return fluid == Fluids.EMPTY ? EMPTY : new FluidEmiStack(fluid, componentChanges, amount);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public EmiIngredient setChance(float param1) {
      return this.setChance(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public EmiIngredient setAmount(long param1) {
      return this.setAmount(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public EmiIngredient copy() {
      return this.copy();
   }

   abstract static class Entry<T> {
   }
}
