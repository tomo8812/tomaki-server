package de.maxhenkel.gravestone.corelib.energy;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyUtils {
   public static int pushEnergy(IEnergyStorage provider, IEnergyStorage receiver, int maxAmount) {
      int energySim = provider.extractEnergy(maxAmount, true);
      int receivedSim = receiver.receiveEnergy(energySim, true);
      int energy = provider.extractEnergy(receivedSim, false);
      receiver.receiveEnergy(energy, false);
      return energy;
   }

   @Nullable
   public static IEnergyStorage getEnergyStorage(Level world, BlockPos pos, Direction side) {
      return (IEnergyStorage)world.getCapability(EnergyStorage.BLOCK, pos, side.getOpposite());
   }

   @Nullable
   public static IEnergyStorage getEnergyStorageOffset(Level world, BlockPos pos, Direction side) {
      return getEnergyStorage(world, pos.relative(side), side.getOpposite());
   }
}
