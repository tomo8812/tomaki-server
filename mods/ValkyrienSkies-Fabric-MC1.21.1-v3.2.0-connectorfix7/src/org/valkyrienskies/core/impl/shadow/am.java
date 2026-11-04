package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class am {
   public final am.a[] a;
   public static final am b = new am(new am.a[0]);
   private static int c;

   private am(am.a[] var1) {
      this.a = var1;
   }

   public am(List<am.a> var1) {
      this.a = var1.toArray(new am.a[0]);
   }

   public static am a(String var0) {
      StringBuilder var1 = null;
      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 < var0.length(); var3++) {
         char var4 = var0.charAt(var3);
         if (var3 == 0) {
            if (var4 != '/') {
               throw new IllegalArgumentException("Missing leading slash");
            }

            var1 = new StringBuilder();
         } else {
            switch (var4) {
               case '/':
                  var2.add(new am.a(var1.toString()));
                  var1.setLength(0);
                  break;
               case '~':
                  var3++;
                  switch (var0.charAt(var3)) {
                     case '0':
                        var1.append('~');
                        continue;
                     case '1':
                        var1.append('/');
                        continue;
                     default:
                        throw new IllegalArgumentException("Invalid escape sequence ~" + var0.charAt(var3) + " at index " + var3);
                  }
               default:
                  var1.append(var4);
            }
         }
      }

      if (var1 == null) {
         return b;
      }

      var2.add(am.a.a(var1.toString()));
      return new am(var2);
   }

   public final boolean a() {
      return this.a.length == 0;
   }

   final am b(String var1) {
      am.a[] var2;
      (var2 = Arrays.copyOf(this.a, this.a.length + 1))[this.a.length] = new am.a(var1);
      return new am(var2);
   }

   final am a(int var1) {
      return this.b(Integer.toString(var1));
   }

   final int b() {
      return this.a.length;
   }

   @Override
   public final String toString() {
      StringBuilder var1 = new StringBuilder();
      am.a[] var2 = this.a;
      int var3 = this.a.length;

      for (int var4 = 0; var4 < var3; var4++) {
         am.a var5 = var2[var4];
         var1.append('/');
         var1.append(var5);
      }

      return var1.toString();
   }

   public final List<am.a> c() {
      return Arrays.asList((Object[])this.a.clone());
   }

   public final am.a b(int var1) {
      if (var1 >= 0 && var1 < this.a.length) {
         return this.a[var1];
      } else {
         throw new IndexOutOfBoundsException("Illegal index: " + var1);
      }
   }

   public final am.a d() {
      if (this.a()) {
         throw new IllegalStateException("Root pointers contain no reference tokens");
      } else {
         return this.a[this.a.length - 1];
      }
   }

   public final am e() {
      return this.a() ? this : new am(Arrays.copyOf(this.a, this.a.length - 1));
   }

   private void a(int var1, String var2, JsonNode var3) {
      throw new an(var2, new am(Arrays.copyOf(this.a, var1)), var3);
   }

   public final JsonNode a(JsonNode var1) {
      JsonNode var2 = var1;

      for (int var3 = 0; var3 < this.a.length; var3++) {
         am.a var4 = this.a[var3];
         if (var2.isArray()) {
            if (!var4.a()) {
               this.a(var3, "Can't reference field \"" + var4.a + "\" on array", var1);
            }

            if (var4.b() == Integer.MIN_VALUE || var4.b() >= var2.size()) {
               this.a(var3, "Array index " + var4.toString() + " is out of bounds", var1);
            }

            var2 = var2.get(var4.b());
         } else if (var2.isObject()) {
            if (!var2.has(var4.a)) {
               this.a(var3, "Missing field \"" + var4.a + "\"", var1);
            }

            var2 = var2.get(var4.a);
         } else {
            this.a(var3, "Can't reference past scalar value", var1);
         }
      }

      return var2;
   }

   @Override
   public final boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         o = (am)o;
         return Arrays.equals(this.a, ((am)o).a);
      } else {
         return false;
      }
   }

   @Override
   public final int hashCode() {
      return Arrays.hashCode(this.a);
   }

   public static class a {
      String a;
      private transient Integer b = null;
      private static final Pattern c = Pattern.compile("~0");
      private static final Pattern d = Pattern.compile("~1");
      private static final Pattern e = Pattern.compile("~");
      private static final Pattern f = Pattern.compile("/");
      private static final Pattern g = Pattern.compile("-|0|(?:[1-9][0-9]*)");

      public a(String var1) {
         if (var1 == null) {
            throw new IllegalArgumentException("Token can't be null");
         }

         this.a = var1;
      }

      private static String a(Object var0) {
         var0 = var0.toString();
         var0 = d.matcher(var0).replaceAll("/");
         return c.matcher(var0).replaceAll("~");
      }

      private static String b(Object var0) {
         var0 = var0.toString();
         var0 = e.matcher(var0).replaceAll("~0");
         return f.matcher(var0).replaceAll("~1");
      }

      public static am.a a(String var0) {
         if (var0 == null) {
            throw new IllegalArgumentException("Token can't be null");
         }

         var0 = var0.toString();
         var0 = d.matcher(var0).replaceAll("/");
         return new am.a(c.matcher(var0).replaceAll("~"));
      }

      public final boolean a() {
         if (this.b != null) {
            return true;
         } else {
            Matcher var1;
            if ((var1 = g.matcher(this.a)).matches()) {
               this.b = var1.group().equals("-") ? Integer.MIN_VALUE : Integer.parseInt(var1.group());
               return true;
            } else {
               return false;
            }
         }
      }

      public final int b() {
         if (!this.a()) {
            throw new IllegalStateException("Object operation on array target");
         } else {
            return this.b;
         }
      }

      public final String c() {
         return this.a;
      }

      @Override
      public final String toString() {
         String var1 = this.a;
         var1 = this.a.toString();
         var1 = e.matcher(var1).replaceAll("~0");
         return f.matcher(var1).replaceAll("~1");
      }

      @Override
      public final boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            o = (am.a)o;
            return this.a.equals(((am.a)o).a);
         } else {
            return false;
         }
      }

      @Override
      public final int hashCode() {
         return this.a.hashCode();
      }
   }
}
