package org.valkyrienskies.core.impl.shadow;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3dc;

public interface Hd {
   int a();

   void a(int var1);

   int b();

   List<CompletableFuture<Void>> a(List<Hd.a> var1, double var2);

   final class a {
      public final He a;
      public final Vector3dc b;

      public a(He var1, Vector3dc var2) {
         Intrinsics.checkNotNullParameter(var1, "");
         Intrinsics.checkNotNullParameter(var2, "");
         super();
         this.a = var1;
         this.b = var2;
      }

      public final He a() {
         return this.a;
      }

      public final Vector3dc b() {
         return this.b;
      }

      private He c() {
         return this.a;
      }

      private Vector3dc d() {
         return this.b;
      }

      private static Hd.a a(He var0, Vector3dc var1) {
         Intrinsics.checkNotNullParameter(var0, "");
         Intrinsics.checkNotNullParameter(var1, "");
         return new Hd.a(var0, var1);
      }

      // $VF: synthetic method
      private static Hd.a a(Hd.a var0, He var1, Vector3dc var2, int var3) {
         if ((var3 & 1) != 0) {
            var1 = var0.a;
         }

         if ((var3 & 2) != 0) {
            var2 = var0.b;
         }

         Vector3dc var5 = var2;
         He var4 = var1;
         Intrinsics.checkNotNullParameter(var1, "");
         Intrinsics.checkNotNullParameter(var5, "");
         return new Hd.a(var4, var5);
      }

      @Override
      public final String toString() {
         return "WorldSimulationTask(world=" + this.a + ", gravity=" + this.b + ')';
      }

      @Override
      public final int hashCode() {
         return this.a.hashCode() * 31 + this.b.hashCode();
      }

      @Override
      public final boolean equals(Object other) {
         if (this == other) {
            return true;
         }

         if (!(other instanceof Hd.a)) {
            return false;
         }

         other = (Hd.a)other;
         return !Intrinsics.areEqual(this.a, ((Hd.a)other).a) ? false : Intrinsics.areEqual(this.b, ((Hd.a)other).b);
      }
   }
}
