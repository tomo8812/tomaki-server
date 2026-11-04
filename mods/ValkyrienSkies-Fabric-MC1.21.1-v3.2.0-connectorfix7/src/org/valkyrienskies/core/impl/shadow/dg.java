package org.valkyrienskies.core.impl.shadow;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class dg {
   private String a;
   private String b;
   private String c;
   private String d;
   private String[] e;
   private Map<String, Object> f;
   private String g;

   dg() {
   }

   public String a() {
      return this.b;
   }

   void a(String var1) {
      this.b = var1;
   }

   public String b() {
      return this.c;
   }

   void b(String var1) {
      this.c = var1;
   }

   public String c() {
      return this.d;
   }

   public void c(String var1) {
      this.d = var1;
   }

   public String[] d() {
      return this.e;
   }

   void a(String[] var1) {
      this.e = var1;
   }

   void a(Map<String, Object> var1) {
      this.f = var1;
   }

   public Map<String, Object> e() {
      return this.f;
   }

   public String f() {
      return this.g;
   }

   void d(String var1) {
      this.g = var1;
   }

   @Override
   public String toString() {
      return this.g;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (o != null && this.getClass() == o.getClass()) {
         o = (dg)o;
         if (this.a != null ? this.a.equals(((dg)o).a) : ((dg)o).a == null) {
            if (this.b != null ? this.b.equals(((dg)o).b) : ((dg)o).b == null) {
               if (this.c != null ? this.c.equals(((dg)o).c) : ((dg)o).c == null) {
                  if (this.d != null ? this.d.equals(((dg)o).d) : ((dg)o).d == null) {
                     if (this.f != null ? this.f.equals(((dg)o).f) : ((dg)o).f == null) {
                        if (!Arrays.equals(this.e, ((dg)o).e)) {
                           return false;
                        } else {
                           return this.g != null ? this.g.equals(((dg)o).g) : ((dg)o).g == null;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.a != null ? this.a.hashCode() : 0;
      var1 = var1 * 31 + (this.b != null ? this.b.hashCode() : 0);
      var1 = var1 * 31 + (this.c != null ? this.c.hashCode() : 0);
      var1 = var1 * 31 + (this.d != null ? this.d.hashCode() : 0);
      var1 = var1 * 31 + (this.f != null ? this.f.hashCode() : 0);
      var1 = var1 * 31 + (this.e != null ? Arrays.hashCode(this.e) : 0);
      return var1 * 31 + (this.g != null ? this.g.hashCode() : 0);
   }

   public String g() {
      return this.a;
   }

   public void e(String var1) {
      this.a = var1;
   }

   public static dg a(String var0, ce var1, String var2, String var3, String... var4) {
      dg.a var5;
      (var5 = new dg.a()).b(var1.b_()).c(var2).d(var3).a(var4).a(var1.b()).a(var0).e(var1.c());
      return var5.a();
   }

   public static dg a(String var0, ce var1, String var2, String var3, Map<String, Object> var4) {
      dg.a var5;
      (var5 = new dg.a()).b(var1.b_()).c(var2).d(var3).a(var4).a(var1.b()).a(var0);
      return var5.a();
   }

   public static class a {
      private String a;
      private String b;
      private String c;
      private String d;
      private String[] e;
      private Map<String, Object> f;
      private MessageFormat g;
      private String h;

      public dg.a a(String var1) {
         this.a = var1;
         return this;
      }

      public dg.a b(String var1) {
         this.b = var1;
         return this;
      }

      public dg.a c(String var1) {
         this.c = var1;
         return this;
      }

      public dg.a d(String var1) {
         this.d = var1;
         return this;
      }

      public dg.a a(String... var1) {
         this.e = var1;
         return this;
      }

      public dg.a a(Map<String, Object> var1) {
         this.f = var1;
         return this;
      }

      public dg.a a(MessageFormat var1) {
         this.g = var1;
         return this;
      }

      public dg.a e(String var1) {
         this.h = var1;
         return this;
      }

      public dg a() {
         dg var1;
         (var1 = new dg()).e(this.a);
         var1.a(this.b);
         var1.b(this.c);
         var1.c(this.d);
         var1.a(this.e);
         var1.a(this.f);
         if (this.g != null) {
            String[] var2;
            (var2 = new String[(this.e == null ? 0 : this.e.length) + 1])[0] = this.c;
            if (this.e != null) {
               for (int var3 = 1; var3 < var2.length; var3++) {
                  var2[var3] = this.e[var3 - 1];
               }
            }

            if (StringUtils.isNotBlank(this.h)) {
               var1.d(this.h);
            } else {
               var1.d(this.g.format(var2));
            }
         }

         return var1;
      }
   }
}
