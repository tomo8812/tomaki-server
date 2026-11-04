package org.valkyrienskies.core.impl.shadow;

public abstract class wG<PAIR> implements wP<PAIR> {
   private static final wG.a a = new wG.a((byte)0);
   private static final wG.b b = new wG.b((byte)0);
   private final int c;
   private final int d;
   private final wK<PAIR> e;

   public wG(int var1, int var2, wK<PAIR> var3) {
      this.c = var1;
      this.d = var2;
      this.e = var3;
   }

   @Override
   public final Cb d() {
      return new Cb(this.c, a);
   }

   @Override
   public final Cb e() {
      return new Cb(this.d, b);
   }

   @Override
   public final wK<PAIR> f() {
      return this.e;
   }

   static class a implements Cb.a {
      private a() {
      }

      @Override
      public final void a(int var1) {
         throw new pp(var1);
      }

      // $VF: synthetic method
      a(byte var1) {
         this();
      }
   }

   static class b implements Cb.a {
      private b() {
      }

      @Override
      public final void a(int var1) {
         throw new pq(var1);
      }

      // $VF: synthetic method
      b(byte var1) {
         this();
      }
   }
}
