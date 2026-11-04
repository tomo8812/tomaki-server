package net.satisfy.candlelight.core.registry;

import java.util.function.Supplier;
import net.satisfy.candlelight.core.util.Wearables;
import net.satisfy.candlelight.platform.PlatformHelper;
import net.satisfy.foundation.armor.ArmorSet;

public final class ArmorSetRegistry {
   private ArmorSetRegistry() {
   }

   public static void init() {
      ArmorSet.builder("tooltip.candlelight.set.chef")
         .piece(new Supplier[]{ObjectRegistry.COOKING_HAT})
         .piece(new Supplier[]{ObjectRegistry.CHEFS_JACKET})
         .piece(new Supplier[]{ObjectRegistry.CHEFS_PANTS})
         .piece(new Supplier[]{ObjectRegistry.CHEFS_BOOTS})
         .bonus("tooltip.candlelight.set.chef.bonus")
         .bonusActive(Wearables::hasChefSet)
         .tooltipVisible(() -> PlatformHelper.showSetBonusTooltips())
         .register();
      ArmorSet.builder("tooltip.candlelight.set.suit")
         .piece(new Supplier[]{ObjectRegistry.SHIRT, ObjectRegistry.FORMAL_SHIRT})
         .piece(new Supplier[]{ObjectRegistry.TROUSERS_AND_VEST})
         .piece(new Supplier[]{ObjectRegistry.NECKTIE})
         .bonus("tooltip.candlelight.set.elegant.bonus")
         .bonusActive(Wearables::isElegantlyDressed)
         .tooltipVisible(() -> PlatformHelper.showSetBonusTooltips())
         .register();
      ArmorSet.builder("tooltip.candlelight.set.dress")
         .piece(new Supplier[]{ObjectRegistry.DRESS})
         .piece(new Supplier[]{ObjectRegistry.FLOWER_CROWN})
         .bonus("tooltip.candlelight.set.elegant.bonus")
         .bonusActive(Wearables::isElegantlyDressed)
         .tooltipVisible(() -> PlatformHelper.showSetBonusTooltips())
         .register();
   }
}
