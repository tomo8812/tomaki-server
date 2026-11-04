package org.valkyrienskies.mod.event;

import kotlin.Unit;
import net.minecraft.class_5455;
import org.valkyrienskies.mod.common.hooks.VSGameEvents;

public class RegistryEvents {
   @Deprecated(forRemoval = true)
   public static void onTagsLoaded(Runnable event) {
      VSGameEvents.INSTANCE.getTagsAreLoaded().on(x -> event.run());
   }

   @Deprecated(forRemoval = true)
   public static void tagsAreLoaded(class_5455 registries, boolean client) {
      VSGameEvents.INSTANCE.getTagsAreLoaded().emit(Unit.INSTANCE);
   }

   @Deprecated(forRemoval = true)
   public static void onRegistriesComplete(Runnable event) {
      VSGameEvents.INSTANCE.getRegistriesCompleted().on(x -> event.run());
   }

   @Deprecated(forRemoval = true)
   public static void registriesAreComplete() {
      VSGameEvents.INSTANCE.getRegistriesCompleted().emit(Unit.INSTANCE);
   }
}
