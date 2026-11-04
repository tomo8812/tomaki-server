package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.node.ObjectNode;

@Deprecated
public interface az<M extends aB<?, ?>> extends aA<M> {
   void overrideInstanceAttributes(ObjectNode var1, M var2);

   @Override
   default void overrideInstanceAttributes(ObjectNode jsonSchemaAttributesNode, M member, aI context) {
      this.overrideInstanceAttributes(jsonSchemaAttributesNode, member);
   }
}
