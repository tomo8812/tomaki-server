package org.valkyrienskies.core.impl.shadow;

public abstract class wJ<PAIR> {
   private Cb a;
   private Cb b;
   private final wK<PAIR> c;

   public wJ(wK<PAIR> var1) {
      this(var1, 0, Integer.MAX_VALUE);
   }

   private wJ(wK<PAIR> var1, int var2, int var3) {
      this.c = var1;
      this.a = new Cb(0, new wJ.a((byte)0));
      this.b = new Cb(Integer.MAX_VALUE, new wJ.b((byte)0));
   }

   public final int h() {
      Cb var1 = this.a;
      return this.a.a;
   }

   public int a() {
      Cb var1 = this.a;
      return this.a.b;
   }

   private int b() {
      Cb var1 = this.b;
      return this.b.a;
   }

   public final int i() {
      Cb var1 = this.b;
      return this.b.b;
   }

   public final wK<PAIR> j() {
      return this.c;
   }

   public PAIR a(wO... var1) {
      this.b(var1);
      this.a.d();
      this.b.d();
      return this.c();
   }

   private PAIR d() {
      this.a.d();
      this.b.d();
      return this.c();
   }

   protected abstract PAIR c();

   public final void k() {
      this.a.c();
   }

   protected final void l() {
      this.b.c();
   }

   public void b(wO... var1) {
      wO[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         wO var4;
         if ((var4 = var5[var3]) instanceof wM) {
            wM var6;
            this.a.a((var6 = (wM)var4).a);
         } else if (var4 instanceof wN) {
            wN var7;
            this.b.a((var7 = (wN)var4).a);
         }
      }
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
