package org.valkyrienskies.core.impl.shadow;

import java.util.Optional;

public final class bi extends bj<Optional> {
   public bi() {
      super(Optional.class);
   }

   @Override
   public final void a(aL var1) {
      super.a(var1);
      var1.c.a(var1x -> this.a(var1x) ? Boolean.TRUE : null);
      var1.d.a(var1x -> this.a(var1x) ? Boolean.TRUE : null);
   }
}
