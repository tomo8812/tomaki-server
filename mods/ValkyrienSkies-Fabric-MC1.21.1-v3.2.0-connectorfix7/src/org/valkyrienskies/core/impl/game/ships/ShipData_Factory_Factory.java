package org.valkyrienskies.core.impl.game.ships;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.shadow.Cy;
import org.valkyrienskies.core.impl.shadow.ek;

public final class ShipData_Factory_Factory implements ek<ShipData.Factory> {
   private final Provider<Cy> attachmentHolderFactoryProvider;

   public ShipData_Factory_Factory(Provider<Cy> attachmentHolderFactoryProvider) {
      this.attachmentHolderFactoryProvider = attachmentHolderFactoryProvider;
   }

   public final ShipData.Factory get() {
      return newInstance(this.attachmentHolderFactoryProvider.get());
   }

   public static ShipData_Factory_Factory create(Provider<Cy> attachmentHolderFactoryProvider) {
      return new ShipData_Factory_Factory(attachmentHolderFactoryProvider);
   }

   public static ShipData.Factory newInstance(Cy attachmentHolderFactory) {
      return new ShipData.Factory(attachmentHolderFactory);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }
}
