package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public class dl extends ct {
   private static String d = "https://json-schema.org/draft/2020-12/schema";
   private static final String e = "$id";

   @Override
   co a() {
      return new co.a(d)
         .a("$id")
         .b(c)
         .a(dj.a(cU.a.V202012))
         .a(
            Arrays.asList(
               new cG("$schema"),
               new cG("$id"),
               new cG("title"),
               new cG("description"),
               new cG("default"),
               new cG("definitions"),
               new cG("$comment"),
               new cG("$defs"),
               new cG("$anchor"),
               new cG("deprecated"),
               new cG("contentMediaType"),
               new cG("contentEncoding"),
               new cG("examples"),
               new cG("then"),
               new cG("unevaluatedItems")
            )
         )
         .a();
   }
}
