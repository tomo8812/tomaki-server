package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

public final class zZ implements Serializable {
   private static final long a = -3845586908418844111L;
   private final SortedMap<Comparable<?>, Long> b;

   public zZ() {
      this.b = new TreeMap<>();
   }

   private zZ(Comparator<?> var1) {
      this.b = new TreeMap<>(var1);
   }

   @Override
   public final String toString() {
      NumberFormat var1 = NumberFormat.getPercentInstance();
      StringBuilder var2;
      (var2 = new StringBuilder()).append("Value \t Freq. \t Pct. \t Cum Pct. \n");

      for (Comparable var4 : this.b.keySet()) {
         var2.append(var4);
         var2.append('\t');
         var2.append(this.b(var4));
         var2.append('\t');
         var2.append(var1.format(this.c(var4)));
         var2.append('\t');
         var2.append(var1.format(this.e(var4)));
         var2.append('\n');
      }

      return var2.toString();
   }

   public final void a(Comparable<?> var1) {
      this.a(var1, 1L);
   }

   private void a(int var1) {
      Long var2 = (long)var1;
      this.a(var2, 1L);
   }

   private void a(long var1) {
      Long var2 = var1;
      this.a(var2, 1L);
   }

   private void a(char var1) {
      Character var2 = var1;
      this.a(var2, 1L);
   }

   public final void a(Comparable<?> var1, long var2) {
      Comparable var4 = var1;
      if (var1 instanceof Integer) {
         var4 = ((Integer)var1).longValue();
      }

      try {
         Long var5;
         if ((var5 = this.b.get(var4)) == null) {
            this.b.put(var4, var2);
         } else {
            this.b.put(var4, var5 + var2);
         }
      } catch (ClassCastException var6) {
         throw new oV(px.INSTANCES_NOT_COMPARABLE_TO_EXISTING_VALUES, var1.getClass().getName());
      }
   }

   private void a(int var1, long var2) {
      this.a(Long.valueOf((long)var1), var2);
   }

   private void a(long var1, long var3) {
      this.a(Long.valueOf(var1), var3);
   }

   private void a(char var1, long var2) {
      this.a(Character.valueOf(var1), var2);
   }

   private void b() {
      this.b.clear();
   }

   private Iterator<Comparable<?>> c() {
      return this.b.keySet().iterator();
   }

   private Iterator<Entry<Comparable<?>, Long>> d() {
      return this.b.entrySet().iterator();
   }

   private long e() {
      long var1 = 0L;
      Iterator var3 = this.b.values().iterator();

      while (var3.hasNext()) {
         var1 += var3.next();
      }

      return var1;
   }

   private long b(Comparable<?> var1) {
      while (var1 instanceof Integer) {
         long var6 = ((Integer)var1).longValue();
         var1 = var6;
         this = this;
      }

      long var2 = 0L;

      try {
         Long var9;
         if ((var9 = this.b.get(var1)) != null) {
            var2 = var9;
         }
      } catch (ClassCastException var8) {
      }

      return var2;
   }

   private long b(int var1) {
      return this.b(Long.valueOf((long)var1));
   }

   private long b(long var1) {
      return this.b(Long.valueOf(var1));
   }

   private long b(char var1) {
      return this.b(Character.valueOf(var1));
   }

   private int f() {
      return this.b.keySet().size();
   }

   private double c(Comparable<?> var1) {
      long var2;
      return (var2 = this.e()) == 0L ? Double.NaN : (double)this.b(var1) / var2;
   }

   private double c(int var1) {
      return this.c(Long.valueOf((long)var1));
   }

   private double c(long var1) {
      return this.c(Long.valueOf(var1));
   }

   private double c(char var1) {
      return this.c(Character.valueOf(var1));
   }

   private long d(Comparable<?> var1) {
      while (this.e() != 0L) {
         if (!(var1 instanceof Integer)) {
            Comparator var2;
            if ((var2 = this.b.comparator()) == null) {
               var2 = new zZ.a((byte)0);
            }

            long var3 = 0L;

            try {
               Long var5;
               if ((var5 = this.b.get(var1)) != null) {
                  var3 = var5;
               }
            } catch (ClassCastException var10) {
               return 0L;
            }

            if (var2.compare(var1, this.b.firstKey()) < 0) {
               return 0L;
            }

            if (var2.compare(var1, this.b.lastKey()) >= 0) {
               return this.e();
            }

            for (Comparable var6 : this.b.keySet()) {
               if (var2.compare(var1, var6) <= 0) {
                  return var3;
               }

               var3 += this.b(var6);
            }

            return var3;
         }

         long var8 = ((Integer)var1).longValue();
         var1 = var8;
         this = this;
      }

      return 0L;
   }

   private long d(int var1) {
      return this.d(Long.valueOf((long)var1));
   }

   private long d(long var1) {
      return this.d(Long.valueOf(var1));
   }

   private long d(char var1) {
      return this.d(Character.valueOf(var1));
   }

   private double e(Comparable<?> var1) {
      long var2;
      return (var2 = this.e()) == 0L ? Double.NaN : (double)this.d(var1) / var2;
   }

   private double e(int var1) {
      return this.e(Long.valueOf((long)var1));
   }

   private double e(long var1) {
      return this.e(Long.valueOf(var1));
   }

   private double e(char var1) {
      return this.e(Character.valueOf(var1));
   }

   public final List<Comparable<?>> a() {
      long var1 = 0L;
      Iterator var3 = this.b.values().iterator();

      while (var3.hasNext()) {
         long var5;
         if ((var5 = (Long)var3.next()) > var1) {
            var1 = var5;
         }
      }

      ArrayList var8 = new ArrayList();
      Iterator var4 = this.b.entrySet().iterator();

      while (var4.hasNext()) {
         Entry var9;
         if ((Long)(var9 = (Entry)var4.next()).getValue() == var1) {
            var8.add(var9.getKey());
         }
      }

      return var8;
   }

   private void a(zZ var1) {
      Ci.a(var1, px.NULL_NOT_ALLOWED);

      for (Entry var2 : var1.b.entrySet()) {
         this.a((Comparable<?>)var2.getKey(), (Long)var2.getValue());
      }
   }

   private void a(Collection<zZ> var1) {
      Ci.a(var1, px.NULL_NOT_ALLOWED);

      for (zZ var2 : var1) {
         zZ var3 = var2;
         var2 = this;
         Ci.a(var3, px.NULL_NOT_ALLOWED);

         for (Entry var4 : var3.b.entrySet()) {
            var2.a((Comparable<?>)var4.getKey(), (Long)var4.getValue());
         }
      }
   }

   @Override
   public final int hashCode() {
      int var1;
      return var1 = 31 + (this.b == null ? 0 : this.b.hashCode());
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof zZ)) {
         return false;
      }

      obj = (zZ)obj;
      if (this.b == null) {
         if (((zZ)obj).b != null) {
            return false;
         }
      } else if (!this.b.equals(((zZ)obj).b)) {
         return false;
      }

      return true;
   }

   static class a<T extends Comparable<T>> implements Serializable, Comparator<Comparable<T>> {
      private static final long a = -3852193713161395148L;

      private a() {
      }

      private static int a(Comparable<T> var0, Comparable<T> var1) {
         return var0.compareTo(var1);
      }

      // $VF: synthetic method
      @Override
      public final int compare(Object var1, Object var2) {
         Comparable var10000 = (Comparable)var1;
         Comparable var3 = (Comparable)var2;
         return var10000.compareTo(var3);
      }

      // $VF: synthetic method
      a(byte var1) {
         this();
      }
   }
}
