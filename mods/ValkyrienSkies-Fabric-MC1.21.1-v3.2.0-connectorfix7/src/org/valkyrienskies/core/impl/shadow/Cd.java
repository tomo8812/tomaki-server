package org.valkyrienskies.core.impl.shadow;

import java.util.EventObject;

public class Cd extends EventObject {
   private static final long a = 20120128L;
   private final int b;

   public Cd(Object var1, int var2) {
      super(var1);
      this.b = var2;
   }

   private int a() {
      return this.b;
   }
}
