package org.valkyrienskies.core.impl.shadow;

@Deprecated
public abstract class yA<FUNC extends kW> extends yz<FUNC> implements xV<FUNC>, xW<FUNC> {
   @Deprecated
   protected yA() {
   }

   protected yA(ya<yp> var1) {
      super(var1);
   }

   @Override
   public final yp a(int var1, FUNC var2, yf var3, double[] var4) {
      return super.a(var1, (FUNC)var2, var3, new yg(var4));
   }

   @Override
   public final yp a(int var1, FUNC var2, yf var3, double[] var4, double[] var5, double[] var6) {
      return super.a(var1, (FUNC)var2, var3, new yg(var4), new yr(var5, var6));
   }
}
