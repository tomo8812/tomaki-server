package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public class dm extends ct {
   String d = "https://json-schema.org/draft-04/schema";
   String e = "id";

   @Override
   co a() {
      return new co.a(this.d)
         .a(this.e)
         .b(c)
         .a(dj.a(cU.a.V4))
         .a(
            Arrays.asList(
               new cG("$schema"), new cG("id"), new cG("title"), new cG("description"), new cG("default"), new cG("definitions"), new cG("exampleSetFlag")
            )
         )
         .a();
   }
}
