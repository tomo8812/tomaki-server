package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class pu implements Serializable {
   private static final long a = -6024911025449780478L;
   private Throwable b;
   private List<pw> c;
   private List<Object[]> d;
   private Map<String, Object> e;

   public pu(Throwable var1) {
      this.b = var1;
      this.c = new ArrayList<>();
      this.d = new ArrayList<>();
      this.e = new HashMap<>();
   }

   private Throwable c() {
      return this.b;
   }

   public final void a(pw var1, Object... var2) {
      this.c.add(var1);
      this.d.add(ps.a(var2));
   }

   public final void a(String var1, Object var2) {
      this.e.put(var1, var2);
   }

   private Object a(String var1) {
      return this.e.get(var1);
   }

   private Set<String> d() {
      return this.e.keySet();
   }

   public final String a() {
      Locale var2 = Locale.US;
      return this.a(var2, ": ");
   }

   public final String b() {
      Locale var2 = Locale.getDefault();
      return this.a(var2, ": ");
   }

   private String a(Locale var1) {
      return this.a(var1, ": ");
   }

   private String b(Locale var1, String var2) {
      return this.a(var1, var2);
   }

   public String a(Locale var1, String var2) {
      StringBuilder var3 = new StringBuilder();
      int var4 = 0;
      int var5 = this.c.size();

      for (int var6 = 0; var6 < var5; var6++) {
         pw var7 = this.c.get(var6);
         Object[] var8 = this.d.get(var6);
         MessageFormat var9 = new MessageFormat(var7.a(var1), var1);
         var3.append(var9.format(var8));
         if (++var4 < var5) {
            var3.append(var2);
         }
      }

      return var3.toString();
   }

   private void a(ObjectOutputStream var1) {
      var1.writeObject(this.b);
      ObjectOutputStream var3 = var1;
      pu var2 = this;
      int var4 = this.c.size();
      var3.writeInt(var4);

      for (int var5 = 0; var5 < var4; var5++) {
         pw var6 = var2.c.get(var5);
         var3.writeObject(var6);
         Object[] var13;
         int var7 = (var13 = var2.d.get(var5)).length;
         var3.writeInt(var7);

         for (int var8 = 0; var8 < var7; var8++) {
            if (var13[var8] instanceof Serializable) {
               var3.writeObject(var13[var8]);
            } else {
               var3.writeObject(a(var13[var8]));
            }
         }
      }

      var3 = var1;
      var2 = this;
      var4 = this.e.size();
      var3.writeInt(var4);

      for (Entry var14 : var2.e.entrySet()) {
         var3.writeObject(var14.getKey());
         Object var15;
         if ((var15 = var14.getValue()) instanceof Serializable) {
            var3.writeObject(var15);
         } else {
            var3.writeObject(a(var15));
         }
      }
   }

   private void a(ObjectInputStream var1) {
      this.b = (Throwable)var1.readObject();
      ObjectInputStream var3 = var1;
      pu var2 = this;
      int var4 = var3.readInt();
      var2.c = new ArrayList<>(var4);
      var2.d = new ArrayList<>(var4);

      for (int var5 = 0; var5 < var4; var5++) {
         pw var6 = (pw)var3.readObject();
         var2.c.add(var6);
         int var7;
         Object[] var13 = new Object[var7 = var3.readInt()];

         for (int var8 = 0; var8 < var7; var8++) {
            var13[var8] = var3.readObject();
         }

         var2.d.add(var13);
      }

      var3 = var1;
      var2 = this;
      var4 = var3.readInt();
      var2.e = new HashMap<>();

      for (int var12 = 0; var12 < var4; var12++) {
         String var14 = (String)var3.readObject();
         Object var15 = var3.readObject();
         var2.e.put(var14, var15);
      }
   }

   private void b(ObjectOutputStream var1) {
      int var2 = this.c.size();
      var1.writeInt(var2);

      for (int var3 = 0; var3 < var2; var3++) {
         pw var4 = this.c.get(var3);
         var1.writeObject(var4);
         Object[] var7;
         int var5 = (var7 = this.d.get(var3)).length;
         var1.writeInt(var5);

         for (int var6 = 0; var6 < var5; var6++) {
            if (var7[var6] instanceof Serializable) {
               var1.writeObject(var7[var6]);
            } else {
               var1.writeObject(a(var7[var6]));
            }
         }
      }
   }

   private void b(ObjectInputStream var1) {
      int var2 = var1.readInt();
      this.c = new ArrayList<>(var2);
      this.d = new ArrayList<>(var2);

      for (int var3 = 0; var3 < var2; var3++) {
         pw var4 = (pw)var1.readObject();
         this.c.add(var4);
         int var7;
         Object[] var5 = new Object[var7 = var1.readInt()];

         for (int var6 = 0; var6 < var7; var6++) {
            var5[var6] = var1.readObject();
         }

         this.d.add(var5);
      }
   }

   private void c(ObjectOutputStream var1) {
      int var2 = this.e.size();
      var1.writeInt(var2);

      for (Entry var3 : this.e.entrySet()) {
         var1.writeObject(var3.getKey());
         Object var5;
         if ((var5 = var3.getValue()) instanceof Serializable) {
            var1.writeObject(var5);
         } else {
            var1.writeObject(a(var5));
         }
      }
   }

   private void c(ObjectInputStream var1) {
      int var2 = var1.readInt();
      this.e = new HashMap<>();

      for (int var3 = 0; var3 < var2; var3++) {
         String var4 = (String)var1.readObject();
         Object var5 = var1.readObject();
         this.e.put(var4, var5);
      }
   }

   private static String a(Object var0) {
      return "[Object could not be serialized: " + var0.getClass().getName() + "]";
   }
}
