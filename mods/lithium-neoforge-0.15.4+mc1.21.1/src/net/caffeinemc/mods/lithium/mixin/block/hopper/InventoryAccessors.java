package net.caffeinemc.mods.lithium.mixin.block.hopper;

import net.caffeinemc.mods.lithium.api.inventory.LithiumInventory;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

public class InventoryAccessors {
   @Mixin(AbstractFurnaceBlockEntity.class)
   public abstract static class AbstractFurnaceBlockEntityMixin implements LithiumInventory {
      @Accessor("items")
      @Override
      public abstract NonNullList<ItemStack> getInventoryLithium();

      @Accessor("items")
      @Override
      public abstract void setInventoryLithium(NonNullList<ItemStack> var1);
   }

   @Mixin(AbstractMinecartContainer.class)
   public abstract static class AbstractMinecartContainerMixin implements LithiumInventory {
      @Accessor("itemStacks")
      @Override
      public abstract NonNullList<ItemStack> getInventoryLithium();

      @Accessor("itemStacks")
      @Override
      public abstract void setInventoryLithium(NonNullList<ItemStack> var1);
   }

   @Mixin(BarrelBlockEntity.class)
   public abstract static class BarrelBlockEntityMixin implements LithiumInventory {
      @Accessor("items")
      @Override
      public abstract NonNullList<ItemStack> getInventoryLithium();

      @Accessor("items")
      @Override
      public abstract void setInventoryLithium(NonNullList<ItemStack> var1);
   }

   @Mixin(BrewingStandBlockEntity.class)
   public abstract static class BrewingStandBlockEntityMixin implements LithiumInventory {
      @Accessor("items")
      @Override
      public abstract NonNullList<ItemStack> getInventoryLithium();

      @Accessor("items")
      @Override
      public abstract void setInventoryLithium(NonNullList<ItemStack> var1);
   }

   @Mixin(ChestBlockEntity.class)
   public abstract static class ChestBlockEntityMixin implements LithiumInventory {
      @Accessor("items")
      @Override
      public abstract NonNullList<ItemStack> getInventoryLithium();

      @Accessor("items")
      @Override
      public abstract void setInventoryLithium(NonNullList<ItemStack> var1);
   }

   @Mixin(DispenserBlockEntity.class)
   public abstract static class DispenserBlockEntityMixin implements LithiumInventory {
      @Accessor("items")
      @Override
      public abstract NonNullList<ItemStack> getInventoryLithium();

      @Accessor("items")
      @Override
      public abstract void setInventoryLithium(NonNullList<ItemStack> var1);
   }

   @Mixin(HopperBlockEntity.class)
   public abstract static class HopperBlockEntityMixin implements LithiumInventory {
      @Accessor("items")
      @Override
      public abstract NonNullList<ItemStack> getInventoryLithium();

      @Accessor("items")
      @Override
      public abstract void setInventoryLithium(NonNullList<ItemStack> var1);
   }

   @Mixin(ShulkerBoxBlockEntity.class)
   public abstract static class ShulkerBoxBlockEntityMixin implements LithiumInventory {
      @Accessor("itemStacks")
      @Override
      public abstract NonNullList<ItemStack> getInventoryLithium();

      @Accessor("itemStacks")
      @Override
      public abstract void setInventoryLithium(NonNullList<ItemStack> var1);
   }
}
