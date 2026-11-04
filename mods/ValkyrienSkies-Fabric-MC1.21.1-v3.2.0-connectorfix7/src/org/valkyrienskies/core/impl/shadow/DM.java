package org.valkyrienskies.core.impl.shadow;

import kotlin.Deprecated;
import kotlin.ReplaceWith;
import org.valkyrienskies.core.internal.world.chunks.VsiBlockType;

public final class DM implements VsiBlockType {
   public final int a;

   public DM(int var1) {
      this.a = var1;
   }

   public final int a() {
      return this.a;
   }

   @Deprecated(message = "Internal detail, toInt() should be removed from the API", replaceWith = @ReplaceWith(expression = "state", imports = {}))
   @Override
   public final int toInt() {
      return this.a;
   }

   private int b() {
      return this.a;
   }

   private static DM a(int var0) {
      return new DM(var0);
   }

   // $VF: synthetic method
   private static DM a(DM var0, int var1, int var2) {
      if ((var2 & 1) != 0) {
         var1 = var0.a;
      }

      int var3 = var1;
      return new DM(var3);
   }

   @Override
   public final String toString() {
      return "BlockTypeImpl(state=" + this.a + ")";
   }

   @Override
   public final int hashCode() {
      return Integer.hashCode(this.a);
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return !(other instanceof DM var2) ? false : this.a == var2.a;
      }
   }
}
