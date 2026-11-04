package org.valkyrienskies.core.impl.api_impl.physics.blockstates;

import javax.inject.Provider;
import org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape;
import org.valkyrienskies.core.impl.shadow.ek;

public final class SolidStateImpl_BuilderImpl_Factory implements ek<SolidStateImpl.BuilderImpl> {
   private final Provider<BoxesBlockShape.Builder> newBoxesBlockShapeBuilderProvider;

   public SolidStateImpl_BuilderImpl_Factory(Provider<BoxesBlockShape.Builder> newBoxesBlockShapeBuilderProvider) {
      this.newBoxesBlockShapeBuilderProvider = newBoxesBlockShapeBuilderProvider;
   }

   public final SolidStateImpl.BuilderImpl get() {
      return newInstance(this.newBoxesBlockShapeBuilderProvider);
   }

   public static SolidStateImpl_BuilderImpl_Factory create(Provider<BoxesBlockShape.Builder> newBoxesBlockShapeBuilderProvider) {
      return new SolidStateImpl_BuilderImpl_Factory(newBoxesBlockShapeBuilderProvider);
   }

   public static SolidStateImpl.BuilderImpl newInstance(Provider<BoxesBlockShape.Builder> newBoxesBlockShapeBuilder) {
      return new SolidStateImpl.BuilderImpl(newBoxesBlockShapeBuilder);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }
}
