package org.valkyrienskies.core.impl.shadow;

import java.time.DateTimeException;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Arrays;

public final class k extends i implements q {
   private static char a;
   private static char b;
   private static char c;
   private static final k d = new k();
   private static final char e = '+';
   private static final char f = '-';
   private static final char g = 't';
   private static final char h = ' ';
   private static final char i = '.';
   private static final char j = 'Z';
   private static final char k = 'z';
   private static final int[] l = new int[]{100000000, 10000000, 1000000, 100000, 10000, 1000, 100, 10, 1};
   private final j m = new j();

   private k() {
   }

   public static k a() {
      return d;
   }

   public static String a(char[] var0, int var1, h var2) {
      int var3 = 0;
      if (var2 != null) {
         h var4 = var2;
         var3 = var1;
         char[] var6 = var0;
         byte var10000;
         if (var4.equals(org.valkyrienskies.core.impl.shadow.h.a)) {
            var6[var3] = 'Z';
            var10000 = 1;
         } else {
            var6[var3] = (char)(var4.c() < 0 ? 45 : 43);
            org.valkyrienskies.core.impl.shadow.m.a(Math.abs(var4.b), var6, var3 + 1, 2);
            var6[var3 + 3] = ':';
            org.valkyrienskies.core.impl.shadow.m.a(Math.abs(var4.c), var6, var3 + 4, 2);
            var10000 = 6;
         }

         var3 = var10000;
      }

      return new String(var0, 0, var1 + var3);
   }

   private static int b(char[] var0, int var1, h var2) {
      if (var2.equals(org.valkyrienskies.core.impl.shadow.h.a)) {
         var0[var1] = 'Z';
         return 1;
      } else {
         var0[var1] = (char)(var2.c() < 0 ? 45 : 43);
         org.valkyrienskies.core.impl.shadow.m.a(Math.abs(var2.b), var0, var1 + 1, 2);
         var0[var1 + 3] = ':';
         org.valkyrienskies.core.impl.shadow.m.a(Math.abs(var2.c), var0, var1 + 4, 2);
         return 6;
      }
   }

   private static int a(char[] var0) {
      return org.valkyrienskies.core.impl.shadow.m.a(var0, 11, 13);
   }

   private static int b(char[] var0) {
      return org.valkyrienskies.core.impl.shadow.m.a(var0, 14, 16);
   }

   private static int c(char[] var0) {
      return org.valkyrienskies.core.impl.shadow.m.a(var0, 8, 10);
   }

   private a a(char[] var1, int var2, int var3, int var4, int var5, int var6) {
      switch (var1[16]) {
         case '+':
         case '-':
         case 'Z':
         case 'z':
            h var14 = this.a(var1, 16);
            return org.valkyrienskies.core.impl.shadow.a.a(var2, var3, var4, var5, var6, var14);
         case ':':
            char[] var7 = var1;
            var6 = var6;
            var5 = var5;
            var4 = var4;
            var3 = var3;
            var2 = var2;
            k var13 = this;
            int var8;
            if ((var8 = var7.length - 19) == 0) {
               int var22 = f(var7);
               return var13.a(var2, var3, var4, var5, var6, var22, 0, null, 0);
            }

            h var9 = null;
            int var10 = 0;
            int var11 = 0;
            int var12 = var7[19];
            if (var8 != 1 || var12 != 90 && var12 != 122) {
               if (var8 > 0 && var12 == 46) {
                  if ((var8 = org.valkyrienskies.core.impl.shadow.m.a(var7, 20)) != -1) {
                     int var21 = var8 - 20;
                     var12 = var21;
                     var11 = var8;
                     var10 = org.valkyrienskies.core.impl.shadow.m.b(var7, 20, var11);
                     int var10000;
                     switch (var12) {
                        case 1:
                           var10000 = var10 * 100000000;
                           break;
                        case 2:
                           var10000 = var10 * 10000000;
                           break;
                        case 3:
                           var10000 = var10 * 1000000;
                           break;
                        case 4:
                           var10000 = var10 * 100000;
                           break;
                        case 5:
                           var10000 = var10 * 10000;
                           break;
                        case 6:
                           var10000 = var10 * 1000;
                           break;
                        case 7:
                           var10000 = var10 * 100;
                           break;
                        case 8:
                           var10000 = var10 * 10;
                           break;
                        default:
                           var10000 = var10;
                     }

                     var10 = var10000;
                     var11 = var21;
                     var9 = var13.a(var7, var8);
                  } else {
                     a(var7, "No timezone information");
                  }
               } else if (var8 <= 0 || var12 != 43 && var12 != 45) {
                  a(var7, "Unexpected character at position 19");
               } else {
                  var9 = var13.a(var7, 19);
               }
            } else {
               var9 = org.valkyrienskies.core.impl.shadow.h.a;
               b(var7, 19);
            }

            return var13.a(var2, var3, var4, var5, var6, f(var7), var10, var9, var11);
         default:
            this.a(var1, 16, ':', '+', '-', 'Z');
            throw new DateTimeException(new String(var1));
      }
   }

   private void a(char[] var1, int var2, char var3) {
      if (var2 >= var1.length) {
         a(var1, "Unexpected end of input");
      }

      if (var1[var2] != var3) {
         throw new DateTimeException("Expected character " + var3 + " at position " + (var2 + 1) + " '" + new String(var1) + "'");
      }
   }

   private void a(char[] var1, int var2, char... var3) {
      if (var2 >= var1.length) {
         a(var1, "Unexpected end of input");
      }

      boolean var4 = false;
      char[] var5 = var3;
      int var6 = var3.length;

      for (int var7 = 0; var7 < var6; var7++) {
         char var8 = var5[var7];
         if (var1[var2] == var8) {
            var4 = true;
            break;
         }
      }

      if (!var4) {
         throw new DateTimeException("Expected character " + Arrays.toString(var3) + " at position " + (var2 + 1) + " '" + new String(var1) + "'");
      }
   }

   private h a(char[] var1, int var2) {
      int var3;
      var3 = (var3 = var1.length) - var2;
      char var4;
      if ((var4 = var1[var2]) == 90 || var4 == 122) {
         b(var1, var2);
         return org.valkyrienskies.core.impl.shadow.h.a;
      }

      if (var3 != 6) {
         throw new DateTimeException("Invalid timezone offset: " + new String(var1, var2, var3));
      }

      char var7 = var1[var2];
      var4 = org.valkyrienskies.core.impl.shadow.m.a(var1, var2 + 1, var2 + 3);
      int var5 = org.valkyrienskies.core.impl.shadow.m.a(var1, var2 + 4, var2 + 4 + 2);
      if (var7 == '-') {
         var4 = -var4;
         var5 = -var5;
      } else if (var7 != '+') {
         throw new DateTimeException("Invalid character starting at position " + var2 + 1);
      }

      if (var7 == '-' && var4 == 0 && var5 == 0) {
         throw new DateTimeException("Unknown 'Local Offset Convention' date-time not allowed");
      } else {
         return org.valkyrienskies.core.impl.shadow.h.a(var4, var5);
      }
   }

   private static void b(char[] var0, int var1) {
      if (var0.length > var1 + 1) {
         throw new DateTimeException("Trailing junk data after position " + (var1 + 1) + ": " + new String(var0));
      }
   }

   @Override
   public final String a(OffsetDateTime var1, int var2) {
      return this.a(var1, ZoneOffset.UTC, org.valkyrienskies.core.impl.shadow.b.SECOND, var2);
   }

   @Override
   public final String a(OffsetDateTime var1, b var2) {
      return this.a(var1, ZoneOffset.UTC, var2, 0);
   }

   @Override
   public final String a(OffsetDateTime var1, ZoneOffset var2, int var3) {
      return this.a(var1, var2, org.valkyrienskies.core.impl.shadow.b.NANO, var3);
   }

   public String a(OffsetDateTime var1, ZoneOffset var2, b var3, int var4) {
      a(var4);
      OffsetDateTime var5 = var1;
      if (!var1.getOffset().equals(var2)) {
         var5 = var1.atZoneSameInstant(var2).toOffsetDateTime();
      }

      h var6 = org.valkyrienskies.core.impl.shadow.h.a(var2);
      char[] var7 = new char[31];
      if (a(var3, var7, var5.getYear(), 0, 4, org.valkyrienskies.core.impl.shadow.b.YEAR)) {
         b var11 = org.valkyrienskies.core.impl.shadow.b.YEAR;
         return a(var7, org.valkyrienskies.core.impl.shadow.b.YEAR.requiredLength, (h)null);
      } else {
         var7[4] = '-';
         if (a(var3, var7, var5.getMonthValue(), 5, 2, org.valkyrienskies.core.impl.shadow.b.MONTH)) {
            b var10 = org.valkyrienskies.core.impl.shadow.b.MONTH;
            return a(var7, org.valkyrienskies.core.impl.shadow.b.MONTH.requiredLength, (h)null);
         } else {
            var7[7] = '-';
            if (a(var3, var7, var5.getDayOfMonth(), 8, 2, org.valkyrienskies.core.impl.shadow.b.DAY)) {
               b var9 = org.valkyrienskies.core.impl.shadow.b.DAY;
               return a(var7, org.valkyrienskies.core.impl.shadow.b.DAY.requiredLength, (h)null);
            } else {
               var7[10] = 'T';
               org.valkyrienskies.core.impl.shadow.m.a(var5.getHour(), var7, 11, 2);
               var7[13] = ':';
               if (a(var3, var7, var5.getMinute(), 14, 2, org.valkyrienskies.core.impl.shadow.b.MINUTE)) {
                  b var8 = org.valkyrienskies.core.impl.shadow.b.MINUTE;
                  return a(var7, org.valkyrienskies.core.impl.shadow.b.MINUTE.requiredLength, var6);
               } else {
                  var7[16] = ':';
                  org.valkyrienskies.core.impl.shadow.m.a(var5.getSecond(), var7, 17, 2);
                  boolean var12;
                  if (var12 = var4 > 0) {
                     var7[19] = '.';
                     a(var7, var4, var5.getNano());
                     return a(var7, var4 + 20, var6);
                  } else {
                     return a(var7, 19, var6);
                  }
               }
            }
         }
      }
   }

   private static boolean a(b var0, char[] var1, int var2, int var3, int var4, b var5) {
      org.valkyrienskies.core.impl.shadow.m.a(var2, var1, var3, var4);
      return var0 == var5;
   }

   private static void a(char[] var0, int var1, int var2) {
      double var3 = l[var1 - 1];
      org.valkyrienskies.core.impl.shadow.m.a((int)(var2 / var3), var0, 20, var1);
   }

   @Override
   public final OffsetDateTime a(String var1) {
      return a(this.b(var1)).e();
   }

   public static a a(a var0) {
      if (!var0.a(org.valkyrienskies.core.impl.shadow.b.SECOND)) {
         throw new DateTimeException("No " + org.valkyrienskies.core.impl.shadow.b.SECOND.name() + " field found");
      } else {
         return var0;
      }
   }

   @Override
   public final String a(OffsetDateTime var1) {
      return this.a(var1, 3);
   }

   @Override
   public final String b(OffsetDateTime var1) {
      return this.a(var1, 6);
   }

   @Override
   public final String c(OffsetDateTime var1) {
      return this.a(var1, 9);
   }

   @Override
   public final String d(OffsetDateTime var1) {
      return this.a(var1, 0);
   }

   @Override
   public final a b(String var1) {
      if (var1 == null) {
         throw new NullPointerException("text cannot be null");
      }

      int var2 = var1.length();
      char[] var8;
      int var3 = org.valkyrienskies.core.impl.shadow.m.a(var8 = var1.toCharArray(), 0, 4);
      if (4 == var2) {
         return org.valkyrienskies.core.impl.shadow.a.a(var3);
      }

      this.a(var8, 4, '-');
      int var4 = org.valkyrienskies.core.impl.shadow.m.a(var8, 5, 7);
      if (7 == var2) {
         return org.valkyrienskies.core.impl.shadow.a.a(var3, var4);
      }

      this.a(var8, 7, '-');
      int var5 = c(var8);
      if (10 == var2) {
         return org.valkyrienskies.core.impl.shadow.a.a(var3, var4, var5);
      }

      this.a(var8, 10, 'T', 't', ' ');
      int var6 = a(var8);
      this.a(var8, 13, ':');
      int var7 = b(var8);
      return 16 == var2 ? org.valkyrienskies.core.impl.shadow.a.a(var3, var4, var5, var6, var7, null) : this.a(var8, var3, var4, var5, var6, var7);
   }

   private static int d(char[] var0) {
      return org.valkyrienskies.core.impl.shadow.m.a(var0, 5, 7);
   }

   private static int e(char[] var0) {
      return org.valkyrienskies.core.impl.shadow.m.a(var0, 0, 4);
   }

   private a a(int var1, int var2, int var3, int var4, int var5, char[] var6) {
      int var7;
      if ((var7 = var6.length - 19) == 0) {
         int var14 = f(var6);
         return this.a(var1, var2, var3, var4, var5, var14, 0, null, 0);
      }

      h var8 = null;
      int var9 = 0;
      int var10 = 0;
      char var11 = var6[19];
      if (var7 != 1 || var11 != 'Z' && var11 != 'z') {
         if (var7 > 0 && var11 == '.') {
            if ((var7 = org.valkyrienskies.core.impl.shadow.m.a(var6, 20)) != -1) {
               int var13 = var7 - 20;
               var9 = b(var6, var7, var13);
               var10 = var13;
               var8 = this.a(var6, var7);
            } else {
               a(var6, "No timezone information");
            }
         } else if (var7 <= 0 || var11 != '+' && var11 != '-') {
            a(var6, "Unexpected character at position 19");
         } else {
            var8 = this.a(var6, 19);
         }
      } else {
         var8 = org.valkyrienskies.core.impl.shadow.h.a;
         b(var6, 19);
      }

      return this.a(var1, var2, var3, var4, var5, f(var6), var9, var8, var10);
   }

   private a a(int var1, int var2, int var3, int var4, int var5, int var6, int var7, h var8, int var9) {
      if (var6 == 60) {
         YearMonth var10 = YearMonth.of(var1, var2);
         boolean var11;
         if ((var11 = this.m.a(var10)) || var10.isAfter(this.m.a())) {
            int var14 = var4 - var8.c() / 3600;
            int var12 = var5 - var8.c() % 3600 / 60;
            if ((var2 == Month.DECEMBER.getValue() && var3 == 31 || var2 == Month.JUNE.getValue() && var3 == 30) && var14 == 23 && var12 == 59) {
               OffsetDateTime var13 = OffsetDateTime.of(var1, var2, var3, var4, var5, 59, var7, var8.d()).plusSeconds(1L);
               throw new d(var13, var6, var11);
            }
         }
      }

      return var9 > 0
         ? org.valkyrienskies.core.impl.shadow.a.a(var1, var2, var3, var4, var5, var6, var7, var8, var9)
         : org.valkyrienskies.core.impl.shadow.a.a(var1, var2, var3, var4, var5, var6, var8);
   }

   private static void a(char[] var0, String var1) {
      throw new DateTimeException(var1 + ": " + new String(var0));
   }

   private static int f(char[] var0) {
      return org.valkyrienskies.core.impl.shadow.m.a(var0, 17, 19);
   }

   private static int b(char[] var0, int var1, int var2) {
      int var3 = org.valkyrienskies.core.impl.shadow.m.b(var0, 20, var1);
      switch (var2) {
         case 1:
            return var3 * 100000000;
         case 2:
            return var3 * 10000000;
         case 3:
            return var3 * 1000000;
         case 4:
            return var3 * 100000;
         case 5:
            return var3 * 10000;
         case 6:
            return var3 * 1000;
         case 7:
            return var3 * 100;
         case 8:
            return var3 * 10;
         default:
            return var3;
      }
   }
}
