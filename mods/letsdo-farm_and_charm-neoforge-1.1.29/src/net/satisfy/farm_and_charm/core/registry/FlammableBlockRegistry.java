package net.satisfy.farm_and_charm.core.registry;

import java.util.function.Supplier;
import net.satisfy.foundation.flammable.FoundationFlammables;

public class FlammableBlockRegistry {
   public static void init() {
      FoundationFlammables.plant(
         new Supplier[]{
            ObjectRegistry.WILD_RIBWORT,
            ObjectRegistry.WILD_NETTLE,
            ObjectRegistry.WILD_EMMER,
            ObjectRegistry.WILD_CORN,
            ObjectRegistry.WILD_BARLEY,
            ObjectRegistry.WILD_OAT,
            ObjectRegistry.WILD_CARROTS,
            ObjectRegistry.WILD_BEETROOTS,
            ObjectRegistry.WILD_POTATOES,
            ObjectRegistry.WILD_TOMATOES,
            ObjectRegistry.WILD_LETTUCE,
            ObjectRegistry.WILD_ONIONS,
            ObjectRegistry.WILD_STRAWBERRIES,
            ObjectRegistry.CHICKEN_NEST
         }
      );
      FoundationFlammables.wool(
         new Supplier[]{
            ObjectRegistry.STRAWBERRY_BAG,
            ObjectRegistry.CARROT_BAG,
            ObjectRegistry.POTATO_BAG,
            ObjectRegistry.BEETROOT_BAG,
            ObjectRegistry.LETTUCE_BAG,
            ObjectRegistry.TOMATO_BAG,
            ObjectRegistry.CORN_BAG,
            ObjectRegistry.ONION_BAG,
            ObjectRegistry.FLOUR_BAG,
            ObjectRegistry.ROPE_BLOCK,
            ObjectRegistry.FEATHER_PILE
         }
      );
      FoundationFlammables.hay(
         new Supplier[]{
            ObjectRegistry.OAT_BALL,
            ObjectRegistry.BARLEY_BALL,
            ObjectRegistry.SCARECROW,
            ObjectRegistry.STRAW_STABLE_FLOOR,
            ObjectRegistry.WHEAT_PILE,
            ObjectRegistry.DOG_FOOD_BAG,
            ObjectRegistry.CAT_FOOD_BAG
         }
      );
      FoundationFlammables.wood(
         new Supplier[]{
            ObjectRegistry.FEEDING_TROUGH,
            ObjectRegistry.WATER_TROUGH,
            ObjectRegistry.SILO_WOOD,
            ObjectRegistry.CUTTING_BOARD,
            ObjectRegistry.PET_BOWL,
            ObjectRegistry.CHICKEN_COOP,
            ObjectRegistry.ROPE_KNOT,
            ObjectRegistry.CHICKEN_FENCE,
            ObjectRegistry.STABLE_FLOOR,
            ObjectRegistry.TRAMPLED_STABLE_FLOOR,
            ObjectRegistry.TIMBER_WELL
         }
      );
   }
}
