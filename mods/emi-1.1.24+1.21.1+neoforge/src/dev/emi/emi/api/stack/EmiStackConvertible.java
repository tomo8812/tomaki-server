package dev.emi.emi.api.stack;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public interface EmiStackConvertible {
   @OnlyIn(Dist.CLIENT)
   @Experimental
   default EmiStack emi() {
      throw new IllegalStateException();
   }

   @OnlyIn(Dist.CLIENT)
   @Experimental
   default EmiStack emi(long amount) {
      throw new IllegalStateException();
   }
}
