package net.satisfy.brewery.core.registry;

import java.util.function.Supplier;
import net.satisfy.foundation.flammable.FoundationFlammables;

public class FlammableBlockRegistry {
   public static void init() {
      FoundationFlammables.plant(new Supplier[]{ObjectRegistry.WILD_HOPS});
      FoundationFlammables.wool(
         new Supplier[]{
            ObjectRegistry.DRIED_WHEAT, ObjectRegistry.DRIED_BARLEY, ObjectRegistry.DRIED_CORN, ObjectRegistry.DRIED_OAT, ObjectRegistry.PATTERNED_WOOL
         }
      );
      FoundationFlammables.hay(new Supplier[]{ObjectRegistry.PATTERNED_CARPET_BLOCK});
      FoundationFlammables.wood(
         new Supplier[]{
            ObjectRegistry.BENCH,
            ObjectRegistry.TABLE,
            ObjectRegistry.CABINET,
            ObjectRegistry.DRAWER,
            ObjectRegistry.BAR_COUNTER,
            ObjectRegistry.SIDEBOARD,
            ObjectRegistry.WALL_CABINET,
            ObjectRegistry.WOODEN_BREWINGSTATION,
            ObjectRegistry.BARREL_MAIN,
            ObjectRegistry.BARREL_MAIN_HEAD,
            ObjectRegistry.BARREL_RIGHT,
            ObjectRegistry.BARREL_HEAD_RIGHT
         }
      );
   }
}
