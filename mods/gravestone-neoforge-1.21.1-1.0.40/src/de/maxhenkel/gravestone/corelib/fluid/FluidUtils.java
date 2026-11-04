package de.maxhenkel.gravestone.corelib.fluid;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class FluidUtils {
   public static boolean isFluidHandler(Level world, BlockPos pos, Direction direction) {
      return getFluidHandler(world, pos, direction) != null;
   }

   public static boolean isFluidHandlerOffset(Level world, BlockPos pos, Direction direction) {
      return getFluidHandlerOffset(world, pos, direction) != null;
   }

   @Nullable
   public static IFluidHandler getFluidHandler(Level world, BlockPos pos, Direction direction) {
      return (IFluidHandler)world.getCapability(FluidHandler.BLOCK, pos, direction);
   }

   @Nullable
   public static IFluidHandler getFluidHandlerOffset(Level world, BlockPos pos, Direction direction) {
      return getFluidHandler(world, pos.relative(direction), direction.getOpposite());
   }

   public static FluidStack tryFluidTransfer(
      IFluidHandler fluidDestination, IFluidHandler fluidSource, int maxAmount, boolean doTransfer, @Nullable Fluid filter
   ) {
      FluidStack drainable;
      if (filter == null) {
         drainable = fluidSource.drain(maxAmount, FluidAction.SIMULATE);
      } else {
         drainable = fluidSource.drain(new FluidStack(filter, maxAmount), FluidAction.SIMULATE);
      }

      if (drainable.getAmount() > 0) {
         int fillableAmount = fluidDestination.fill(drainable, FluidAction.SIMULATE);
         if (fillableAmount > 0) {
            if (doTransfer) {
               FluidStack drained;
               if (filter == null) {
                  drained = fluidSource.drain(fillableAmount, FluidAction.EXECUTE);
               } else {
                  drained = fluidSource.drain(new FluidStack(filter, fillableAmount), FluidAction.EXECUTE);
               }

               drained.setAmount(fluidDestination.fill(drained, FluidAction.EXECUTE));
               return drained;
            }

            drainable.setAmount(fillableAmount);
            return drainable;
         }
      }

      return FluidStack.EMPTY;
   }

   public static FluidStack tryFluidTransfer(IFluidHandler fluidDestination, IFluidHandler fluidSource, int maxAmount, boolean doTransfer) {
      return FluidUtil.tryFluidTransfer(fluidDestination, fluidSource, maxAmount, doTransfer);
   }

   public static boolean tryFluidInteraction(Player player, InteractionHand hand, Level world, BlockPos pos) {
      ItemStack stack = player.getItemInHand(hand);
      FluidStack fluidStack = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
      if (!fluidStack.isEmpty() && handleEmpty(world, pos, player, hand)) {
         return true;
      }

      IFluidHandler handler = (IFluidHandler)FluidUtil.getFluidHandler(stack).orElse(null);
      return handler != null && handleFill(world, pos, player, hand);
   }

   public static boolean handleEmpty(LevelAccessor world, BlockPos pos, Player player, InteractionHand hand) {
      if (!(world.getBlockEntity(pos) instanceof IFluidHandler handler)) {
         return false;
      } else {
         IItemHandler inv = new InvWrapper(player.getInventory());
         ItemStack stack = player.getItemInHand(hand);
         FluidActionResult res = FluidUtil.tryEmptyContainerAndStow(stack, handler, inv, Integer.MAX_VALUE, player, true);
         if (res.isSuccess()) {
            player.setItemInHand(hand, res.result);
            return true;
         } else {
            return false;
         }
      }
   }

   public static boolean handleFill(LevelAccessor world, BlockPos pos, Player player, InteractionHand hand) {
      if (!(world.getBlockEntity(pos) instanceof IFluidHandler blockHandler)) {
         return false;
      } else {
         IItemHandler inv = new InvWrapper(player.getInventory());
         ItemStack stack = player.getItemInHand(hand);
         FluidActionResult result = FluidUtil.tryFillContainerAndStow(stack, blockHandler, inv, Integer.MAX_VALUE, player, true);
         if (result.isSuccess()) {
            player.setItemInHand(hand, result.result);
            return true;
         } else {
            return false;
         }
      }
   }
}
