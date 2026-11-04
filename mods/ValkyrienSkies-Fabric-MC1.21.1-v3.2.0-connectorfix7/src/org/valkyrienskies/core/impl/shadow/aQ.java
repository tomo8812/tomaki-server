package org.valkyrienskies.core.impl.shadow;

public enum aQ {
   DRAFT_6("http://json-schema.org/draft-06/schema#"),
   DRAFT_7("http://json-schema.org/draft-07/schema#"),
   DRAFT_2019_09("https://json-schema.org/draft/2019-09/schema"),
   DRAFT_2020_12("https://json-schema.org/draft/2020-12/schema");

   private final String identifier;

   aQ(String var3) {
      this.identifier = var3;
   }

   public final String a() {
      return this.identifier;
   }
}
