package org.valkyrienskies.core.impl.shadow;

public enum wD {
   NEITHER(false, false),
   FIRST(true, false),
   LAST(false, true),
   BOTH(true, true);

   final boolean first;
   final boolean last;

   wD(boolean var3, boolean var4) {
      this.first = var3;
      this.last = var4;
   }

   public final boolean a() {
      return this.first;
   }

   public final boolean b() {
      return this.last;
   }
}
