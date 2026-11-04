package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public class dn extends ct {
   private static String d = "https://json-schema.org/draft-06/schema";
   private static final String e = "$id";

   @Override
   co a() {
      return new co.a(d)
         .a("$id")
         .b(c)
         .a(dj.a(cU.a.V6))
         .a(Arrays.asList(new cG("$schema"), new cG("$id"), new cG("title"), new cG("description"), new cG("default"), new cG("definitions")))
         .a();
   }
}
