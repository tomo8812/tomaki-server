package org.embeddedt.modernfix.common.mixin.feature.measure_time;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ProfiledReloadInstance;
import net.minecraft.server.packs.resources.ProfiledReloadInstance.State;
import org.embeddedt.modernfix.util.NamedPreparableResourceListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ProfiledReloadInstance.class)
public class ProfiledReloadInstanceMixin {
   @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private static List<PreparableReloadListener> getWrappedListeners(List<PreparableReloadListener> listeners) {
      List<PreparableReloadListener> newList = new ArrayList<>(listeners.size());

      for (PreparableReloadListener listener : listeners) {
         String className = listener.getClass().getName();
         if (!className.startsWith("net.minecraftforge.") && !className.startsWith("net.neoforged.") && !className.startsWith("net.fabricmc.")) {
            newList.add(new NamedPreparableResourceListener(listener));
         } else {
            newList.add(listener);
         }
      }

      return newList;
   }

   @ModifyVariable(method = "finish", ordinal = 0, argsOnly = true, at = @At("HEAD"))
   private List<State> sortStates(List<State> datapoints) {
      datapoints = new ArrayList<>(datapoints);
      datapoints.sort(Comparator.<State>comparingLong(s -> s.preparationNanos.get() + s.reloadNanos.get()).reversed());
      return datapoints;
   }
}
