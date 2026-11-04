package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.node.ObjectNode;

@Deprecated
public interface aS extends aT {
   void a();

   @Override
   default void a(ObjectNode var1, aV var2, bb var3) {
      var3.a();
   }
}
