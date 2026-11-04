package net.satisfy.bakery.core.registry;

import java.util.function.Supplier;
import net.satisfy.foundation.flammable.FoundationFlammables;

public class FlammableBlockRegistry {
   public static void init() {
      FoundationFlammables.wood(
         new Supplier[]{
            ObjectRegistry.CABINET,
            ObjectRegistry.DRAWER,
            ObjectRegistry.WALL_CABINET,
            ObjectRegistry.STREET_SIGN,
            ObjectRegistry.BREADBOX,
            ObjectRegistry.TRAY,
            ObjectRegistry.BREAD_CRATE,
            ObjectRegistry.WALL_DISPLAY
         }
      );
   }
}
