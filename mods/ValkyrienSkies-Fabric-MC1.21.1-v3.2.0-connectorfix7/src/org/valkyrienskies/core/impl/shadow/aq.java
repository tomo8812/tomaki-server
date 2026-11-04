package org.valkyrienskies.core.impl.shadow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum aq {
   ADD("add"),
   REMOVE("remove"),
   REPLACE("replace"),
   MOVE("move"),
   COPY("copy"),
   TEST("test");

   private static final Map<String, aq> OPS;
   public String rfcName;

   private static Map<String, aq> b() {
      HashMap var0;
      (var0 = new HashMap()).put(ADD.rfcName, ADD);
      var0.put(REMOVE.rfcName, REMOVE);
      var0.put(REPLACE.rfcName, REPLACE);
      var0.put(MOVE.rfcName, MOVE);
      var0.put(COPY.rfcName, COPY);
      var0.put(TEST.rfcName, TEST);
      return Collections.unmodifiableMap(var0);
   }

   aq(String var3) {
      this.rfcName = var3;
   }

   public static aq a(String var0) {
      if (var0 == null) {
         throw new ah("rfcName cannot be null");
      } else {
         aq var1;
         if ((var1 = OPS.get(var0.toLowerCase())) == null) {
            throw new ah("unknown / unsupported operation " + var0);
         } else {
            return var1;
         }
      }
   }

   public final String a() {
      return this.rfcName;
   }

   static {
      HashMap var0;
      (var0 = new HashMap()).put(ADD.rfcName, ADD);
      var0.put(REMOVE.rfcName, REMOVE);
      var0.put(REPLACE.rfcName, REPLACE);
      var0.put(MOVE.rfcName, MOVE);
      var0.put(COPY.rfcName, COPY);
      var0.put(TEST.rfcName, TEST);
      OPS = Collections.unmodifiableMap(var0);
   }
}
