package org.valkyrienskies.core.impl.shadow;

public enum cu {
   OBJECT("object"),
   ARRAY("array"),
   STRING("string"),
   NUMBER("number"),
   INTEGER("integer"),
   BOOLEAN("boolean"),
   NULL("null"),
   ANY("any"),
   UNKNOWN("unknown"),
   UNION("union");

   private String type;

   cu(String var3) {
      this.type = var3;
   }

   @Override
   public final String toString() {
      return this.type;
   }
}
