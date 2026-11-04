package org.millenaire.content.validate;

import java.util.List;

public interface ValidationRule {
   String id();

   default List<String> ids() {
      return List.of(this.id());
   }

   void check(ContentIndex var1, ValidationContext var2, List<Finding> var3);
}
