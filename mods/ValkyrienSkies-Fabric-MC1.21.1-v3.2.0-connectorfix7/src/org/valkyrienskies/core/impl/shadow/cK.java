package org.valkyrienskies.core.impl.shadow;

import java.util.regex.Pattern;

public class cK implements ci {
   private final String a;
   private final Pattern b;

   public cK(String var1, String var2) {
      this.a = var1;
      this.b = Pattern.compile(var2);
   }

   @Override
   public boolean a(String var1) {
      return this.b.matcher(var1).matches();
   }

   @Override
   public String a() {
      return this.a;
   }

   @Override
   public String b() {
      return this.b.pattern();
   }
}
