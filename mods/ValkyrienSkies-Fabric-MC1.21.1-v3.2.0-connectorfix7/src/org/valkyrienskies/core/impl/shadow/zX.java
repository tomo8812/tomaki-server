package org.valkyrienskies.core.impl.shadow;

public final class zX {
   private static final double a = 0.4769362762044697;

   private zX() {
   }

   public static double a(double var0) {
      if (BY.w(var0) > 40.0) {
         return var0 > 0.0 ? 1.0 : -1.0;
      }

      double var2 = zY.a(0.5, var0 * var0, 1.0E-15, 10000);
      return var0 < 0.0 ? -var2 : var2;
   }

   public static double b(double var0) {
      if (BY.w(var0) > 40.0) {
         return var0 > 0.0 ? 0.0 : 2.0;
      }

      double var2 = zY.b(0.5, var0 * var0, 1.0E-15, 10000);
      return var0 < 0.0 ? 2.0 - var2 : var2;
   }

   public static double a(double var0, double var2) {
      if (var0 > var2) {
         return -a(var2, var0);
      } else if (var0 < -0.4769362762044697) {
         return var2 < 0.0 ? b(-var2) - b(-var0) : a(var2) - a(var0);
      } else {
         return var2 > 0.4769362762044697 && var0 > 0.0 ? b(var0) - b(var2) : a(var2) - a(var0);
      }
   }

   public static double c(double var0) {
      double var2;
      double var29;
      if ((var2 = -BY.k((1.0 - var0) * (var0 + 1.0))) < 6.25) {
         var2 -= 3.125;
         var29 = -1.6850591381820166E-19 + var2 * -3.64441206401782E-21;
         var29 = 1.28584807152564E-18 + var29 * var2;
         var29 = 1.1157877678025181E-17 + var29 * var2;
         var29 = -1.333171662854621E-16 + var29 * var2;
         var29 = 2.0972767875968562E-17 + var29 * var2;
         var29 = 6.637638134358324E-15 + var29 * var2;
         var29 = -4.054566272975207E-14 + var29 * var2;
         var29 = -8.151934197605472E-14 + var29 * var2;
         var29 = 2.6335093153082323E-12 + var29 * var2;
         var29 = -1.2975133253453532E-11 + var29 * var2;
         var29 = -5.415412054294628E-11 + var29 * var2;
         var29 = 1.0512122733215323E-9 + var29 * var2;
         var29 = -4.112633980346984E-9 + var29 * var2;
         var29 = -2.9070369957882005E-8 + var29 * var2;
         var29 = 4.2347877827932404E-7 + var29 * var2;
         var29 = -1.3654692000834679E-6 + var29 * var2;
         var29 = -1.3882523362786469E-5 + var29 * var2;
         var29 = 1.8673420803405714E-4 + var29 * var2;
         var29 = -7.40702534166267E-4 + var29 * var2;
         var29 = -0.006033670871430149 + var29 * var2;
         var29 = 0.24015818242558962 + var29 * var2;
         var29 = 1.6536545626831027 + var29 * var2;
      } else if (var2 < 16.0) {
         var2 = BY.a(var2) - 3.25;
         var29 = 9.075656193888539E-8 + var2 * 2.2137376921775787E-9;
         var29 = -2.7517406297064545E-7 + var29 * var2;
         var29 = 1.8239629214389228E-8 + var29 * var2;
         var29 = 1.5027403968909828E-6 + var29 * var2;
         var29 = -4.013867526981546E-6 + var29 * var2;
         var29 = 2.9234449089955446E-6 + var29 * var2;
         var29 = 1.2475304481671779E-5 + var29 * var2;
         var29 = -4.7318229009055734E-5 + var29 * var2;
         var29 = 6.828485145957318E-5 + var29 * var2;
         var29 = 2.4031110387097894E-5 + var29 * var2;
         var29 = -3.550375203628475E-4 + var29 * var2;
         var29 = 9.532893797373805E-4 + var29 * var2;
         var29 = -0.0016882755560235047 + var29 * var2;
         var29 = 0.002491442096107851 + var29 * var2;
         var29 = -0.003751208507569241 + var29 * var2;
         var29 = 0.005370914553590064 + var29 * var2;
         var29 = 1.0052589676941592 + var29 * var2;
         var29 = 3.0838856104922208 + var29 * var2;
      } else if (!Double.isInfinite(var2)) {
         var2 = BY.a(var2) - 5.0;
         var29 = -2.555641816996525E-10 + var2 * -2.7109920616438573E-11;
         var29 = 1.5076572693500548E-9 + var29 * var2;
         var29 = -3.789465440126737E-9 + var29 * var2;
         var29 = 7.61570120807834E-9 + var29 * var2;
         var29 = -1.496002662714924E-8 + var29 * var2;
         var29 = 2.914795345090108E-8 + var29 * var2;
         var29 = -6.771199775845234E-8 + var29 * var2;
         var29 = 2.2900482228026655E-7 + var29 * var2;
         var29 = -9.9298272942317E-7 + var29 * var2;
         var29 = 4.526062597223154E-6 + var29 * var2;
         var29 = -1.968177810553167E-5 + var29 * var2;
         var29 = 7.599527703001776E-5 + var29 * var2;
         var29 = -2.1503011930044477E-4 + var29 * var2;
         var29 = -1.3871931833623122E-4 + var29 * var2;
         var29 = 1.0103004648645344 + var29 * var2;
         var29 = 4.849906401408584 + var29 * var2;
      } else {
         var29 = Double.POSITIVE_INFINITY;
      }

      return var29 * var0;
   }

   public static double d(double var0) {
      return c(1.0 - var0);
   }
}
